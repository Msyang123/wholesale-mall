package com.lhiot.mall.wholesale.article.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("用于首页资讯滚动展示")
public class Information {

	@JsonProperty("id")
	@ApiModelProperty(notes="资讯ID",dataType="Long")
	private Long id;
	
	@JsonProperty("name")
	@ApiModelProperty(notes="资讯文章标题",dataType="String")
	private String articleTitle;
}
