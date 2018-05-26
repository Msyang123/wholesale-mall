package com.lhiot.mall.wholesale.activity.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class FlashsaleGoods {

	@JsonProperty("flashsaleId")
	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="活动id",dataType="Long")
	private Long activityId;
	
	@JsonProperty("standardId")
	@ApiModelProperty(notes="商品规格id",dataType="Long")
	private Long goodsStandardId;
	
	@JsonProperty("id")
	@ApiModelProperty(notes="商品id",dataType="Long")
	private Long goodsId;
	
	@JsonProperty("name")
	@ApiModelProperty(notes="商品名称",dataType="String")
	private String goodsName;
	
	@ApiModelProperty(notes="商品价格",dataType="Integer")
	private Integer price;
	
	@JsonProperty("flashPrice")
	@ApiModelProperty(notes="商品抢购价",dataType="Integer")
	private Integer specialPrice;
	
	@ApiModelProperty(notes="基础单位",dataType="String")
	private String baseUnit;
	
	@JsonProperty("unit")
	@ApiModelProperty(notes="商品单位",dataType="String")
	private String goodsUnit;
	
	@JsonProperty("src")
	@ApiModelProperty(notes="商品图片",dataType="String")
	private String goodsImage;
	
	@ApiModelProperty(notes="抢购库存",dataType="Integer")
	private Integer goodsStock;
	
	@ApiModelProperty(notes="用户限购数量",dataType="Integer")
	private Integer limitQuantity;
	
	@ApiModelProperty(notes="商品排序",dataType="Integer")
	private Integer rankNum;
	
	@JsonProperty("percent")
	@ApiModelProperty(notes="抢购进度",dataType="String")
	private String progress;
	
	@ApiModelProperty(notes="剩余数量",dataType="Integer")
	private Integer remain;
}
