package com.lhiot.mall.wholesale.goods.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("关键词查询商品历史")
public class SearchHistory {
   
	@ApiModelProperty(notes = "历史记录id", dataType = "Long")
    private Long id;
	
	@JsonIgnore
	@ApiModelProperty(notes = "用户id", dataType = "Long")
    private Long userId;
	
	@JsonProperty("name")
	@ApiModelProperty(notes = "关键词", dataType = "String")
    private String keyword;
	
	@ApiModelProperty(notes = "关键词id", dataType = "Long")
    private Long keywordId;
}
