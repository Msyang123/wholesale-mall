package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class PlateGoods {

	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="版块id",dataType="Long")
	private Long plateId;
	
	@ApiModelProperty(notes="版块商品排序",dataType="Integer")
	private Integer rank;
	
	@ApiModelProperty(notes="商品规格id,逗号分割",dataType="String")
	private String goodsStandardIds;
	
	@ApiModelProperty(notes="商品版块Id和规格id",dataType="String")
	private String idAndStandardIds;
	
	@ApiModelProperty(notes="商品规格id",dataType="Long")
	private Long goodsStandardId;
}
