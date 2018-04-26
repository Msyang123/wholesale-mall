package com.lhiot.mall.wholesale.advertise.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel
@NoArgsConstructor
public class Advertise {

	@ApiModelProperty(notes="广告ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="标题",dataType="String")
	private String title;
	
	@ApiModelProperty(notes="链接地址",dataType="String")
	private String linkUrl;
	
	@ApiModelProperty(notes="广告位置 0广告弹窗 1首页轮播图 2限时抢购 3底部banner图",dataType="String")
	private String advertmentPosition;
	
	@ApiModelProperty(notes="图片地址",dataType="String")
	private String advertmentImage;
	
	@ApiModelProperty(notes="开始时间",dataType="String")
	private String beginTime;
	
	@ApiModelProperty(notes="结束时间",dataType="String")
	private String endTime;
	
	@ApiModelProperty(notes="创建时间",dataType="String")
	private String createAt;
	
	@ApiModelProperty(notes="是否有效 false无效 true有效",dataType="Boolean")
	private Boolean isVaild;
	
	@ApiModelProperty(notes="是否有效 0无效 1有效",dataType="Integer")
	private Integer vaild;
}
