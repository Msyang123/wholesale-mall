package com.lhiot.mall.wholesale.user.mapper;

import com.lhiot.mall.wholesale.user.domain.User1;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper1 {

    int insert(User1 user);

    int update(User1 user);

    void remove(long id);

    User1 select(long id);

    List<User1> search(Map<String, Object> where);
}
