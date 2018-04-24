package com.lhiot.mall.wholesale.user.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.lhiot.mall.wholesale.user.domain.SalesUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leon.microx.util.ImmutableMap;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api
@Slf4j
@RestController
public class SalesUserApi {

    private final SalesUserService salesUserService;

    private final UserService userService;

    @Autowired
    public SalesUserApi(SalesUserService salesUserService, UserService userService) {
        this.salesUserService = salesUserService;
        this.userService = userService;
    }

    @GetMapping("/userIsCheck/{id}")
    @ApiOperation(value = "根据业务员ID查询是否审核通过或者未审核的商户")
    public ResponseEntity<List<User>> isCheck(@PathVariable @NotNull Integer id, @RequestParam Integer page,
                                              @RequestParam Integer rows, @RequestParam Integer is_check) {
        Map<String,Object> param = ImmutableMap.of("id",id,"isCheck",is_check,"START",(page-1)*rows,"ROWS",rows);
        List<SalesUserRelation> salesUserRelations = salesUserService.selectRelation(param);
        if (salesUserRelations.isEmpty()){
            return ResponseEntity.ok(new ArrayList<>());
        }
        List ids = new ArrayList();
        for (SalesUserRelation salesUserRelation:salesUserRelations) {
            ids.add(salesUserRelation.getUserId());
        }
        return ResponseEntity.ok(userService.search(ids));
    }


    @GetMapping("/salesUser/{id}")
    @ApiOperation(value = "查询业务员信息")
    public ResponseEntity<SalesUser> salesUser(@PathVariable @NotNull long id) {
        return ResponseEntity.ok(salesUserService.searchSalesUser(id));
    }
}
