package com.lhiot.mall.wholesale.user.mapper;

import com.lhiot.mall.wholesale.user.vo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    int insert(User user);

    int update(User user);

    void remove(long id);

    User select(long id);

    List<User> search(Map<String, Object> where);
}
