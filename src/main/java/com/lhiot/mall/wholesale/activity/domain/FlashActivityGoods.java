package com.lhiot.mall.wholesale.activity.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class FlashActivityGoods {

	@ApiModelProperty(notes="活动信息",dataType="Activity")
	private Activity activity;

	@ApiModelProperty(notes="抢购活动商品",dataType="java.util.List")
	List<FlashsaleGoods> flashGoods;
}
