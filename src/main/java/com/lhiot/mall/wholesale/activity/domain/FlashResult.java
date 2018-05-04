package com.lhiot.mall.wholesale.activity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("当前抢购商品列表和下期抢购商品列表")
public class FlashResult {
	
	@ApiModelProperty(notes="当前的抢购活动",dataType="FlashActivityGoods")
	private FlashActivityGoods flash;
	
	@ApiModelProperty(notes="下期的抢购活动",dataType="FlashActivityGoods")
	private FlashActivityGoods nextFlash;
}
