package com.lhiot.mall.wholesale.aftersale.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("售后订单详情")
@NoArgsConstructor
public class OrderRefundResult {
	
	@ApiModelProperty(notes="售后订单id",dataType="Long")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	
	@ApiModelProperty(notes="售后类型",dataType="String")
	private String applicationType;
	
	@ApiModelProperty(notes="存在的问题",dataType="String")
	private String existProblem;
	
	@ApiModelProperty(notes="其他问题",dataType="String")
	private String otherProblem;
	
	@ApiModelProperty(notes="售后证明图片",dataType="String")
	private String refundEvidence;
	
	@ApiModelProperty(notes="审核状态",dataType="String")
	private String auditStatus;
	
	@ApiModelProperty(notes="优惠金额",dataType="Integer")
	private Integer orderDiscountFee;
	
	@ApiModelProperty(notes="售后需要扣除的配送费用",dataType="Integer")
	private Integer deliveryFee;
	
	@ApiModelProperty(notes="备注",dataType="String")
	private String comments;
	
	@ApiModelProperty(notes="订单编号",dataType="String")
	@JsonSerialize(using = ToStringSerializer.class)
	private String orderId;
	
	@ApiModelProperty(notes="用户编号",dataType="Long")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long userId;
	
	@ApiModelProperty(notes="联系电话",dataType="String")
	private String contactsPhone;
	
	@ApiModelProperty(notes="售后申请时间",dataType="Timestamp")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp createAt;
	
	@ApiModelProperty(notes="订单补差额记录",dataType="java.util.List")
	private List<SupplementRecords> supplements = new ArrayList<>();
	
	@ApiModelProperty(notes="应付金额",dataType="Integer")
	private Integer payableFee;
	
	@ApiModelProperty(notes="订单运送费金额",dataType="Integer")
	private Integer orderDeliveryFee;
}
