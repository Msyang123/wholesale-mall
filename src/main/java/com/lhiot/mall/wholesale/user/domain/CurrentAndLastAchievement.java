package com.lhiot.mall.wholesale.user.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("业务员本期和上期业绩")
@NoArgsConstructor
public class CurrentAndLastAchievement {
	
	@ApiModelProperty(notes="销售总金额",dataType="Achievement")
	private Achievement current;
	
	@ApiModelProperty(notes="销售总金额",dataType="Achievement")
	private Achievement last;
}
