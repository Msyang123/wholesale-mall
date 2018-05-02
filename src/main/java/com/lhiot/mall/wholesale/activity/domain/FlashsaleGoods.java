package com.lhiot.mall.wholesale.activity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class FlashsaleGoods {

	@JsonIgnore
	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@JsonIgnore
	@ApiModelProperty(notes="活动id",dataType="Long")
	private Long activityId;
	
	@JsonIgnore
	@ApiModelProperty(notes="商品规格id",dataType="Long")
	private Long goodsStandardId;
	
	@JsonProperty("id")
	@ApiModelProperty(notes="商品id",dataType="Long")
	private Long goodsId;
	
	@JsonProperty("name")
	@ApiModelProperty(notes="商品名称",dataType="String")
	private String goodsName;
	
	@ApiModelProperty(notes="商品价格",dataType="String")
	private String price;
	
	@JsonProperty("flashPrice")
	@ApiModelProperty(notes="商品抢购价",dataType="Integer")
	private Integer specialPrice;
	
	@JsonIgnore
	@ApiModelProperty(notes="商品抢购价，冗余字段用于后天管理展示",dataType="String")
	private String specialPriceDispalye;
	
	@ApiModelProperty(notes="基础单位",dataType="String")
	private String baseUnit;
	
	@JsonProperty("src")
	@ApiModelProperty(notes="商品图片",dataType="String")
	private String goodsImage;
	
	@JsonIgnore
	@ApiModelProperty(notes="抢购库存",dataType="Integer")
	private Integer goodsStock;
	
	@JsonIgnore
	@ApiModelProperty(notes="用户限购数量",dataType="Integer")
	private Integer limitQuantity;
	
	@JsonIgnore
	@ApiModelProperty(notes="商品排序",dataType="Integer")
	private Integer rankNum;
	
	@JsonProperty("percent")
	@ApiModelProperty(notes="抢购进度",dataType="String")
	private String progress;
	
	@ApiModelProperty(notes="剩余数量",dataType="String")
	private String remainNum;
}