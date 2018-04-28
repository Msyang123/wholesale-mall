package com.lhiot.mall.wholesale.order.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("商品的售卖数量")
public class SoldQuantity {

	@ApiModelProperty(notes="商品规格id",dataType="Long")
	private Long goodsId;

	@ApiModelProperty(notes="售卖数量",dataType="Integer")
	private Integer soldQuantity;
}
