package com.lhiot.mall.wholesale.activity.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.lhiot.mall.wholesale.activity.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.activity.domain.gridparam.ActivityGirdParam;
import com.lhiot.mall.wholesale.activity.mapper.FlashsaleMapper;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.service.GoodsStandardService;

/**
 * 抢购中心
 * @author lynn
 *
 */
@Service
@Transactional
public class FlashsaleService {
	
	private final FlashsaleMapper flashsaleMapper;
	
	private final GoodsStandardService goodsStandardService;
	private final ActivityService activityService;
	@Autowired
	public FlashsaleService(FlashsaleMapper flasesaleMapper,GoodsStandardService goodsStandardService,
		   ActivityService activityService){
		this.flashsaleMapper = flasesaleMapper;
		this.goodsStandardService = goodsStandardService;
		this.activityService = activityService;
	}
	
	/**
	 * 新增
	 * @param activityId活动id,standardIds
	 * @return
	 */
	public boolean create(FlashActivity flashActivity){
		
		String standardIds = flashActivity.getStandardIds();
		Long activityId = flashActivity.getActivityId();
		Integer price = flashActivity.getSpecialPrice();
		
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
			activity.setSpecialPrice(price);
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
	 * 去重重复的
	 * @param standardIds
	 * @param id活动id
	 */
	public void duplicate(List<Long> standardIds,Long id){
		List<FlashsaleGoods> list = flashsaleMapper.search(id);
		if(list.isEmpty()) return ;
		for(int i=standardIds.size();i>=0;i--){
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
			return flashActivityGoods;
		}
		//查询活动商品
		List<FlashsaleGoods> flashGoods = flashsaleMapper.search(flashActivityGoods.getId());
		if(flashGoods.isEmpty()){
			return flashActivityGoods;
		}
		//查询并设置抢购进度
		for(FlashsaleGoods flashsaleGoods : flashGoods){
			int goodsStock = flashsaleGoods.getGoodsStock();
			Map<String,Object> flashsaleProgress = this.flashsaleProgress(flashsaleGoods.getId(), goodsStock);
			flashsaleGoods.setProgress(flashsaleProgress.get("progress").toString());
			flashsaleGoods.setRemain(flashsaleProgress.get("remainNum").toString());
		}
		//组装商品信息
		this.contructData(flashGoods);
		flashActivityGoods.setProList(flashGoods);
		return flashActivityGoods;
	}
	
	/**
	 * 抢购商品抢购进度统计，及剩余数量
	 * @param id
	 * @return
	 */
	public Map<String,Object> flashsaleProgress(Long id,Integer goodsStock){
		int progress = 0;
		Integer sum = flashsaleMapper.flashGoodsRecord(id);
		if(Objects.equals(0, sum)){
			return ImmutableMap.of("progress", progress, "remainNum", goodsStock);
		}
		BigDecimal b1 = new BigDecimal(sum*100);
		BigDecimal b2 = new BigDecimal(goodsStock);
		progress = b1.divide(b2).intValue();
		return ImmutableMap.of("progress", progress, "remainNum", (goodsStock-sum));
	}
	
	/**
	 * 查询用户当前活动的抢购数量
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public Integer userRecords(Long userId,Long activityId){
		Map<String,Object> param = ImmutableMap.of("userId", userId, "activityId", activityId);
		return flashsaleMapper.userRecord(param);
	}



}
