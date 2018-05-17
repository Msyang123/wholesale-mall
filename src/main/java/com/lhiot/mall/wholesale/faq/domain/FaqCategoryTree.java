package com.lhiot.mall.wholesale.faq.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class FaqCategoryTree {

	@ApiModelProperty(notes="分类id",dataType="Long")
	private Long id;
	
	@JsonProperty("pId")
	@ApiModelProperty(notes="分类父id",dataType="Long")
	private Long parentId;
	
	@JsonProperty("name")
	@ApiModelProperty(notes="节点名字",dataType="String")
	private String faqCategoryName;
	
}
