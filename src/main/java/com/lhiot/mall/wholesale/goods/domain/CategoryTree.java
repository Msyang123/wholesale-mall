package com.lhiot.mall.wholesale.goods.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class CategoryTree {

	@ApiModelProperty(notes="分类id",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="分类父id",dataType="Long")
	private Long pId;
	
	@ApiModelProperty(notes="节点名字",dataType="String")
	private String name;
	
	@ApiModelProperty(notes="是否为父节点",dataType="Boolean")
	private Boolean isParent;
	
	@ApiModelProperty(notes="父节点名字",dataType="String")
	private String parentClassName;
	
	@ApiModelProperty(notes="等级",dataType="Integer")
	private Integer level;
	
}
