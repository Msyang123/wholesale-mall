package com.lhiot.mall.wholesale.user.service;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final SnowflakeId snowflakeId;

    private final UserMapper userMapper;


    @Autowired
    public UserService(SnowflakeId snowflakeId, UserMapper userMapper) {
        this.snowflakeId = snowflakeId;
        this.userMapper = userMapper;
    }


    public List<User> search(List ids) {
        return userMapper.search(ids);
    }

    public int updateUserStatus(long id) {
        return userMapper.updateUserStatus(id);
    }

    public User user(long id){
        return userMapper.user(id);
    }

    public boolean updateUser(User user){
        return userMapper.updateUser(user)>0;
    }

    public boolean saveOrUpdateAddress(UserAddress userAddress){
        if (userAddress.getId()>0){
            return userMapper.updateAddress(userAddress)>0;
        }else {
            return userMapper.insertAddress(userAddress)>0;
        }
    }

    public List<UserAddress> searchAddressList(long userId){
        return userMapper.searchAddressList(userId);
    }

    public UserAddress userAddress(long id){
        return userMapper.userAddress(id);
    }

    public void deleteAddress(long id){
        userMapper.deleteAddress(id);
    }

    public int updateDefaultAddress(){
        return userMapper.updateDefaultAddress();
    }

    public int register(User user){
        return userMapper.register(user);
    }

}
