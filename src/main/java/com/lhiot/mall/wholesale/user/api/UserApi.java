package com.lhiot.mall.wholesale.user.api;

import com.lhiot.mall.wholesale.user.model.SearchUser;
import com.lhiot.mall.wholesale.user.pojo.User;
import com.lhiot.mall.wholesale.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Api
@Slf4j
@RestController
public class UserApi {

    private final UserService userService;

    @Autowired
    public UserApi(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    @ApiOperation("添加用户")
    public ResponseEntity<User> add(@RequestBody User user) {
        User pojo = userService.save(user);
        return ResponseEntity.created(URI.create("/user/" + pojo.getId())).body(pojo);
    }

    @PutMapping("/user/{id}")
    @ApiOperation("根据ID修改用户信息")
    public ResponseEntity<User> modify(@PathVariable("id") Long id, @RequestBody User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.save(user));
    }

    @DeleteMapping("/user/{id}")
    @ApiOperation("根据ID删除一个用户")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    @ApiOperation("根据ID查询一个用户信息")
    public ResponseEntity<User> user(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.user(id));
    }

    @PostMapping("/user/search")
    @ApiOperation("新建一个查询，用于返回用户列表")
    public ResponseEntity<List<User>> search(@RequestBody(required = false) SearchUser param) {
        return ResponseEntity.ok(userService.users(param));
    }
}
