package com.lhiot.mall.wholesale.user.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("业务员业绩统计条件参数")
public class SaleStatisticsParam {
	
	@ApiModelProperty(notes="第一个查询日期条件",dataType="Long")
	private String firstPeriod;
	
	@ApiModelProperty(notes="第二个查询的日期条件",dataType="Long")
	private String secondPeriod;
	
	@ApiModelProperty(notes="业务员id",dataType="Long")
	private Long salesmanId;
}
