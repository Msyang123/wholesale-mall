package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class GoodsCategory {

	@ApiModelProperty(notes="商品分类ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="商品分类父ID",dataType="Long")
	private Long parentId;
	
	@ApiModelProperty(notes="分类名称",dataType="String")
	private String categoryName;
	
}
