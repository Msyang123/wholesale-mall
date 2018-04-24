package com.lhiot.mall.wholesale.user.mapper;

import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> search(List ids);

    int updateUserStatus(long id);

    User user(long id);

    int updateUser(User user);

    int insertAddress(UserAddress userAddress);

    int updateAddress(UserAddress userAddress);

    List<UserAddress> searchAddressList(long userId);

    UserAddress userAddress(long id);

    void deleteAddress(long id);

    int updateDefaultAddress();

    int register(User user);

    int insertRelation(SalesUserRelation salesUserRelation);
}
