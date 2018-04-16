package com.lhiot.mall.wholesale.user.api;

import com.lhiot.mall.wholesale.user.model.SearchUser;
import com.lhiot.mall.wholesale.user.pojo.User;
import com.lhiot.mall.wholesale.user.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Api
@Slf4j
@RestController
public class UserApi {

    private final UserRepository repository;

    @Autowired
    public UserApi(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/user")
    @ApiOperation("添加用户")
    public ResponseEntity<User> add(@RequestBody User user) {
        User pojo = repository.save(user);
        return ResponseEntity.created(URI.create("/user/" + pojo.getId())).body(pojo);
    }

    @PutMapping("/user/{id}")
    @ApiOperation("根据ID修改用户信息")
    public ResponseEntity<User> modify(@PathVariable("id") Long id, @RequestBody User user) {
        user.setId(id);
        User pojo = repository.save(user);
        return ResponseEntity.ok(pojo);
    }

    @DeleteMapping("/user/{id}")
    @ApiOperation("根据ID删除一个用户")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    @ApiOperation("根据ID查询一个用户信息")
    public ResponseEntity<User> user(@PathVariable("id") Long id) {
        Optional<User> pojo = repository.findById(id);
        return ResponseEntity.ok(pojo.orElseGet(User::new));
    }

    @PostMapping("/user/search")
    @ApiOperation("新建一个查询，用于返回用户列表")
    public ResponseEntity<List<User>> search(@RequestBody(required = false) SearchUser param) {
        List<User> users;
        if (StringUtils.hasLength(param.getLikeName())) {
            users = repository.findByNameLike(param.getLikeName());
        } else if (!ObjectUtils.isEmpty(param.getIds())) {
            users = repository.findAllById(Arrays.asList(param.getIds()));
        } else {
            users = repository.findAll();
        }
        return ResponseEntity.ok(users);
    }
}
