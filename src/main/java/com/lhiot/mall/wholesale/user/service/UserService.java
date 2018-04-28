package com.lhiot.mall.wholesale.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.user.domain.*;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final SalesUserService salesUserService;
    private final SnowflakeId snowflakeId;

    @Autowired
    public UserService(UserMapper userMapper, SalesUserService salesUserService,SnowflakeId snowflakeId) {
        this.userMapper = userMapper;
        this.salesUserService = salesUserService;
        this.snowflakeId = snowflakeId;
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

    public boolean updateUser(User user) {
        return userMapper.updateUser(user) > 0;
    }

    public boolean saveOrUpdateAddress(UserAddress userAddress) {
        if (userAddress.getIsDefault() == 0) {
            userMapper.updateDefaultAddress(userAddress.getUserId());
        }
        UserAddress pojo = userMapper.userAddress(userAddress.getId());
        if (Objects.nonNull(pojo)) {
            return userMapper.updateAddress(userAddress) > 0;
        } else {
            return userMapper.insertAddress(userAddress) > 0;
        }
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
        SalesUser salesUser = salesUserService.searchSalesUserCode(code);
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
     * 通过微信返回用户详细信息转换成系统用户
     * @param userStr
     * @return
     */
    public User convert(String userStr) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Map<String,String> wxUserMap=om.readValue(userStr, Map.class);

        List<User> users=users(wxUserMap.get("openid"));
        if (users.size()==0)
            return null;
        User returnUser=users.get(0);
        returnUser.setNickname(wxUserMap.get("nickname"));

        //FIXME 相关其余信息等user 全部完善再赋值
        /*

        String unionid = userJson.getString("unionid");
        if (StringUtil.isNull(nickname)) {
            return user;
        }
        if ("byte".equals(this.filterEmoji)) {
            nickname = replaceByte4(nickname);
        }
        if ("regular".equals(this.filterEmoji)) {
            nickname = replaceEmoji(nickname);
        }

        if(StringUtil.isNotNull(nickname)){
            user.set("nickname", nickname);
            logger.debug("=======授权用户：" + nickname + "======");
        }
        if(StringUtil.isNotNull(userJson.get("sex")+"")){
            user.set("sex", userJson.get("sex"));
        }
        if(StringUtil.isNotNull(userJson.get("headimgurl")+"")){
            user.set("user_img_id", userJson.get("headimgurl"));
        }
        String address = userJson.get("country") + " " + userJson.get("province") + " " + userJson.get("city");
        if(StringUtil.isNotNull(address)){
            user.set("user_address", address);
        }

        if(StringUtil.isNotNull(unionid)){
            user.set("union_id", unionid);
        }*/
        return returnUser;
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
}
