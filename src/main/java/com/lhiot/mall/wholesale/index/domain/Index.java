package com.lhiot.mall.wholesale.index.domain;

import java.util.ArrayList;
import java.util.List;

import com.lhiot.mall.wholesale.advertise.domain.Advertise;
import com.lhiot.mall.wholesale.article.domain.Information;
import com.lhiot.mall.wholesale.goods.domain.GoodsCategory;
import com.lhiot.mall.wholesale.goods.domain.PlateCategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("首页返回值")
public class Index {

	@ApiModelProperty(notes="新闻资讯--开发中",dataType="java.util.List")
	private List<Information> newsList = new ArrayList<>();
	
	@ApiModelProperty(notes="分类列表",dataType="java.util.List")
	private List<GoodsCategory> categoryList = new ArrayList<>();
	
	@ApiModelProperty(notes="限时抢购广告图",dataType="Advertise")
	private Advertise flash;
	
	@ApiModelProperty(notes="底部Banner",dataType="Advertise")
	private Advertise footerBanner;	
	
	@ApiModelProperty(notes="广告弹窗",dataType="Advertise")
	private Advertise advBanner;
	
	@ApiModelProperty(notes="轮播图",dataType="java.util.List")
	private List<Advertise> swiperSlides = new ArrayList<>();;
	
	@ApiModelProperty(notes="平铺排版的版块商品",dataType="HotPro")
	private PlateCategory hotPro;
	
	@ApiModelProperty(notes="滚动排版的版块商品",dataType="channelList")
	private List<PlateCategory> channelList = new ArrayList<>();
}
