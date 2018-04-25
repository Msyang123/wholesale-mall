package com.lhiot.mall.wholesale.goods.domain.girdparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(description = "商品版块分页查询的参数")
public class PlateGirdParam extends PageObject {
	
	@ApiModelProperty(notes="版块名称",dataType="String")
	private String plateName;
	
	@ApiModelProperty(notes="版块名称",dataType="Long")
	private Long parentId;
	
	@ApiModelProperty(notes="分页查询起始页",dataType="Integer")
	private Integer start;
}
