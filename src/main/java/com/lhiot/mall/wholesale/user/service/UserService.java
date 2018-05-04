package com.lhiot.mall.wholesale.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.StringReplaceUtil;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import com.sgsl.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final SalesUserService salesUserService;

    @Autowired
    public UserService(UserMapper userMapper, SalesUserService salesUserService,SnowflakeId snowflakeId) {
        this.userMapper = userMapper;
        this.salesUserService = salesUserService;
    }

    public List<User> search(List ids) {
        return userMapper.search(ids);
    }

    public int updateUserStatus(long id) {
        return userMapper.updateUserStatus(id);
    }

    public User user(long id) {
        return userMapper.user(id);
    }

    public User searchUserByOpenid(String openid) {
        return userMapper.searchUserByOpenid(openid);
    }

    public boolean updateUser(User user) {
        return userMapper.updateUser(user) > 0;
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
        userMapper.updateDefaultAddress(userAddress.getUserId());
        userAddress.setIsDefault("yes");
        return userMapper.updateAddress(userAddress)>0;
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
        SalesUser salesUser = salesUserService.findCode(code);
        if (Objects.isNull(salesUser)) {
            throw new ServiceException("不是有效的业务员");
        }
        if (this.updateUser(user)) {
            SalesUserRelation salesUserRelation = new SalesUserRelation();
            salesUserRelation.setUserId(user.getId());
            salesUserRelation.setSalesmanId(salesUser.getId());
            salesUserRelation.setCheck(2);
            if (salesUserService.insertRelation(salesUserRelation) < 1) {
                throw new ServiceException("注册审核提交失败");
            }
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
        Map<String,String> wxUserMap=om.readValue(userStr, Map.class);
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
        LocalDate currentTime = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
        user.setRegisterTime(formatter.format(currentTime));

        String nickname=StringReplaceUtil.replaceByte4(StringReplaceUtil.replaceEmoji(wxUserMap.get("nickname")));
        user.setNickname(nickname);

        user.setSex(wxUserMap.get("sex"));
        user.setProfilePhoto(wxUserMap.get("headimgurl"));

        String address = wxUserMap.get("country") + " " + wxUserMap.get("province") + " " + wxUserMap.get("city");
        user.setAddressDetail(address);

        String unionid = wxUserMap.get("unionid");

        if(StringUtils.isNotBlank(unionid)){
            user.setUnionid(unionid);
        }
        return user;
    }
    public List<User> users(String userName) {
        return userMapper.search(userName);
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
}
