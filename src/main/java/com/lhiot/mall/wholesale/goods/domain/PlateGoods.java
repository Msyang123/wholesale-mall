package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class PlateGoods {

	@ApiModelProperty(notes="版块ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="版块名称",dataType="String")
	private String plateName;
	
	@ApiModelProperty(notes="版块图片",dataType="String")
	private String plateImage;
	
}
