package com.lhiot.mall.wholesale.goods.domain;

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
	
	@ApiModelProperty(notes="分类名称",dataType="String")
	private String categoryName;
	
	@ApiModelProperty(notes="父分类名称",dataType="String")
	private String parentCategoryName;
	
	@ApiModelProperty(notes="等级",dataType="Integer")
	private Integer levels;
}
