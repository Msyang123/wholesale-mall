package com.lhiot.mall.wholesale.setting.api;

import java.net.URI;

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
import com.lhiot.mall.wholesale.goods.domain.CategoryTree;
import com.lhiot.mall.wholesale.setting.domain.ParamCategory;
import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.setting.domain.ParamSettingTree;
import com.lhiot.mall.wholesale.setting.domain.gridparam.ParamCategoryGirdParam;
import com.lhiot.mall.wholesale.setting.service.SettingCategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(description ="系统参数配置")
@Slf4j
@RestController
@RequestMapping("/config/category")
public class SettingCategoryApi {

    private final SettingCategoryService settingCategoryService;


    @Autowired
    public SettingCategoryApi( SettingCategoryService settingCategoryService) {
        this.settingCategoryService = settingCategoryService;
    }

    @PostMapping("/setting")
    @ApiOperation(value = "添加", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody ParamCategory param){
    	if(settingCategoryService.create(param)){
    		return ResponseEntity.created(URI.create("/setting/"+param.getId()))
    				.body(param);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "根据ID修改", response = ParamCategory.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody ParamCategory param) {
        if (settingCategoryService.update(param)) {
            return ResponseEntity.ok(param);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id批量删除")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	settingCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询", response = ParamConfig.class)
    public ResponseEntity<ParamCategory> platecategory(@PathVariable("id") Long id) {
        return ResponseEntity.ok(settingCategoryService.paramCategory(id));
    }
    
    @PostMapping("/gird")
    @ApiOperation(value = "新建一个查询，分页查询", response = ArrayObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) ParamCategoryGirdParam param) {
        return ResponseEntity.ok(settingCategoryService.pageQuery(param));
    }
    
    @SuppressWarnings("unchecked")
	@GetMapping("/tree")
    @ApiOperation(value = "查询树结构", response = ArrayObject.class)
    public ResponseEntity<ArrayObject<ParamSettingTree>> grid() {
        return ResponseEntity.ok(ArrayObject.of(settingCategoryService.tree()));
    }

}
