package com.lhiot.mall.wholesale.goods.domain.girdparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "商品分类分页查询的参数")
@NoArgsConstructor
public class GoodsCategoryGirdParam extends PageObject {

	@ApiModelProperty(notes="商品分类父ID",dataType="Long")
	private Long parentId;
	
	@ApiModelProperty(notes="分类编码",dataType="String")
	private String categoryCode;
	
	@ApiModelProperty(notes="分类名称",dataType="String")
	private String categoryName;
	
	@ApiModelProperty(notes="商品单位名称",dataType="Integer")
	private Integer start;
}
