package com.lhiot.mall.wholesale.activity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class FlashActivity {
	
	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="活动id",dataType="Long")
	private Long activityId;
	
	@ApiModelProperty(notes="商品抢购价",dataType="Integer")
	private Integer specialPrice;
	
	@ApiModelProperty(notes="商品规格id",dataType="Long")
	private Long goodsStandardId;
	
	@ApiModelProperty(notes="抢购库存",dataType="Integer")
	private Integer goodsStock;
	
	@ApiModelProperty(notes="用户限购数量",dataType="Integer")
	private Integer limitQuantity;
	
	@ApiModelProperty(notes="商品排序",dataType="Integer")
	private Integer rankNum;
	
	@ApiModelProperty(notes="剩余数量",dataType="Integer")
	private Integer remain;
	
	@ApiModelProperty(notes="逗号分割的规格id",dataType="String")
	private String standardIds;
}
