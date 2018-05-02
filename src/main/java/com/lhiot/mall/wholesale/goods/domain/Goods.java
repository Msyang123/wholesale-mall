package com.lhiot.mall.wholesale.goods.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class Goods {

	@ApiModelProperty(notes="商品单位ID",dataType="Long")
	private Long id;
	
	@JsonIgnore
	@ApiModelProperty(notes="商品编码",dataType="String")
	private String goodsCode;
	
	@JsonProperty("name")
	@ApiModelProperty(notes="商品名字",dataType="String")
	private String goodsName;
	
	@JsonIgnore
	@ApiModelProperty(notes="商品分类id",dataType="Long")
	private Long categoryId;
	
	@JsonProperty("src")
	@ApiModelProperty(notes="商品主图路径",dataType="String")
	private String goodsImage;
	
	@JsonIgnore
	@ApiModelProperty(notes="商品基础条码",dataType="String")
	private String baseBar;
	
	@JsonIgnore
	@ApiModelProperty(notes="基础单位id",dataType="Long")
	private Long baseUnit;
	
	@JsonIgnore
	@ApiModelProperty(notes="安全库存",dataType="Integer")
	private String stockLimit;
	
	@JsonIgnore
	@ApiModelProperty(notes="商品描述",dataType="String")
	private String goodsDes;
	
	@JsonIgnore
	@ApiModelProperty(notes="商品详情图片s",dataType="String")
	private String goodsImages;
	
	@JsonIgnore
	@ApiModelProperty(notes="服务保障图片",dataType="String")
	private String otherImage;
	
	@JsonIgnore
	@ApiModelProperty(notes="基础单位编码",dataType="String")
	private String baseUnitCode;
	
	@JsonProperty("baseUnit")
	@ApiModelProperty(notes="商品基础单位名称",dataType="String")
	private String baseUnitName;
	
	@JsonIgnore
	@ApiModelProperty(notes="商品分类名称",dataType="String")
	private String categoryName;
	
	@ApiModelProperty(notes="计价单位",dataType="String")
	private String goodsUnit;
	
	@ApiModelProperty(notes="商品重量",dataType="BigDecimal")
	private BigDecimal weight;

	@ApiModelProperty(notes="商品规格",dataType="String")
	private String standard;
	
	@ApiModelProperty(notes="原价",dataType="Integer")
	private Integer price;
	
	@ApiModelProperty(notes="最低价",dataType="Integer")
	private Integer minPrice;
	
	@ApiModelProperty(notes="已售数量",dataType="Integer")
	private Integer soldQuantity;
	
	@JsonIgnore
	@ApiModelProperty(notes="关键词id，关键查询商品列表",dataType="Long")
	private Long keywordId;
}
