package com.lhiot.mall.wholesale.setting.domain.gridparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(description = "系统参数分类分页查询的参数")
public class ParamCategoryGirdParam extends PageObject {
	
	@ApiModelProperty(notes="参数分类名称",dataType="String")
	private String paramCategoryName;
	
	@ApiModelProperty(notes="父节点ID",dataType="String")
	private Long parentId;
	
	@ApiModelProperty(notes="分页查询起始页",dataType="Integer")
	private Integer start;
}
