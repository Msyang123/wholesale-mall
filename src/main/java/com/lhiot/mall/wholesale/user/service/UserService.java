package com.lhiot.mall.wholesale.user.service;

import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.BeanUtils;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final SalesUserService salesUserService;

    @Autowired
    public UserService(UserMapper userMapper, SalesUserService salesUserService) {
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
            salesUserRelation.setIsCheck(2);
            if (salesUserService.insertRelation(salesUserRelation) < 1) {
                throw new ServiceException("注册审核提交失败");
            }
            return true;
        }
        return false;
    }

}
