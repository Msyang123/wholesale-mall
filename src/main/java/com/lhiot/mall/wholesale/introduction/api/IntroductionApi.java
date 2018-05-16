package com.lhiot.mall.wholesale.introduction.api;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.introduction.domain.Introduction;
import com.lhiot.mall.wholesale.introduction.domain.gridparam.IntroductionGridParam;
import com.lhiot.mall.wholesale.introduction.service.IntroductionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.util.Date;

@Api(description = "服务协议接口")
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
    public ResponseEntity<Introduction> detail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(introductionService.introduction(id));
    }

    @PostMapping("/introduction/grid")
    @ApiOperation(value = "后台管理-分页查询服务协议", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) IntroductionGridParam param) {
        return ResponseEntity.ok(introductionService.pageQuery(param));
    }

    @PutMapping("/introduction/addorupdate")
    @ApiOperation("后台管理-新增/修改服务协议")
    public ResponseEntity saveAddress(@RequestBody Introduction introduction) {
        introduction.setCreateTime(new Timestamp(new Date().getTime()));
        if (introductionService.saveOrUpdateIntroduction(introduction)>0) {
            return ResponseEntity.ok(introduction);
        }
        return ResponseEntity.badRequest().body("添加/修改失败");
    }

    @DeleteMapping("/introduction/{id}")
    @ApiOperation(value = "根据id批量删除服务协议")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        introductionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}