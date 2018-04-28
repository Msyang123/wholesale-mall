package com.lhiot.mall.wholesale.user.api;

import com.leon.microx.util.ImmutableMap;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(description = "业务员接口")
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

    @GetMapping("/sales/{id}/search/{approved}/sellers")
    @ApiOperation(value = "根据业务员ID查询是否审核通过或者未审核的商户")
    public ResponseEntity<List<User>> isCheck(@PathVariable @NotNull Integer id, @PathVariable Integer approved, @RequestParam Integer page, @RequestParam Integer rows) {
        Map<String, Object> param = ImmutableMap.of("id", id, "isCheck", approved, "START", (page - 1) * rows, "ROWS", rows);
        List<SalesUserRelation> salesUserRelations = salesUserService.selectRelation(param);
        if (salesUserRelations.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List ids = new ArrayList();
        for (SalesUserRelation salesUserRelation : salesUserRelations) {
            ids.add(salesUserRelation.getUserId());
        }
        return ResponseEntity.ok(userService.search(ids));
    }


    @GetMapping("/sales/{id}")
    @ApiOperation(value = "查询业务员信息")
    public ResponseEntity<SalesUser> salesUser(@PathVariable @NotNull long id) {
        return ResponseEntity.ok(salesUserService.searchSalesUser(id));
    }
}
