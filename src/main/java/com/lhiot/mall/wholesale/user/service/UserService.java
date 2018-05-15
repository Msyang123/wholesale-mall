package com.lhiot.mall.wholesale.user.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.DateFormatUtil;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.base.StringReplaceUtil;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserPerformanceDetail;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import com.lhiot.mall.wholesale.user.domain.UserGridParam;
import com.lhiot.mall.wholesale.user.domain.UserResult;
import com.lhiot.mall.wholesale.user.domain.gridparam.UserPerformanceGridParam;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.sgsl.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final SalesUserService salesUserService;
    private final PaymentProperties properties;
    private final RestTemplate restTemplate;

    @Autowired
    public UserService(UserMapper userMapper, SalesUserService salesUserService, SnowflakeId snowflakeId, PaymentProperties properties, RestTemplate restTemplate) {
        this.userMapper = userMapper;
        this.salesUserService = salesUserService;
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    public List<User> search(List ids) {
        return userMapper.search(ids);
    }
/*

 */
    public Integer updateUserStatus(long id) {
        return userMapper.updateUserStatus(id);
    }


    public User user(long id) {
        return userMapper.user(id);
    }

    public User searchUserByOpenid(String openid) {
        return userMapper.searchUserByOpenid(openid);
    }

    public boolean updateBalance(User user) {
        return userMapper.updateBalance(user) > 0;
    }

    public boolean saveOrUpdateAddress(UserAddress userAddress) {
        if ("yes".equals(userAddress.getIsDefault())) {
            userMapper.updateDefaultAddress(userAddress.getUserId());
        }
        UserAddress pojo = userMapper.userAddress(userAddress.getId());
        if (Objects.nonNull(pojo)) {
            return userMapper.updateAddress(userAddress) > 0;
        } else {
            userAddress.setIsDefault("no");
            return userMapper.insertAddress(userAddress) > 0;
        }
    }

    public boolean updateDefault(UserAddress userAddress){
        UserAddress address = userMapper.userAddress(userAddress.getId());
        userMapper.updateDefaultAddress(address.getUserId());
        userAddress.setIsDefault("yes");
        return userMapper.updateAddress(userAddress)>0;
    }

    public Boolean updateUser(User user){
        return userMapper.updateUser(user)>0;
    }

    public List<UserAddress> searchAddressList(long userId) {
        return userMapper.searchAddressList(userId);
    }

    public UserAddress userAddress(long id) {
        return userMapper.userAddress(id);
    }

    public void deleteAddress(long id) {
        userMapper.deleteAddress(id);
    }

    public boolean register(User user, String code) {
        String time = DateFormatUtil.format1(new java.util.Date());
        user.setRegisterTime(Timestamp.valueOf(time));
        user.setUserStatus("unaudited");//审核认证中
        SalesUser salesUser = salesUserService.findCode(code);
        if (Objects.isNull(salesUser)) {
            throw new ServiceException("不是有效的业务员");
        }
        if (this.updateUser(user)) {
            //冗余地址
            UserAddress userAddress = new UserAddress();
            userAddress.setPhone(user.getPhone());
            userAddress.setIsDefault("yes");
            userAddress.setContactsName(user.getUserName());
            userAddress.setAddressArea(user.getCity());
            userAddress.setAddressDetail(user.getAddressDetail());
            userAddress.setUserId(user.getId());
            userAddress.setSex(user.getSex());
            this.saveOrUpdateAddress(userAddress);

            SalesUserRelation salesUserRelation = new SalesUserRelation();
            salesUserRelation.setUserId(user.getId());
            salesUserRelation.setSalesmanId(salesUser.getId());
            salesUserRelation.setAuditStatus("unaudited");//待审核
            if (salesUserService.insertRelation(salesUserRelation) < 1) {
                throw new ServiceException("注册审核提交失败");
            }
            //发送短信
            Map<String, Object> body = ImmutableMap.of("phone",salesUser.getSalesmanPhone());
            HttpEntity<Map<String, Object>> request = properties.getSendSms().createRequest(body);
            String messageUrl= MessageFormat.format(properties.getSendSms().getUrl(),"check-reminding",user.getPhone());
            String result=restTemplate.postForObject(messageUrl, request, String.class);
            log.info("result:"+result);
            return true;
        }
        return false;
    }

    /**
     * 微信关注注册用户信息
     * @param user
     * @return
     */
    public int save(User user){
        return userMapper.save(user);
    }
    /**
     * 通过微信返回用户详细信息转换成系统用户
     * @param userStr
     * @return
     */
    public User convert(String userStr) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Map<String,Object> wxUserMap=om.readValue(userStr, Map.class);
        //{    "openid":" OPENID",
        // " nickname": NICKNAME,
        // "sex":"1",
        // "province":"PROVINCE"
        // "city":"CITY",
        // "country":"COUNTRY",
        // "headimgurl":    "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ
        //4eMsv84eavHiaiceqxibJxCfHe/46",
        //"privilege":[ "PRIVILEGE1" "PRIVILEGE2"     ],
        // "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
        //}
        User user=new User();
        user.setRegisterTime(new Timestamp(System.currentTimeMillis()));

        String nickname=StringReplaceUtil.replaceByte4(StringReplaceUtil.replaceEmoji(wxUserMap.get("nickname").toString()));
        user.setNickname(nickname);


        String sex=String.valueOf(wxUserMap.get("sex"));
        switch (sex){
            case "0":
                user.setSex("female");
                break;
            case "1":
                user.setSex("male");
                break;
            default:
                user.setSex("unknown");
                break;
        }

        user.setProfilePhoto(wxUserMap.get("headimgurl").toString());

        String address = wxUserMap.get("country") + " " + wxUserMap.get("province") + " " + wxUserMap.get("city");
        user.setAddressDetail(address);

        String unionid = (String)wxUserMap.get("unionid");

        if(StringUtils.isNotBlank(unionid)){
            user.setUnionid(unionid);
        }

        user.setOpenid(String.valueOf(wxUserMap.get("openid")));
        return user;
    }
    
    /**
     * 根据电话号码模糊查询用户信息
     * @param phone
     * @return
     */
    public List<User> fuzzySearch(String phone){
    	return userMapper.fuzzySearchByPhone(phone);
    }
    
    /**
     * 根据电话号码批量查询用户信息
     * @param phone
     * @return
     */
    public List<User> searchByPhones(List<String> phone){
    	return userMapper.searchByPhones(phone);
    }
    
    /**
     * 根据用户id批量查询用户信息
     * @param userIds
     * @return
     */
    public List<User> users(List<Long> userIds){
    	return userMapper.searchInbatch(userIds);
    }

    public List<PaymentLog> getBalanceRecord(Integer userId){
        return userMapper.getBalanceRecord(userId);
    }

    public UserAddress searchAddressListYes(long userId) {
        return userMapper.searchAddressListYes(userId);
    }

    public List<UserAddress> searchAddressListNO(long userId) {
        return userMapper.searchAddressListNo(userId);
    }

    public Integer debtFee(long userId){
        return userMapper.debtFee(userId);
    }

    //后台管理 根据用户手机号或用户名分页查询用户信息
    public List<User> searchByPhoneOrName(User param) {
        return userMapper.searchByPhoneOrName(param);
    }
    
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(UserGridParam param){
		int count = userMapper.pageQueryCount(param);
		int page = param.getPage();
		int rows = param.getRows();
		//起始行
		param.setStart((page-1)*rows);
		//总记录数
		int totalPages = (count%rows==0?count/rows:count/rows+1);
		if(totalPages < page){
			page = 1;
			param.setPage(page);
			param.setStart(0);
		}
		List<UserResult> activitys = userMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(activitys);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
    public Integer performanceUserQueryCount(UserPerformanceGridParam param){
        return userMapper.performanceUserQueryCount(param);
    }
    public List<SalesUserPerformanceDetail> pagePerformanceUserQuery(UserPerformanceGridParam param){
        return userMapper.pagePerformanceUserQuery(param);
    }
    public List<Long> queryUserId(Map<String, Object> param) {
        return userMapper.queryUserId(param);
    }
    
    //导出用户数据
    public List<UserResult> exportData(UserGridParam param){
    	List<UserResult> activitys = userMapper.pageQuery(param);
    	return activitys;
    }
}
