package com.lhiot.mall.wholesale.goods.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class PlateCategory {

	@ApiModelProperty(notes="id",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="父节点id",dataType="Long")
	private Long parentId;
	
	@JsonProperty("name")
	@ApiModelProperty(notes="版块名称",dataType="String")
	private String plateName;
	
	@JsonProperty("src")
	@ApiModelProperty(notes="版块图片",dataType="String")
	private String plateImage;
	
	@ApiModelProperty(notes="排序",dataType="Integer")
	private Integer rank;
	
	@ApiModelProperty(notes="父分类名称",dataType="String")
	private String parentPlateName;
	
	@ApiModelProperty(notes="等级",dataType="Integer")
	private Integer levels;
	
	@ApiModelProperty(notes="布局方式",dataType="String")
	private String layout;
	
	@ApiModelProperty(notes="版块商品",dataType="java.util.List")
	private List<Goods> channelGoods = new ArrayList<>();
}

