package com.lhiot.mall.wholesale.setting.api;

import java.net.URI;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.setting.domain.gridparam.ParamConfigGirdParam;
import com.lhiot.mall.wholesale.setting.service.SettingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(description ="系统参数配置")
@Slf4j
@RestController
@RequestMapping("/config")
public class SettingApi {

    private final SettingService settingService;


    @Autowired
    public SettingApi( SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/search/{key}")
    @ApiOperation(value = "根据key查询参数值")
    public ResponseEntity<ParamConfig> invoiceTile(@PathVariable @NotNull String key) {
        return ResponseEntity.ok(settingService.searchConfigParam(key));
    }

    @PostMapping("/paramconfig")
    @ApiOperation(value = "添加", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody ParamConfig paramConfig){
    	if(settingService.create(paramConfig)){
    		return ResponseEntity.created(URI.create("/paramconfig/"+paramConfig.getId()))
    				.body(paramConfig);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/paramconfig/{id}")
    @ApiOperation(value = "根据ID修改", response = ParamConfig.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody ParamConfig paramConfig) {
        if (settingService.update(paramConfig)) {
            return ResponseEntity.ok(paramConfig);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/paramconfig/{id}")
    @ApiOperation(value = "根据id批量删除")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	settingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paramconfig/{id}")
    @ApiOperation(value = "根据ID查询", response = ParamConfig.class)
    public ResponseEntity<ParamConfig> config(@PathVariable("id") Long id){
        return ResponseEntity.ok(settingService.paramConfig(id));
    }
    
    @PostMapping("/paramconfig/gird")
    @ApiOperation(value = "新建一个查询，分页查询", response = ArrayObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) ParamConfigGirdParam param) {
        return ResponseEntity.ok(settingService.pageQuery(param));
    }
    
}
