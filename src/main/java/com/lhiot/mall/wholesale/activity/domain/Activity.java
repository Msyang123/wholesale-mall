package com.lhiot.mall.wholesale.activity.domain;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class Activity {

	@ApiModelProperty(notes="活动ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="活动描述",dataType="String")
	private String activityDesc;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(notes="活动起始时间",dataType="Timestamp")
	private Timestamp startTime;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(notes="活动结束时间",dataType="Timestamp")
	private Timestamp endTime;
	
	@ApiModelProperty(notes="活动类型",dataType="String")
	private String activityType;
	
	@ApiModelProperty(notes="链接地址",dataType="String")
	private String linkUrl;
	
	@ApiModelProperty(notes="是否有效",dataType="String")
	private String vaild;
	
	@ApiModelProperty(notes="抢购活动商品",dataType="java.util.List")
	List<FlashsaleGoods> flashGoods;
}
