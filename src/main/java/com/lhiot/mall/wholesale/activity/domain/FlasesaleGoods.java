package com.lhiot.mall.wholesale.activity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class FlasesaleGoods {
	

	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="活动id",dataType="Long")
	private Long activityId;
	
	@ApiModelProperty(notes="商品规格id",dataType="Long")
	private Long goodsStandardId;
	
	@ApiModelProperty(notes="商品id",dataType="Long")
	private Long goodsId;
	
	@ApiModelProperty(notes="商品名称",dataType="String")
	private String goodsName;
	
	@ApiModelProperty(notes="商品价格",dataType="String")
	private String price;
	
	@ApiModelProperty(notes="商品抢购价",dataType="Integer")
	private Integer specialPrice;
	
	@ApiModelProperty(notes="商品抢购价，冗余字段用于后天管理展示",dataType="String")
	private String specialPriceDispalye;
	
	@ApiModelProperty(notes="基础单位",dataType="String")
	private String baseUnit;
	
	@ApiModelProperty(notes="商品图片",dataType="String")
	private String goodsImage;
	
	@ApiModelProperty(notes="抢购库存",dataType="Integer")
	private Integer goodsStock;
	
	@ApiModelProperty(notes="用户限购数量",dataType="Integer")
	private Integer limitQuantity;
	
	@ApiModelProperty(notes="商品排序",dataType="Integer")
	private Integer rankNum;
}
