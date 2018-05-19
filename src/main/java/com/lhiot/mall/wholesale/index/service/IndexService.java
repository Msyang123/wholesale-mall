package com.lhiot.mall.wholesale.index.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhiot.mall.wholesale.activity.service.ActivityService;
import com.lhiot.mall.wholesale.advertise.domain.Advertise;
import com.lhiot.mall.wholesale.advertise.domain.AdvertiseType;
import com.lhiot.mall.wholesale.advertise.service.AdvertiseService;
import com.lhiot.mall.wholesale.article.domain.Information;
import com.lhiot.mall.wholesale.article.service.ArticleService;
import com.lhiot.mall.wholesale.goods.domain.GoodsCategory;
import com.lhiot.mall.wholesale.goods.domain.LayoutType;
import com.lhiot.mall.wholesale.goods.domain.PlateCategory;
import com.lhiot.mall.wholesale.goods.service.GoodsCategoryService;
import com.lhiot.mall.wholesale.goods.service.GoodsService;
import com.lhiot.mall.wholesale.index.domain.Index;

/**
 * 首页数据拼接，调用其他service
 * @author lynn
 *
 */

@Service
@Transactional
public class IndexService {
	private static final Long PARENT_ID = 0l;//父节点id
	private static final Integer FIRST_INDEX = 0;//第一个节点
	private final AdvertiseService advertiseService;
	private final ActivityService activityService;
	private final GoodsService goodsService;
	private final GoodsCategoryService goodsCategoryService;
	private final ArticleService articleService;
	
	
	@Autowired
	public IndexService(AdvertiseService advertiseService,
			GoodsService goodsService,
			GoodsCategoryService goodsCategoryService,
			ActivityService activityService,
			ArticleService articleService){
		this.advertiseService = advertiseService;
		this.goodsService = goodsService;
		this.goodsCategoryService = goodsCategoryService;
		this.activityService = activityService;
		this.articleService = articleService;
	}
	
	/**
	 * 查询首页数据
	 * @return
	 */
	public Index index(){
		Index index = new Index();
		//查询新闻资讯列表
		List<Information> infos = articleService.infomations();
		index.setNewsList(infos);
		//查询父分类列表
		List<GoodsCategory> categoryList = goodsCategoryService.findCategories(PARENT_ID);
		index.setCategoryList(categoryList);
		
		//获取限时抢购广告
		List<Advertise> flashes = advertiseService.findByType(AdvertiseType.flashsale);
		if(!flashes.isEmpty()){
			index.setFlash(flashes.get(FIRST_INDEX));
		}
		
		//获取底部Banner
		List<Advertise> footerBanners = advertiseService.findByType(AdvertiseType.bottom);
		if(!footerBanners.isEmpty()){
			index.setFooterBanner(footerBanners.get(FIRST_INDEX));
		}
		
		//获取首页弹窗广告
		List<Advertise> popups = advertiseService.findByType(AdvertiseType.poppup);
		if(!popups.isEmpty()){
			index.setAdvBanner(popups.get(FIRST_INDEX));
		}
		
		//获取轮播图广告
		List<Advertise> swiperSlides = advertiseService.findByType(AdvertiseType.top);
		index.setSwiperSlides(swiperSlides);
		
		//获取滚动排版的版块商品
		List<PlateCategory> hotPro = goodsService.plateGoodses(LayoutType.roll);
		if(!hotPro.isEmpty()){
			index.setHotPro(hotPro.get(FIRST_INDEX));
		}
		
		//获取平铺排版的版块商品
		List<PlateCategory> channelList = goodsService.plateGoodses(LayoutType.tile);
		index.setChannelList(channelList);
		return index;
	}
}
