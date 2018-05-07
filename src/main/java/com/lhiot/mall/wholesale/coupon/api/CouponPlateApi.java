package com.lhiot.mall.wholesale.coupon.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
import com.lhiot.mall.wholesale.coupon.service.CouponPlateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "优惠券模板")
@RestController
@RequestMapping
public class CouponPlateApi {
	
	private final CouponPlateService couponPlateService;
	private final CouponConfigService couponConfigService;
	
	@Autowired
	public CouponPlateApi(CouponPlateService couponPlateService,
			CouponConfigService couponConfigService){
		this.couponPlateService = couponPlateService;
		this.couponConfigService = couponConfigService;
	}
	
    @PostMapping("/coupon/plate")
    @ApiOperation(value = "添加优惠券模板", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody CouponPlate couponPlate){
    	if(couponPlateService.create(couponPlate)){
    		return ResponseEntity.created(URI.create("/coupon/plate/"+couponPlate.getId()))
    				.body(couponPlate);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/coupon/plate/{id}")
    @ApiOperation(value = "根据ID修改优惠券模板", response = CouponPlate.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody CouponPlate couponPlate) {
        if (couponPlateService.update(couponPlate)) {
            return ResponseEntity.ok(couponPlate);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/coupon/plate/{id}")
    @ApiOperation(value = "根据id批量删除优惠券模板")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	couponPlateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/coupon/plate/{id}")
    @ApiOperation(value = "根据ID查询优惠券模板", response = CouponPlate.class)
    public ResponseEntity<CouponPlate> goodsUnit(@PathVariable("id") Long id) {
        return ResponseEntity.ok(couponPlateService.couponPlate(id));
    }
    
    @PostMapping("/coupon/plate/gird")
    @ApiOperation(value = "新建一个查询，分页查询优惠券模板", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) CouponGridParam param) {
        return ResponseEntity.ok(couponPlateService.pageQuery(param));
    }
    
	@GetMapping("/coupon/plate/trydelete/{ids}")
    @ApiOperation(value = "查看模板是否可以被删除")
    public ResponseEntity<String> tryOperation(@PathVariable("ids") String ids) {
		List<Long> list = Arrays.asList(ids.split(",")).stream().map(id -> Long.parseLong(id.trim()))
				.collect(Collectors.toList());
		List<String> result = new ArrayList<>();
		for(Long id : list){
			List<CouponConfig> configs = couponConfigService.existConfig(id);
			if(!configs.isEmpty()){
				CouponConfig config = configs.get(0);
				result.add(config.getCouponName());
			}
		}
        return ResponseEntity.ok(StringUtils.collectionToDelimitedString(result,","));
    }
}
