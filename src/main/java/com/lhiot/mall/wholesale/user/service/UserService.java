package com.lhiot.mall.wholesale.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.DateFormatUtil;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.base.StringReplaceUtil;
import com.lhiot.mall.wholesale.user.domain.*;
import com.lhiot.mall.wholesale.user.domain.gridparam.UserPerformanceGridParam;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.sgsl.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public Integer updateUserStatus(Map param) {
        return userMapper.updateUserStatus(param);
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

    public String register(User user, String code) {
        String time = DateFormatUtil.format1(new java.util.Date());
        user.setRegisterTime(Timestamp.valueOf(time));
        user.setUserStatus("unaudited");//审核认证中
        SalesUser salesUser = salesUserService.findCode(code);
        if (Objects.isNull(salesUser)) {
            return "不是有效的业务员";
        }
        if (this.updateUser(user)) {

            SalesUserRelation salesUserRelation = new SalesUserRelation();
            salesUserRelation.setUserId(user.getId());
            salesUserRelation.setSalesmanId(salesUser.getId());
            salesUserRelation.setAuditStatus("unaudited");//待审核
            if (salesUserService.insertRelation(salesUserRelation) < 1) {
                return "注册审核提交失败";
            }
            //发送短信
            Map<String, Object> body = ImmutableMap.of("phone",salesUser.getSalesmanPhone());

            HttpEntity<Map<String, Object>> request = properties.getSendSms().createRequest(body);
            String messageUrl= MessageFormat.format(properties.getSendSms().getUrl(),"check-reminding",salesUser.getSalesmanPhone());
            String result=restTemplate.postForObject(messageUrl, request, String.class);
            log.info("result:"+result);
            return null;
        }
        return "更新用户信息失败";
    }

    public List<User> searchUser(User user){
        return userMapper.searchUser(user);
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
    
    //导出用户分析数据
    public List<UserResult> exportData(UserGridParam param){
    	return userMapper.exportData(param);
    }
    
    //导出会员数据
    public List<UserResult> exportUsers(UserGridParam param){
    	return userMapper.exportUsers(param);
    }
    
    //后台管理--会员详情
    public UserResult detail(Long userId){
    	return userMapper.searchById(userId);
    }
    
	/**
	 * 分页查询会员
	 * @return
	 */
	public PageQueryObject pageQueryUsers(UserGridParam param){
		int count = userMapper.pageQueryUserCount(param);
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
		List<UserResult> activitys = userMapper.pageQueryUser(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(activitys);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
}
