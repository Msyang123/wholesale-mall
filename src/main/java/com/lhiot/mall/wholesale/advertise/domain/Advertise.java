package com.lhiot.mall.wholesale.advertise.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class Advertise {

	@ApiModelProperty(notes="广告ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="标题",dataType="String")
	private String title;
	
	@JsonProperty("href")
	@ApiModelProperty(notes="链接地址",dataType="String")
	private String linkUrl;
	
	@ApiModelProperty(notes="广告位置 0广告弹窗 1首页轮播图 2限时抢购 3底部banner图",dataType="String")
	private String advertmentPosition;
	
	@JsonProperty("src")
	@ApiModelProperty(notes="图片地址",dataType="String")
	private String advertmentImage;
	
	@ApiModelProperty(notes="开始时间",dataType="String")
	private String beginTime;
	
	@ApiModelProperty(notes="结束时间",dataType="String")
	private String endTime;
	
	@JsonProperty("createTime")
	@ApiModelProperty(notes="创建时间",dataType="String")
	private String createAt;
	
	@ApiModelProperty(notes="是否有效 no无效 yes有效",dataType="String")
	private String vaild;
}
