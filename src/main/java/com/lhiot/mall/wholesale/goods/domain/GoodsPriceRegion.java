package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class GoodsPriceRegion {

	@ApiModelProperty(notes="商品价格区间id",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="商品规格id",dataType="Long")
	private Long goodsStandardId;
	
	@ApiModelProperty(notes="价格",dataType="Integer")
	private Integer price;
	
	@ApiModelProperty(notes="商品分类id",dataType="Long")
	private Long categoryId;
	
	@ApiModelProperty(notes="规格数量描述",dataType="String")
	private String standardDes;
	
	@ApiModelProperty(notes="享受价格的最低购买数量",dataType="Integer")
	private Integer minQuantity;
	
	@ApiModelProperty(notes="享受价格的最高购买数量",dataType="Integer")
	private Integer maxQuantity;
	
	@ApiModelProperty(notes="排序",dataType="Integer")
	private Integer reorder;
}
