package com.lhiot.mall.wholesale.coupon.domain.gridparam;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ApiModel(description = "分页查询的参数")
@NoArgsConstructor
@ToString
public class CouponGridParam extends PageObject {
	
	@ApiModelProperty(notes="优惠券名称",dataType="String")
	private String couponName;
	
	@ApiModelProperty(notes="优惠券状态 no-无效 yes-有效",dataType="String")
	private String vaild;
	
	@ApiModelProperty(notes="配置类型  activity-活动送券 artificial-手动发券",dataType="String")
	private String couponType;
	
	@ApiModelProperty(notes="优惠券类型  all-全品类 single-单品类",dataType="String")
	private String couponTypePlate;
	
	@ApiModelProperty(notes="用户电话",dataType="String")
	private String phone;
	
	@ApiModelProperty(notes="用户id,list",dataType="java.util.List")
	private List<Long> userIds;
	
	@ApiModelProperty(notes="优惠券使用情况",dataType="String")
	private String couponStatus;
	
	@ApiModelProperty(notes="查询起始页",dataType="Integer")
	private Integer start;
}
