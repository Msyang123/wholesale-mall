package com.lhiot.mall.wholesale.activity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("写抢购记录及修改剩余数量")
public class FlashGoodsRecord {
	
	@ApiModelProperty(notes="用户id",dataType="Long")
	private Long userId;
	
	@ApiModelProperty(notes="活动商品ID",dataType="Long")
	private Long flashsaleGoodsId;
	
	@ApiModelProperty(notes="抢购数量",dataType="Integer")
	private Integer buyCount;
	
	@ApiModelProperty(notes="订单id",dataType="Long")
	private Long orderId;
}
