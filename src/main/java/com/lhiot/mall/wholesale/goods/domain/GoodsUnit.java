package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class GoodsUnit {

	@ApiModelProperty(notes="商品单位ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="商品单位编码",dataType="String")
	private String unitCode;
	
	@ApiModelProperty(notes="商品单位名称",dataType="String")
	private String unitName;
	
}
