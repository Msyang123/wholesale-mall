package com.lhiot.mall.wholesale.activity.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.activity.domain.ActivityPeriodsType;
import com.lhiot.mall.wholesale.activity.domain.ActivityType;
import com.lhiot.mall.wholesale.activity.domain.FlashActivity;
import com.lhiot.mall.wholesale.activity.domain.FlashActivityGoods;
import com.lhiot.mall.wholesale.activity.domain.FlashGoodsRecord;
import com.lhiot.mall.wholesale.activity.domain.FlashsaleGoods;
import com.lhiot.mall.wholesale.activity.domain.gridparam.ActivityGirdParam;
import com.lhiot.mall.wholesale.activity.mapper.FlashsaleMapper;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.GoodsFlashsale;
import com.lhiot.mall.wholesale.goods.domain.GoodsMinPrice;
import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.service.GoodsPriceRegionService;
import com.lhiot.mall.wholesale.goods.service.GoodsStandardService;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;

/**
 * 抢购中心
 * @author lynn
 *
 */
@Service
@Transactional
public class FlashsaleService {
	
	private final FlashsaleMapper flashsaleMapper;
	private final GoodsPriceRegionService goodsPriceRegionService;
	private final GoodsStandardService goodsStandardService;
	private final ActivityService activityService;
	@Autowired
	public FlashsaleService(FlashsaleMapper flasesaleMapper,GoodsStandardService goodsStandardService,
		   ActivityService activityService,
		   GoodsPriceRegionService goodsPriceRegionService){
		this.flashsaleMapper = flasesaleMapper;
		this.goodsStandardService = goodsStandardService;
		this.activityService = activityService;
		this.goodsPriceRegionService = goodsPriceRegionService;
	}
	
	/**
	 * 新增
	 * @param activityId活动id,standardIds
	 * @return
	 */
	public boolean create(FlashActivity flashActivity){
		
		String standardIds = flashActivity.getStandardIds();
		Long activityId = flashActivity.getActivityId();
		
		if(StringUtils.isBlank(standardIds)){
			return false;
		}
		List<Long> ids = Arrays.asList(standardIds.split(",")).stream()
								.map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
		List<FlashActivity> list = new ArrayList<>();
		duplicate(ids,activityId);
		if(ids.isEmpty()){
			return false;
		}
		FlashActivity activity = null;
		int rank = 1;
		for(Long id : ids){
			activity = new FlashActivity();
			activity.setActivityId(activityId);
			activity.setGoodsStandardId(id);
			activity.setGoodsStock(100);//默认100份
			activity.setLimitQuantity(1);//默认限购1份
			activity.setRankNum(rank);
			activity.setRemain(100);//默认设置100份
			//默认商品价格区间的最大值,没有设置最大值则为原价的8.5折
			activity.setSpecialPrice(this.specialPrice(id));
			list.add(activity);
			rank++;
		}
		return flashsaleMapper.insert(list)>0;
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
		flashsaleMapper.removeInbatch(list);
	}
	
