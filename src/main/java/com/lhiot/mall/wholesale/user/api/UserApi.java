package com.lhiot.mall.wholesale.user.api;

import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import com.lhiot.mall.wholesale.user.service.UserService;
import com.lhiot.mall.wholesale.user.domain.SearchUser;
import com.lhiot.mall.wholesale.user.domain.User1;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
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

    @GetMapping("/userInfo/{id}")
    @ApiOperation(value = "查询个人信息")
    public ResponseEntity<User> selectUserInfo(@PathVariable @NotNull long id) {
        return ResponseEntity.ok(userService.user(id));
    }

    @PutMapping("/userInfo/update")
    @ApiOperation(value = "修改个人信息")
    public ResponseEntity updateUser(@RequestBody User user){
        if (userService.updateUser(user)){
            return ResponseEntity.ok().body("修改完成");
        }
        return ResponseEntity.badRequest().body("修改失败");
    }

    @PostMapping("/queryAddressList/{userId}")
    @ApiOperation(value = "我的地址列表")
    public ResponseEntity<List<UserAddress>> queryAddressList(@PathVariable @NotNull long userId){
        return ResponseEntity.ok(userService.searchAddressList(userId));
    }

    @GetMapping("/queryAddress/{id}")
    @ApiOperation(value = "根据ID查询地址详情")
    public ResponseEntity<UserAddress> queryAddress(@PathVariable @NotNull long id) {
        return ResponseEntity.ok(userService.userAddress(id));
    }

    @PostMapping("/saveAddress")
    @ApiOperation(value = "新增/修改地址")
    public ResponseEntity addAddress(@RequestBody UserAddress userAddress){
        if (userService.searchAddressList(userAddress.getUserId()).isEmpty()){
            userAddress.setIsDefault(0);//如果只有第一条数据则为默认地址
        }else{
            if(userAddress.getIsDefault()==0){
                userService.updateDefaultAddress();
            }
        }
        if (userService.saveOrUpdateAddress(userAddress)){
            return ResponseEntity.ok().body("添加完成");
        }
        return ResponseEntity.badRequest().body("添加失败");
    }

    @DeleteMapping("/deleteAddress/{id}")
    @ApiOperation(value = "根据ID删除地址")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        userService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }


}
