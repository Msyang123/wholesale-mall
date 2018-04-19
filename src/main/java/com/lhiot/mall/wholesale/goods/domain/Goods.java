package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class Goods {

	@ApiModelProperty(notes="商品单位ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="商品编码",dataType="String")
	private String goodsCode;
	
	@ApiModelProperty(notes="商品名字",dataType="String")
	private String goodsName;
	
	@ApiModelProperty(notes="商品分类id",dataType="Long")
	private Long categoryId;
	
	@ApiModelProperty(notes="商品主图路径",dataType="String")
	private String goodsImage;
	
	@ApiModelProperty(notes="商品基础条码",dataType="String")
	private String baseBar;
	
	@ApiModelProperty(notes="基础单位id",dataType="Long")
	private Long baseUnit;
	
	@ApiModelProperty(notes="安全库存",dataType="Integer")
	private String stockLimit;
	
	@ApiModelProperty(notes="商品描述",dataType="String")
	private String goodsDes;
	
	@ApiModelProperty(notes="商品详情图片s",dataType="String")
	private String goodsImages;
	
	@ApiModelProperty(notes="服务保障图片",dataType="String")
	private String otherImage;
	
	@ApiModelProperty(notes="商品基础单位编码",dataType="String")
	private String baseUnitCode;
	
	@ApiModelProperty(notes="商品基础单位名称",dataType="String")
	private String baseUnitName;
	
	@ApiModelProperty(notes="商品分类名称",dataType="String")
	private String categoryName;
	
}
