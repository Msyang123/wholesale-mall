package com.lhiot.mall.wholesale.user.repository;

import com.lhiot.mall.wholesale.user.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByNameLike(String name);
}
