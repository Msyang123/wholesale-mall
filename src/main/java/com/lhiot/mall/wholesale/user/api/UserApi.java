package com.lhiot.mall.wholesale.user.api;

import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api
@Slf4j
@RestController
public class UserApi {

    private final UserService userService;

    private final SalesUserService salesUserService;

    @Autowired
    public UserApi(UserService userService, SalesUserService salesUserService) {
        this.userService = userService;
        this.salesUserService = salesUserService;
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

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public ResponseEntity register(@RequestBody User user,@RequestParam String code){
        ResponseEntity responseEntity=null;
        if (user == null) {
            responseEntity = ResponseEntity.ok().body(ResultObject.of("添加完成"));
        }else {
            SalesUser salesUser = salesUserService.searchSalesUserCode(code);
            if (salesUser==null){
                responseEntity = ResponseEntity.ok().body(ResultObject.of("不是有效的业务员"));
            }else{
                if (userService.register(user)>0){
                    SalesUserRelation salesUserRelation = new SalesUserRelation();
                    salesUserRelation.setUserId(user.getId());
                    salesUserRelation.setSalesmanId(salesUser.getId());
                    salesUserRelation.setIsCheck(2);
                    if (salesUserService.insertRelation(salesUserRelation)>0){
                        responseEntity = ResponseEntity.ok().body(ResultObject.of("注册审核提交成功"));
                    }else{
                        responseEntity = ResponseEntity.ok().body(ResultObject.of("注册审核提交失败"));
                    }
                }else{
                    responseEntity = ResponseEntity.ok().body(ResultObject.of("不是有效的用户"));
                }
            }
        }
        return responseEntity;
    }

    @GetMapping("/isSeller/{id}")
    @ApiOperation(value = "查询是否是审核通过的商户")
    public ResponseEntity<SalesUserRelation> isSeller(@PathVariable @NotNull long userId) {
        return ResponseEntity.ok(salesUserService.isSeller(userId));
    }

}
