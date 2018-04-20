package com.lhiot.mall.wholesale.goods.domain.girdparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "商品单位分页查询的参数")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class GoodsUnitGridParam extends PageObject{
	@ApiModelProperty(notes="商品单位编码",dataType="String")
	private String unitCode;
	@ApiModelProperty(notes="商品单位名称",dataType="String")
	private String unitName;
	@ApiModelProperty(notes="商品单位名称",dataType="Integer")
	private Integer start;
}
