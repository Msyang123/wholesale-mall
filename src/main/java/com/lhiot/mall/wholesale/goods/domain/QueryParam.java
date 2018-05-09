package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("查询参数")
public class QueryParam {
	@ApiModelProperty(notes="商品条码",dataType="String")
	private String barCode;
	
	@ApiModelProperty(notes="id",dataType="Long")
	private Long cards;
}
