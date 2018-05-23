package com.lhiot.mall.wholesale.goods.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.activity.domain.ActivityPeriodsType;
import com.lhiot.mall.wholesale.activity.domain.FlashActivityGoods;
import com.lhiot.mall.wholesale.activity.domain.FlashsaleGoods;
import com.lhiot.mall.wholesale.activity.service.FlashsaleService;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.GoodsFlashsale;
import com.lhiot.mall.wholesale.goods.domain.GoodsInfo;
import com.lhiot.mall.wholesale.goods.domain.GoodsMinPrice;
import com.lhiot.mall.wholesale.goods.domain.LayoutType;
import com.lhiot.mall.wholesale.goods.domain.ModifyGoodsCategory;
import com.lhiot.mall.wholesale.goods.domain.PlateCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsMapper;
import com.lhiot.mall.wholesale.order.domain.SoldQuantity;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.setting.service.SettingService;

/**GoodsService
 * 商品中心
 * @author lynn
 *
 */
@Service
@Transactional
public class GoodsService {
	
	private final GoodsMapper goodsMapper;
	private final OrderMapper orderMapper;
	private final GoodsPriceRegionService priceRegionService;
	private final PlateCategoryService plateCategoryService;
	private final FlashsaleService flashsaleService;
	private final SettingService settingService;
	private static final String SOLD_COUNT = "soldDegree";//销售数量的配置系数
	@Autowired
	public GoodsService(GoodsMapper goodsMapper,OrderMapper orderMapper,
			GoodsPriceRegionService priceRegionService,
			PlateCategoryService plateCategoryService,
			FlashsaleService flashsaleService,
			SettingService settingService){
		this.goodsMapper = goodsMapper;
		this.orderMapper = orderMapper;
		this.priceRegionService = priceRegionService;
		this.plateCategoryService = plateCategoryService;
		this.flashsaleService = flashsaleService;
		this.settingService = settingService;
	}
	
	/**
	 * 新增商品
	 * @param goods
	 * @return
	 */
	public boolean create(Goods goods){
		return goodsMapper.insert(goods)>0;
	}
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(String ids){
		if(StringUtils.isBlank(ids)){
			return ;
		}
		List<Long> list = Arrays.asList(ids.split(",")).stream()
								.map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
		goodsMapper.removeInbatch(list);
	}
	
	/**
	 * 修改商品
	 * @param goods
	 * @return
	 */
	public boolean update(Goods goods){
		return goodsMapper.update(goods)>0;
	}
	
