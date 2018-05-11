package com.lhiot.mall.wholesale.user.mapper;


import java.util.List;

import com.lhiot.mall.wholesale.user.domain.*;
import com.lhiot.mall.wholesale.user.domain.gridparam.UserPerformanceGridParam;
import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.pay.domain.PaymentLog;

@Mapper
public interface UserMapper {

    List<User> search(List ids);

    /*List<User> search(String userName);*/

    Integer updateUserStatus(long id);

    Integer save(User user);

    User user(long id);

    User searchUserByOpenid(String openId);

    Integer updateUser(User user);

    Integer insertAddress(UserAddress userAddress);

    Integer updateAddress(UserAddress userAddress);

    List<UserAddress> searchAddressList(long userId);

    UserAddress userAddress(long id);

    void deleteAddress(long id);

    int updateDefaultAddress(long userId);

    int insertRelation(SalesUserRelation salesUserRelation);

    int updateDefaultAddress();

    //后台管理 根据用户手机号或用户名分页查询用户信息
    List<User> searchByPhoneOrName(User param);


    List<User> searchByPhones(List<String> phones);
    
    List<User> fuzzySearchByPhone(String phone);
    
    List<User> searchInbatch(List<Long> userIds);

    List <PaymentLog> getBalanceRecord(Integer userId);

    UserAddress searchAddressListYes(long userId);

    List<UserAddress> searchAddressListNo(long userId);

    Integer debtFee(long userId);

    //分页查询分类
    List<UserResult> pageQuery(UserGridParam param);
    //查询分类的总记录数
    int pageQueryCount(UserGridParam param);

    List<SalesUserPerformanceDetail> pagePerformanceUserQuery(UserPerformanceGridParam param);

    Integer performanceUserQueryCount(UserPerformanceGridParam param);
}
