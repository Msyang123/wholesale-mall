package com.lhiot.mall.wholesale.introduction.api;

import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.introduction.domain.Introduction;
import com.lhiot.mall.wholesale.introduction.domain.gridparam.IntroductionGridParam;
import com.lhiot.mall.wholesale.introduction.mapper.IntroductionMapper;
import com.lhiot.mall.wholesale.introduction.service.IntroductionService;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@Api
@Slf4j
@RestController
public class IntroductionApi {

    private final IntroductionService introductionService;

    @Autowired
    public IntroductionApi(IntroductionService introductionService) {
        this.introductionService = introductionService;
    }

    @GetMapping("/introduction/{id}")
    @ApiOperation(value = "根据ID查询服务协议", response = Introduction.class)
    public ResponseEntity<Introduction> introduction(@PathVariable("id") Long id) {
        return ResponseEntity.ok(introductionService.introduction(id));
    }

    @PostMapping("/introduction/grid")
    @ApiOperation(value = "新建一个查询，分页查询服务协议", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) IntroductionGridParam param) {
        return ResponseEntity.ok(introductionService.pageQuery(param));
    }

    @PutMapping("/introduction/{id}")
    @ApiOperation(value = "根据ID修改服务协议", response = Introduction.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody Introduction introduction) {
        if (introductionService.update(introduction)) {
            return ResponseEntity.ok(introduction);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @PostMapping("/introduction/addorupdate")
    @ApiOperation("新增/修改服务协议")
    public ResponseEntity saveAddress(@RequestBody Introduction introduction) {
        if (introductionService.saveOrUpdateIntroduction(introduction)) {
            return ResponseEntity.ok(introduction);
        }
        return ResponseEntity.badRequest().body("添加失败");
    }
}