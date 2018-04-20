package com.lhiot.mall.wholesale.user.service;

import com.leon.microx.util.BeanUtils;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import com.lhiot.mall.wholesale.user.vo.SearchUser;
import com.lhiot.mall.wholesale.user.vo.User;
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
    public UserService(UserMapper userMapper, SnowflakeId snowflakeId) {
        this.userMapper = userMapper;
        this.snowflakeId = snowflakeId;
    }

    public boolean save(User user) {
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

    public User user(long id) {
        return userMapper.select(id);
    }

    public List<User> users(SearchUser param) {
        return userMapper.search(BeanUtils.toMap(param));
    }
}
