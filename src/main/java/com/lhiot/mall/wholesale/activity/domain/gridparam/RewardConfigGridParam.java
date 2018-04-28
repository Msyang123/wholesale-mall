package com.lhiot.mall.wholesale.activity.domain.gridparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ApiModel(description = "商品分页查询的参数")
@NoArgsConstructor
@ToString
public class RewardConfigGridParam extends PageObject {

	@ApiModelProperty(notes="活动id",dataType="Long")
	private Long activityId;
	
	@ApiModelProperty(notes="商品单位名称",dataType="Integer")
	private Integer start;
}