	/**
	 * 修改flashActivity
	 * @param 
	 * @return
	 */
	public boolean update(FlashActivity flashActivity){
		//修改初始时候修改库存时，剩余数量与库存数相等
		flashActivity.setRemain(flashActivity.getGoodsStock());
		return flashsaleMapper.update(flashActivity)>0;
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public FlashActivity flashActivity(Long id){
		return flashsaleMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(ActivityGirdParam param){
		int count = flashsaleMapper.pageQueryCount(param);
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
		List<FlashsaleGoods> flashsales = flashsaleMapper.pageQuery(param);
		//组装商品数据
		this.contructData(flashsales);
		PageQueryObject result = new PageQueryObject();
		result.setRows(flashsales);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 去重复的
	 * @param standardIds
	 * @param id活动id
	 */
	public void duplicate(List<Long> standardIds,Long id){
		List<FlashsaleGoods> list = flashsaleMapper.search(id);
		if(list.isEmpty()) return ;
		for(int i=standardIds.size()-1;i>=0;i--){
			for(FlashsaleGoods ac : list){
				if(Objects.equals(standardIds.get(i), ac.getGoodsStandardId())){
					standardIds.remove(i);
				}
			}
		}
	}
	
	/**
	 * 将商品数据组装到分页查询的结果中
	 * @param flashsales
	 * @param standardIds
	 */
	public void contructData(List<FlashsaleGoods> flashsales){
		if(flashsales.isEmpty()){
			return ;
		}
		List<Long> standardIds = new ArrayList<>();
		//获取活动商品的规格id
		flashsales.forEach(fs -> {
			Long id = fs.getGoodsStandardId();
			standardIds.add(id);
		});
		List<GoodsStandard> goodsStandards = goodsStandardService.goodsStandards(standardIds);
		for(FlashsaleGoods flg : flashsales){
			Long sId = flg.getGoodsStandardId();
			for(GoodsStandard gs : goodsStandards){
				Long standardId = gs.getId();
				if(Objects.equals(sId, standardId)){
					flg.setBaseUnit(gs.getBaseUnitName());
					flg.setGoodsId(gs.getGoodsId());
					flg.setGoodsImage(gs.getGoodsImage());
					flg.setGoodsName(gs.getGoodsName());
					flg.setPrice(gs.getGoodsPrice());
					flg.setGoodsUnit(gs.getGoodsUnit());
				}
			}
		}
	}
	
	/**
	 * 获取当前或者下期抢购活动商品
	 * @param type
	 * @return
	 */
	public FlashActivityGoods flashGoods(ActivityPeriodsType activityPeriodsType){
		FlashActivityGoods flashActivityGoods = new FlashActivityGoods();
		ActivityType flasesale = ActivityType.flashsale;
		//获取开启抢购活动
		if(activityPeriodsType.equals(ActivityPeriodsType.current)){
			flashActivityGoods = activityService.currentActivity(flasesale);
		}else if(activityPeriodsType.equals(ActivityPeriodsType.next)){
			flashActivityGoods = activityService.nextActivity(flasesale);
		}
		if(Objects.isNull(flashActivityGoods)){
			return new FlashActivityGoods();
		}
		//查询活动商品
		List<FlashsaleGoods> flashGoods = flashsaleMapper.search(flashActivityGoods.getId());
		if(flashGoods.isEmpty()){
			return flashActivityGoods;
		}
		//查询并设置抢购进度
		for(FlashsaleGoods flashsaleGoods : flashGoods){
			int goodsStock = flashsaleGoods.getGoodsStock();
			int remain = flashsaleGoods.getRemain();
			//计算抢购进度
			BigDecimal b1 = new BigDecimal((goodsStock-remain)*100);
			BigDecimal b2 = new BigDecimal(goodsStock);
			flashsaleGoods.setProgress(b1.divide(b2).intValue()+"");
		}
		//组装商品信息
		this.contructData(flashGoods);
		flashActivityGoods.setProList(flashGoods);
		return flashActivityGoods;
	}
	
	/**
	 * 查询用户当前活动的抢购数量
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public Integer userRecords(Long userId,Long standardId){
		//获取当前开启的活动
		FlashActivityGoods flashActivity = activityService.currentActivity(ActivityType.flashsale);
		Long activityId = flashActivity.getId();
		Map<String,Object> param = ImmutableMap.of("userId", userId, "activityId", activityId,"standardId",standardId);
		return flashsaleMapper.userRecord(param);
	}

	/**
	 * 给一个默认8.5折的抢购价
	 * @param price
	 * @return
	 */
	public Integer specialPrice(Long standardId){
		//获取当前商品区间最大价格
		Integer discount = 0;
		List<Long> list = new ArrayList<>();
		list.add(standardId);
		if(!list.isEmpty()){
			List<GoodsMinPrice> goodsMinPrices = goodsPriceRegionService.minPrices(list);
			if(!goodsMinPrices.isEmpty()){
				GoodsMinPrice goodsMinPrice = goodsMinPrices.get(0);
				Integer maxPrice = goodsMinPrice.getMaxPrice();
				if(Objects.isNull(maxPrice) || Objects.equals(maxPrice, 0)){
					discount = maxPrice;
				}
			}
		}
		//如果没有设置价格区间，则为原价的9折
		if(Objects.equals(discount, 0)){
			//获取商品的原价
			GoodsStandard goodsStandard = goodsStandardService.goodsStandard(standardId);
			BigDecimal b1 = new BigDecimal(0.90);//默认一个9.0折的折扣
			BigDecimal bp = new BigDecimal(goodsStandard.getPrice());
			discount = b1.multiply(bp).intValue();
		}
		return discount;
	}

	/**
	 * 根据商品规格id查询商品的抢购信息
	 * @param standardId
	 * @return
	 */
	public FlashsaleGoods flashsaleGoods(Long standardId){
		FlashsaleGoods result = null;
		FlashActivityGoods ac = activityService.currentActivity(ActivityType.flashsale);
		if(Objects.isNull(ac)){
			return result;
		}
		List<FlashsaleGoods> flashGoods = flashsaleMapper.search(ac.getId());
		if(flashGoods.isEmpty()){
			return result;
		}
		for(FlashsaleGoods goods : flashGoods){
			if(Objects.equals(standardId, goods.getGoodsStandardId())){
				result = goods;
				break;
			}
		}
		return result; 
	}
	
	/**
	 * 获取用户的抢购商品信息
	 * @param standardId
	 * @param userId
	 * @return
	 */
	public GoodsFlashsale goodsFlashsale(Long standardId,Long userId){
		GoodsFlashsale goodsFlashSale = flashsaleMapper.searchFlashGoods(standardId);;
		if(Objects.isNull(goodsFlashSale)){
			return new GoodsFlashsale();
		}
		Long activityId = goodsFlashSale.getActivityId();
		Map<String,Object> param = ImmutableMap.of("userId", userId, "activityId", activityId,"standardId",standardId);
		goodsFlashSale.setUserPucharse(flashsaleMapper.userRecord(param));
		
		return goodsFlashSale;
	}
	
	/**
	 * 修改抢购数量，以及创建抢购记录
	 * @param orderDetail
	 * @return
	 */
	public boolean modifyFlashActiviy(OrderDetail orderDetail){
		//订单商品
		List<OrderGoods> list = orderDetail.getOrderGoodsList();
		boolean success = false;
		if(list.isEmpty()){
			return success;
		}
		//获取当前的抢购活动及商品
		FlashActivityGoods ac = this.flashGoods(ActivityPeriodsType.current);
		//抢购活动配置的活动商品
		List<FlashsaleGoods> proList = ac.getProList();
		//获取参数中抢购活动商品,1-是抢购商品
		List<OrderGoods> orderGoodses = list.stream().filter(goods -> Objects.equals(1, goods.getFlash()))
												.collect(Collectors.toList());
		
		Long orderId = orderDetail.getUserId();
		Long userId = orderDetail.getUserId();
		//构建批量修改抢购商品剩余数量及创建抢购记录的参数
		List<FlashGoodsRecord> param = new ArrayList<>();
		for(OrderGoods orderGoods : orderGoodses){
			for(FlashsaleGoods flashsaleGoods : proList){
				if(Objects.equals(orderGoods.getGoodsStandardId(), flashsaleGoods.getGoodsStandardId())){
					FlashGoodsRecord record = new FlashGoodsRecord();
					record.setBuyCount(orderGoods.getQuanity());
					record.setFlashsaleGoodsId(flashsaleGoods.getId());
					record.setOrderId(orderId);
					record.setUserId(userId);
					param.add(record);
				}
			}
		}
		if(param.isEmpty()){
			return success;
		}
		//批量修改抢购商品剩余数量
		flashsaleMapper.updateInBatch(param);
		//构建创建抢购记录的数据
		flashsaleMapper.insertRecord(param);
		return true;
	}
}
