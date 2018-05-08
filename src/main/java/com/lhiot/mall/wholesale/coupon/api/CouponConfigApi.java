package com.lhiot.mall.wholesale.coupon.api;

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
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.coupon.domain.CouponConfig;
import com.lhiot.mall.wholesale.coupon.domain.CouponPlate;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;
import com.lhiot.mall.wholesale.coupon.service.CouponConfigService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "优惠券配置")
@RestController
@RequestMapping
public class CouponConfigApi {
	
	private final CouponConfigService couponConfigService;
	
	@Autowired
	public CouponConfigApi(CouponConfigService couponConfigService){
		this.couponConfigService = couponConfigService;
	}
	
    @PostMapping("/coupon/config")
    @ApiOperation(value = "添加优惠券配置", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody CouponConfig couponConfig){
    	if(couponConfigService.create(couponConfig)){
    		return ResponseEntity.created(URI.create("/coupon/config/"+couponConfig.getId()))
    				.body(couponConfig);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/coupon/config/{id}")
    @ApiOperation(value = "根据ID修改优惠券配置", response = CouponPlate.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody CouponConfig couponConfig) {
        if (couponConfigService.update(couponConfig)) {
            return ResponseEntity.ok(couponConfig);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/coupon/config/{id}")
    @ApiOperation(value = "根据id批量删除优惠券配置")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	couponConfigService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/coupon/config/{id}")
    @ApiOperation(value = "根据ID查询优惠券配置", response = CouponPlate.class)
    public ResponseEntity<CouponConfig> goodsUnit(@PathVariable("id") Long id) {
        return ResponseEntity.ok(couponConfigService.couponConfig(id));
    }
    
    @PostMapping("/coupon/config/gird")
    @ApiOperation(value = "新建一个查询，分页查询优惠券配置", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) CouponGridParam param) {
        return ResponseEntity.ok(couponConfigService.pageQuery(param));
    }
}
