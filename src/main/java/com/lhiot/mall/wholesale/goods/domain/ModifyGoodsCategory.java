package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("修改商品分类的参数")
public class ModifyGoodsCategory {
	
	@ApiModelProperty(notes="商品的id,逗号分割的字符串",dataType="String")
	private String goodsIds;
	
	@ApiModelProperty(notes="商品分类id",dataType="Long")
	private Long categoryId;
}
