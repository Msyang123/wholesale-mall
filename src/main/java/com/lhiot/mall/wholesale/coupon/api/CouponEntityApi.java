package com.lhiot.mall.wholesale.coupon.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.coupon.domain.CouponEntity;
import com.lhiot.mall.wholesale.coupon.domain.CouponStatusType;
import com.lhiot.mall.wholesale.coupon.domain.ReleaseCouponParam;
import com.lhiot.mall.wholesale.coupon.domain.UserCouponParam;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;
import com.lhiot.mall.wholesale.coupon.service.CouponEntityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "优惠券")
@RestController
@RequestMapping
public class CouponEntityApi {
	
	private final CouponEntityService couponEntityService;
	
	@Autowired
	public CouponEntityApi(CouponEntityService couponEntityService){
		this.couponEntityService = couponEntityService;
	}
	
    @GetMapping("/coupon/{userId}")
    @ApiOperation(value = "查询用户优惠券列表", response = CouponEntity.class, responseContainer = "List")
    public ResponseEntity<List<CouponEntity>> userCoupons(
    		@PathVariable(required=true) Long userId,
    		@RequestParam(defaultValue="1") Integer page,
    		@RequestParam(defaultValue="10") Integer rows) {
    	
    	UserCouponParam param = new UserCouponParam();
    	param.setPage(page);
    	param.setRows(rows);
    	param.setCouponStatus(CouponStatusType.unused.toString());
    	param.setUserId(userId);
    	param.setStart((page-1)*rows);
    	
        return ResponseEntity.ok(couponEntityService.userCoupons(param));
    }
    
    @GetMapping("/coupon")
    @ApiOperation(value = "查询用户可用的优惠券列表", response = CouponEntity.class, responseContainer = "List")
    public ResponseEntity<List<CouponEntity>> userCoupons(
    		@RequestParam Long userId,
    		@RequestParam Integer orderFee) {
    	
    	UserCouponParam param = new UserCouponParam();
    	param.setOrderFee(orderFee);
    	param.setUserId(userId);
    	param.setCouponStatus(CouponStatusType.unused.toString());
    	
        return ResponseEntity.ok(couponEntityService.userCoupons(param));
    }
    
    @PostMapping("/coupon")
    @ApiOperation(value = "手动发券", response = String.class)
    public ResponseEntity<String> releaseCoupon(@RequestBody ReleaseCouponParam param) {
        return ResponseEntity.ok(couponEntityService.realeaseCupon(param));
    }
    
    @PostMapping("/coupon/gird")
    @ApiOperation(value = "新建一个查询，分页查询优惠券", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) CouponGridParam param) {
        return ResponseEntity.ok(couponEntityService.pageQuery(param));
    }
    
    @DeleteMapping("/coupon/{id}")
    @ApiOperation(value = "根据id批量删除优惠券")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	couponEntityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
