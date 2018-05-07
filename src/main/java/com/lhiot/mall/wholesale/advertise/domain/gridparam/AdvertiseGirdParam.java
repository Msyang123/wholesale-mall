package com.lhiot.mall.wholesale.advertise.domain.gridparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "商品分页查询的参数")
@NoArgsConstructor
public class AdvertiseGirdParam extends PageObject {

	@ApiModelProperty(notes="广告位置 0广告弹窗 1首页轮播图 2限时抢购 3底部banner图",dataType="String")
	private String advertmentPosition;
	
	@ApiModelProperty(notes="是否有效 no无效 yes有效",dataType="String")
	private String vaild;
	
	@ApiModelProperty(notes="标题",dataType="String")
	private String title;

	@ApiModelProperty(notes="商品单位名称",dataType="Integer")
	private Integer start;
}
