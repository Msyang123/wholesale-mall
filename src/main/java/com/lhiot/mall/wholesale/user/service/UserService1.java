package com.lhiot.mall.wholesale.user.service;

import com.leon.microx.util.BeanUtils;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.user.mapper.UserMapper1;
import com.lhiot.mall.wholesale.user.domain.SearchUser;
import com.lhiot.mall.wholesale.user.domain.User1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService1 {

    private final SnowflakeId snowflakeId;

    private final UserMapper1 userMapper;

    @Autowired
    public UserService1(UserMapper1 userMapper, SnowflakeId snowflakeId) {
        this.userMapper = userMapper;
        this.snowflakeId = snowflakeId;
    }

    public boolean save(User1 user) {
        if (user.getId() > 0) {
            return userMapper.update(user) > 0;
        } else {
            user.setId(snowflakeId.longId());
            return userMapper.insert(user) > 0;
        }
    }

    public void delete(long id) {
        userMapper.remove(id);
    }

    public User1 user(long id) {
        return userMapper.select(id);
    }

    public List<User1> users(SearchUser param) {
        return userMapper.search(BeanUtils.toMap(param));
    }
}
