package com.lhiot.mall.wholesale.user.mapper;


import com.lhiot.mall.wholesale.user.domain.*;
import com.lhiot.mall.wholesale.user.domain.gridparam.UserPerformanceGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    List<User> search(List ids);

    /*List<User> search(String userName);*/

    Integer updateUserStatus(Map<String,Object> param);

    Integer save(User user);

    User user(long id);

    User searchUserByOpenid(String openId);

    Integer updateBalance(User user);

    Integer updateUser(User user);

    Integer insertAddress(UserAddress userAddress);

    Integer updateAddress(UserAddress userAddress);

    List<UserAddress> searchAddressList(long userId);

    UserAddress userAddress(long id);

    Integer deleteAddress(long id);

    Integer removeAddressList(List<Long> userIds);

    Integer updateDefaultAddress(long userId);

    Integer insertRelation(SalesUserRelation salesUserRelation);

    Integer updateDefaultAddress();

    //后台管理 根据用户手机号或用户名分页查询用户信息
    List<User> searchByPhoneOrName(User param);

    List<User> searchByPhones(List<String> phones);
    
    List<User> fuzzySearchByPhone(String phone);
    
    List<User> searchInbatch(List<Long> userIds);

    UserAddress searchAddressListYes(long userId);

    List<UserAddress> searchAddressListNo(long userId);

    Integer debtFee(long userId);

    //分页查询分类-会员分析
    List<UserResult> pageQuery(UserGridParam param);
    //查询分类的总记录数-会员分析
    int pageQueryCount(UserGridParam param);

    List<SalesUserPerformanceDetail> pagePerformanceUserQuery(UserPerformanceGridParam param);

    Integer performanceUserQueryCount(UserPerformanceGridParam param);

    List<Long> queryUserId(Map<String, Object> param);
    
    List<UserResult> exportData(UserGridParam param);
    
    //分页查询分类-会员管理
    List<UserResult> pageQueryUser(UserGridParam param);
    //查询分类的总记录数-会员管理
    int pageQueryUserCount(UserGridParam param);
    //会员管理数据导出
    List<UserResult> exportUsers(UserGridParam param);
    //后台管理用户数据详情
    UserResult searchById(Long userId);

    List<User> searchUser(User user);
}
