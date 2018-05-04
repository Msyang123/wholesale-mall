package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("商品价格区间中的最低价格")
public class GoodsMinPrice {

	@ApiModelProperty(notes="商品规格id",dataType="Long")
	private Long goodsId;

	@ApiModelProperty(notes="价格区间中最低的价格",dataType="Integer")
	private Integer minPrice;
	
	@ApiModelProperty(notes="价格区间中最高的价格",dataType="Integer")
	private Integer maxPrice;
}
