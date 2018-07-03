package com.lhiot.mall.wholesale.user.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("业务员业绩统计")
@NoArgsConstructor
public class Achievement {
	
	@ApiModelProperty(notes="销售总金额",dataType="Long")
	private Long salesAmount;
	
	@ApiModelProperty(notes=" 订单总数",dataType="Long")
	private Long orderCount;
	
	@ApiModelProperty(notes="退货订单总数",dataType="Long")
	private Long refundedCount;
	
	@ApiModelProperty(notes="下单商户数",dataType="Long")
	private Long userCount;
}
