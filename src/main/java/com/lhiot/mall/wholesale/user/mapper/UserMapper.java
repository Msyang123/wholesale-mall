package com.lhiot.mall.wholesale.user.mapper;


import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> search(List ids);

    List<User> search(String userName);

    int updateUserStatus(long id);

    User user(long id);

    int updateUser(User user);

    int insertAddress(UserAddress userAddress);

    int updateAddress(UserAddress userAddress);

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

}