	/**
	 * 根据id查询商品信息
	 * @param id
	 * @return
	 */
	public Goods goods(Long id){
		return goodsMapper.select(id);
	}

	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(GoodsGirdParam param){
		int count = goodsMapper.pageQueryCount(param);
		int page = param.getPage();
		int rows = param.getRows();
		//起始行
		param.setStart((page-1)*rows);
		//总记录数
		int totalPages = (count%rows==0?count/rows:count/rows+1);
		if(totalPages < page){
			page = 1;
			param.setPage(page);
			param.setStart(0);
		}
		List<Goods> goods = goodsMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goods);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}


	public GoodsInfo goodsInfo(long id){
		return goodsMapper.goodsInfo(id);
	}

	public GoodsFlashsale goodsFlashsale(Map param){
		return goodsMapper.goodsFlashsale(param);
	}

	public List<GoodsInfo> inventoryList(long userId){
		return goodsMapper.inventoryList(userId);
	}

	public List<GoodsInfo> recommendList(){
		List<Long> plateIds=plateCategoryService.recommendGoodsId(LayoutType.list);
		if (plateIds.isEmpty()){
			return new ArrayList<GoodsInfo>();
		}
		return goodsMapper.recommendList(plateIds);
	}
	
	/**
	 * 根据商品分类id批量查询商品
	 * @param list
	 * @return
	 */
	public List<Goods> findGoodsByCategory(List<Long> list){
		return goodsMapper.searchByCategory(list);
	}
	
	/**
	 * 查询编码是否重复，进而判断是否可以进行修改和增加操作
	 * @param goods
	 * @return true允许操作，false 不允许操作
	 */
	public boolean allowOperation(Goods goods){
		boolean success = true;
		List<Goods> gcs = goodsMapper.searchByCode(goods.getGoodsCode());
		Long id = goods.getId();
		//如果不存在重复的编码
		if(gcs.isEmpty()){
			return success;
		}
		//存在重复的编码,则判断是否为本身
		if(null == id){
			success = false;
			return success;
		}
		for(Goods gc : gcs){
			Long categoryId = gc.getId();
			if(!Objects.equals(categoryId, id)){
				success = false;
				break;
			}
		}
		return success;
	}
	
	/**
	 * 根据分类查询商品列表
	 * @param categoryId
	 * @return
	 */
	public List<Goods> findByCategory(Long categoryId){
		return goodsMapper.categoryGoods(categoryId);
	}
	
	/**
	 * 根据关键词查询商品列表
	 * @param keyword 关键词
	 * @param id 关键词id
	 * @return
	 */
	public List<Goods> findGoodsByKeyword(String keyword,Long id){
		List<Goods> goodses = goodsMapper.keywordGoods(keyword);
		if(goodses.isEmpty()){
			return goodses;
		}
		//计算商品最低价格及售卖数
		this.priceRegionAndSoldQua(goodses);
		//排序
		if(Objects.isNull(id)){
			return goodses;
		}
		int size = goodses.size();
		Goods goods = null;
		for(int i=0;i<size;i++){
			goods = goodses.get(i);
			if(Objects.equals(id, goods.getKeywordId())){
				goodses.remove(i);
				goodses.add(0, goods);
				break;
			}
		}
		return goodses;
	}
	
	/**
	 * 获取版块商品
	 * @param plateId
	 * @return
	 */
	public PlateCategory plateGoods(Long plateId){
		if(Objects.isNull(plateId)){
			return null;
		}
		PlateCategory plateCategory = plateCategoryService.plateCategory(plateId);
		List<Goods> plateGoodses = goodsMapper.plateGoodses(plateId);
		//组装商品的售卖数和最低价格
		this.priceRegionAndSoldQua(plateGoodses);
		plateCategory.setChannelGoods(plateGoodses);
		return plateCategory;
	}
	
	/**
	 * 获取版块商品
	 * @param plateId
	 * @return
	 */
	public List<PlateCategory> plateGoodses(LayoutType layoutType){
		List<PlateCategory> plateCategories = plateCategoryService.search(layoutType.toString());
		if(plateCategories.isEmpty()){
			return plateCategories;
		}
		for(PlateCategory plateCategory : plateCategories){
			List<Goods> plateGoodses = goodsMapper.plateGoodses(plateCategory.getId());
			//组装商品的售卖数和最低价格
			this.priceRegionAndSoldQua(plateGoodses);
			plateCategory.setChannelGoods(plateGoodses);
		}
		return plateCategories;
	}
	
	/**
	 * 统计商品的销售数量以及商品的最低和最高售价,
	 * 及判断当前商品是否抢购商品
	 * @param goodses
	 */
	public void priceRegionAndSoldQua(List<Goods> goodses){
		if(goodses.isEmpty()){
			return ;
		}
		List<Long> ids = new ArrayList<>();
		goodses.forEach(goods -> {
			Long goodsId = goods.getId();
			ids.add(goodsId);
		});
		//订单中心获取商品销售数量
		List<SoldQuantity> soldQuantities = orderMapper.soldQuantity(ids);
		for(SoldQuantity soldQuantity : soldQuantities){
			int count = soldQuantity.getSoldQuantity();
			//设置计算出来的销售数量
			soldQuantity.setSoldQuantity(this.calSoldCount(count));
		}
		//组装商品的销售数量
		for(Goods goods : goodses){
			for(SoldQuantity soldQuantity : soldQuantities){
				if(Objects.equals(goods.getId(), 
						soldQuantity.getGoodsId())){
					goods.setSoldQuantity(soldQuantity.getSoldQuantity());
				}
			}
		}
		
		//获取订单的最低售价
		List<GoodsMinPrice> minPrices = priceRegionService.minPrices(ids);
		for(Goods goods : goodses){
			for(GoodsMinPrice minPrice : minPrices){
				if(Objects.equals(goods.getId(), 
						minPrice.getGoodsId())){
					goods.setMinPrice(minPrice.getMinPrice());
					goods.setMaxPrice(minPrice.getMaxPrice());
				}
			}
		}
		
		//获取当前的抢购商品
		FlashActivityGoods flashActivityGoods = flashsaleService.flashGoods(ActivityPeriodsType.current);
		List<FlashsaleGoods> flGoods = flashActivityGoods.getProList();
		if(flGoods.isEmpty()){
			return ;
		}
		//匹配抢购商品价格及标识为抢购商品
		for(Goods goods : goodses){
			for(FlashsaleGoods flashsaleGoods : flGoods){
				if(Objects.equals(goods.getId(), flashsaleGoods.getGoodsId())){
					goods.setIsFlash(true);
					goods.setMinPrice(flashsaleGoods.getSpecialPrice());
				}
			}
		}
	}
	
	/**
	 * 获取单个商品的销售数量
	 * @param goodsIds
	 * @return
	 */
	public int soldCount(Long goodsId){
		int defaultValue = 0;
		if(!Objects.isNull(goodsId)){
			List<Long> ids = new ArrayList<>();
			ids.add(goodsId);
			List<SoldQuantity> soldCounts = orderMapper.soldQuantity(ids);
			if(!soldCounts.isEmpty()){
				SoldQuantity soldQuantity = soldCounts.get(0);
				defaultValue = soldQuantity.getSoldQuantity();
			}
		}
		return this.calSoldCount(defaultValue);
	}
	
	/**
	 * 计算销售数量
	 * @param realCount 真实的或者默认的销售数量
	 * @param degree 系数
	 * @return
	 */
	public int calSoldCount(int realCount){
		BigDecimal degree = this.soldDegree();
		int real = (Objects.isNull(realCount) ? 0 : realCount);
		BigDecimal realBigDecimal = new BigDecimal(real);
		return degree.multiply(realBigDecimal).intValue();
	}
	
	/**
	 * 获取商品的销售系数
	 * @return
	 */
	public BigDecimal soldDegree(){
		BigDecimal degree = new BigDecimal(10);//给定默认值为10
		ParamConfig config = settingService.searchConfigParam(SOLD_COUNT);
		if(Objects.isNull(config)){
			return degree;
		}
		String value = config.getConfigParamValue();
		if(StringUtils.isBlank(value)){
			return degree;
		}
		Pattern pattern = Pattern.compile("^[0-9]+.?[0-9]+");
		boolean isNum = pattern.matcher(value).matches();
		if(!isNum){
			return degree;
		}
		degree = new BigDecimal(value);
		return degree;
	}
	
	/**
	 * 修改商品的分类
	 * @param param
	 * @return
	 */
	public boolean updateCategory(ModifyGoodsCategory param){
		Long categoryId = param.getCategoryId();
		String goodsIds = param.getGoodsIds();
		if(StringUtils.isBlank(goodsIds) || Objects.isNull(categoryId)){
			return false;
		}
		List<Long> ids = Arrays.asList(param.getGoodsIds().split(",")).stream()
							   .map(id -> Long.parseLong(id.trim()))
							   .collect(Collectors.toList());
		Map<String,Object> map = ImmutableMap.of("categoryId", categoryId, "goodsIds", ids);
		return goodsMapper.updateCategory(map)>0;
	}
}
