package com.lhiot.mall.wholesale.goods.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class GoodsCategory {

	@ApiModelProperty(notes="商品分类ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="商品分类父ID",dataType="Long")
	private Long parentId;
	
	@JsonProperty("name")
	@ApiModelProperty(notes="分类名称",dataType="String")
	private String categoryName;
	
	@ApiModelProperty(notes="分类编码",dataType="String")
	private String categoryCode;
	
	@ApiModelProperty(notes="父分类名称",dataType="String")
	private String parentCategoryName;
	
	@ApiModelProperty(notes="等级",dataType="Integer")
	private Integer levels;
	
	@JsonProperty("src")
	@ApiModelProperty(notes="等级",dataType="String")
	private String image;
	
	@ApiModelProperty(notes="父分类编码",dataType="String")
	private String parentCategoryCode;
}
