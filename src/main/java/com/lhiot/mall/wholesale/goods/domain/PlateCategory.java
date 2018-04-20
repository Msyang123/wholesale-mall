package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class PlateCategory {

	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="版块ID",dataType="Long")
	private Long plateId;
	
	@ApiModelProperty(notes="商品规格id",dataType="String")
	private String goodsStandardId;
	
	@ApiModelProperty(notes="排序",dataType="Integer")
	private Integer rank;
}
