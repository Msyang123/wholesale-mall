package com.lhiot.mall.wholesale.goods.domain.girdparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "商品分类分页查询的参数")
public class GoodsCategoryGirdParam extends PageObject {

	@ApiModelProperty(notes="商品分类父ID",dataType="Long")
	private Long parentId;
	
	@ApiModelProperty(notes="分类名称",dataType="String")
	private String categoryName;
}
