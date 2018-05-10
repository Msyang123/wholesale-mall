package com.lhiot.mall.wholesale.user.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("后台管理用户信息")
public class UserResult {

	@ApiModelProperty(notes="用户id",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="用户电话",dataType="phone")
	private String phone;
	
	@ApiModelProperty(notes="店铺名称",dataType="String")
	private String shopName;
	
	@ApiModelProperty(notes="店长名称",dataType="String")
	private String userName;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(notes="最近消费时间",dataType="Timestamp")
	private Timestamp lastCreateTime;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(notes="注册时间",dataType="Timestamp")
	private Timestamp registerTime;
	
	@ApiModelProperty(notes="用户状态",dataType="String")
	private String userStatus;
	
	@ApiModelProperty(notes="业务员名称",dataType="String")
	private String salesmanName;
	
	@ApiModelProperty(notes="本月消费/频次",dataType="String")
	private String thisMonth;
	
	@ApiModelProperty(notes="上月消费/频次",dataType="String")
	private String lastMonth;
	
	@ApiModelProperty(notes="累计消费/频次",dataType="String")
	private String accumulative;
	
	@ApiModelProperty(notes="欠费金额/频次",dataType="String")
	private String arrears;
}
