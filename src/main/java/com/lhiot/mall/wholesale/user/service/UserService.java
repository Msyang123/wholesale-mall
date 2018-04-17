package com.lhiot.mall.wholesale.user.service;

import com.leon.microx.common.redisson.lock.DistributedLock;
import com.lhiot.mall.wholesale.user.model.SearchUser;
import com.lhiot.mall.wholesale.user.pojo.User;
import com.lhiot.mall.wholesale.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @CachePut(value = "user", key = "'user-' + #user.id")
    @DistributedLock(name = "'user-' + #user.id")
    public User save(User user) {
        return repository.save(user);
    }

    @CacheEvict(value = "user", key = "'user-' + #id", condition = "#id >= 1")
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Cacheable(value = "user", key = "'user-' + #id", sync = true) // sync: 多线程访问，只有一个执行到方法
    public User user(long id) {
        return repository.getOne(id);
    }

    @Cacheable(value = "user", sync = true)
    public List<User> users(SearchUser param) {
        List<User> users;
        if (StringUtils.hasLength(param.getLikeName())) {
            users = repository.findByNameLike(param.getLikeName());
        } else if (!ObjectUtils.isEmpty(param.getIds())) {
            users = repository.findAllById(Arrays.asList(param.getIds()));
        } else {
            users = repository.findAll();
        }
        return users;
    }
}
