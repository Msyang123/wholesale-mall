package com.lhiot.mall.wholesale.activity.api;

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
import com.lhiot.mall.wholesale.activity.domain.FlashActivity;
import com.lhiot.mall.wholesale.activity.domain.gridparam.ActivityGirdParam;
import com.lhiot.mall.wholesale.activity.service.FlashsaleService;
import com.lhiot.mall.wholesale.advertise.domain.Advertise;
import com.lhiot.mall.wholesale.advertise.domain.gridparam.AdvertiseGirdParam;
import com.lhiot.mall.wholesale.base.PageQueryObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "抢购")
@RestController
@RequestMapping
public class FlasesaleGoodsApi {
	
	private final FlashsaleService flashsaleService;
	
	@Autowired
	public FlasesaleGoodsApi(FlashsaleService flashsaleService){
		this.flashsaleService = flashsaleService;
	}
	
    @PostMapping("/flashsale")
    @ApiOperation(value = "添加", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody FlashActivity flashActivity){
    	if(flashsaleService.create(flashActivity)){
    		return ResponseEntity.created(URI.create("/flashsale/"+flashActivity.getId()))
    				.body(flashActivity);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/flashsale/{id}")
    @ApiOperation(value = "根据ID修改", response = FlashActivity.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody FlashActivity flashActivity) {
        if (flashsaleService.update(flashActivity)) {
            return ResponseEntity.ok(flashActivity);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/flashsale/{id}")
    @ApiOperation(value = "根据id批量删除")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	flashsaleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/flashsale/{id}")
    @ApiOperation(value = "根据ID查询", response = FlashActivity.class)
    public ResponseEntity<FlashActivity> goodsUnit(@PathVariable("id") Long id) {
        return ResponseEntity.ok(flashsaleService.flashActivity(id));
    }
    
    @PostMapping("/flashsale/gird")
    @ApiOperation(value = "新建一个查询，分页查询", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) ActivityGirdParam param) {
        return ResponseEntity.ok(flashsaleService.pageQuery(param));
    }
    
}
