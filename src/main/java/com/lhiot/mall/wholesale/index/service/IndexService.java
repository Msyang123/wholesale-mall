package com.lhiot.mall.wholesale.index.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
import com.lhiot.mall.wholesale.advertise.domain.Advertise;
import com.lhiot.mall.wholesale.advertise.domain.AdvertiseType;
import com.lhiot.mall.wholesale.advertise.service.AdvertiseService;
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
	private final GoodsService goodsService;
	private final GoodsCategoryService goodsCategoryService;
	
	
	@Autowired
	public IndexService(AdvertiseService advertiseService,
			GoodsService goodsService,
			GoodsCategoryService goodsCategoryService){
		this.advertiseService = advertiseService;
		this.goodsService = goodsService;
		this.goodsCategoryService = goodsCategoryService;
	}
	
	/**
	 * 查询首页数据
	 * @return
	 */
	public Index index(){
		Index index = new Index();
		//查询新闻资讯列表--开发中
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
		List<Advertise> popups = advertiseService.findByType(AdvertiseType.popup);
		if(!popups.isEmpty()){
			index.setAdvBanner(popups.get(FIRST_INDEX));
		}
		
		//获取轮播图广告
		List<Advertise> swiperSlides = advertiseService.findByType(AdvertiseType.top);
		index.setSwiperSlides(swiperSlides);
		
		//获取版块商品
		List<PlateCategory> plateCategories = goodsService.plateGoodses();
		if(!plateCategories.isEmpty()){
			for(PlateCategory plateCategory : plateCategories){
				String layout = plateCategory.getLayout();
				if(Objects.equal(layout, LayoutType.roll.toString())){
					index.setHotPro(plateCategory);
					plateCategories.remove(plateCategory);
					break;
				}
			}
			index.setRanking(plateCategories);
		}
		return index;
	}
}
