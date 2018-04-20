package com.lhiot.mall.wholesale.goods.domain.girdparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "商品分页查询的参数")
public class GoodsGirdParam extends PageObject {

	@ApiModelProperty(notes="商品编码",dataType="String")
	private String goodsCode;
	
	@ApiModelProperty(notes="商品名字",dataType="String")
	private String goodsName;
	
	@ApiModelProperty(notes="商品分类id",dataType="Long")
	private Long categoryId;
}
