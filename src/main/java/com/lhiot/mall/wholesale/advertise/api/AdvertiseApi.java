package com.lhiot.mall.wholesale.advertise.api;

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

import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.advertise.domain.Advertise;
import com.lhiot.mall.wholesale.advertise.domain.gridparam.AdvertiseGirdParam;
import com.lhiot.mall.wholesale.advertise.service.AdvertiseService;
import com.lhiot.mall.wholesale.base.PageQueryObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "广告")
@RestController
@RequestMapping
public class AdvertiseApi {
	
	private final AdvertiseService advertiseService;
	
	@Autowired
	public AdvertiseApi(AdvertiseService advertiseService){
		this.advertiseService = advertiseService;
	}
	
    @PostMapping("/advertice")
    @ApiOperation(value = "添加广告", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody Advertise advertise){
    	if(advertiseService.create(advertise)){
    		return ResponseEntity.created(URI.create("/advertice/"+advertise.getId()))
    				.body(advertise);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/advertice/{id}")
    @ApiOperation(value = "根据ID修改广告", response = Advertise.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody Advertise advertise) {
        if (advertiseService.update(advertise)) {
            return ResponseEntity.ok(advertise);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/advertice/{id}")
    @ApiOperation(value = "根据id批量删除广告")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	advertiseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/advertice/{id}")
    @ApiOperation(value = "根据ID查询广告", response = Advertise.class)
    public ResponseEntity<Advertise> goodsUnit(@PathVariable("id") Long id) {
        return ResponseEntity.ok(advertiseService.advertise(id));
    }
    
    @PostMapping("/advertice/gird")
    @ApiOperation(value = "新建一个查询，分页查询广告", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) AdvertiseGirdParam param) {
        return ResponseEntity.ok(advertiseService.pageQuery(param));
    }
}
