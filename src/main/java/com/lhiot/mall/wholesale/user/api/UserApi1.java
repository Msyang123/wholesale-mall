package com.lhiot.mall.wholesale.user.api;

import com.lhiot.mall.wholesale.user.domain.SearchUser;
import com.lhiot.mall.wholesale.user.domain.User1;
import com.lhiot.mall.wholesale.user.service.UserService1;
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
public class UserApi1 {

    private final UserService1 userService;

    @Autowired
    public UserApi1(UserService1 userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    @ApiOperation(value = "添加用户", response = User1.class)
    public ResponseEntity add(@RequestBody User1 user) {
        if (userService.save(user)) {
            return ResponseEntity.created(URI.create("/user/" + user.getId())).body(user);
        }
        return ResponseEntity.badRequest().body("添加失败");
    }

    @PutMapping("/user/{id}")
    @ApiOperation(value = "根据ID修改用户信息", response = User1.class)
    public ResponseEntity modify(@PathVariable("id") Long id, @RequestBody User1 user) {
        user.setId(id);
        if (userService.save(user)) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().body("修改失败");
    }

    @DeleteMapping("/user/{id}")
    @ApiOperation(value = "根据ID删除一个用户")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    @ApiOperation(value = "根据ID查询一个用户信息", response = User1.class)
    public ResponseEntity<User1> user(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.user(id));
    }

    @PostMapping("/user/search")
    @ApiOperation(value = "新建一个查询，用于返回用户列表", response = User1.class, responseContainer = "List")
    public ResponseEntity<List<User1>> search(@RequestBody(required = false) SearchUser param) {
        return ResponseEntity.ok(userService.users(param));
    }
}
