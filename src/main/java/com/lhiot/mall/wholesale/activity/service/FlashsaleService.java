package com.lhiot.mall.wholesale.activity.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.activity.domain.FlasesaleGoods;
import com.lhiot.mall.wholesale.activity.domain.FlashActivity;
import com.lhiot.mall.wholesale.activity.domain.gridparam.ActivityGirdParam;
import com.lhiot.mall.wholesale.activity.mapper.FlasesaleMapper;
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
	
	private final FlasesaleMapper flasesaleMapper;
	
	private final GoodsStandardService goodsStandardService;
	
	@Autowired
	public FlashsaleService(FlasesaleMapper flasesaleMapper,GoodsStandardService goodsStandardService){
		this.flasesaleMapper = flasesaleMapper;
		this.goodsStandardService = goodsStandardService;
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
		return flasesaleMapper.insert(list)>0;
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
		flasesaleMapper.removeInbatch(list);
	}
	
	/**
	 * 修改flashActivity
	 * @param 
	 * @return
	 */
	public boolean update(FlashActivity flashActivity){
		return flasesaleMapper.update(flashActivity)>0;
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public FlashActivity flashActivity(Long id){
		return flasesaleMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(ActivityGirdParam param){
		int count = flasesaleMapper.pageQueryCount(param);
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
		List<FlasesaleGoods> flashsales = flasesaleMapper.pageQuery(param);
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
		List<FlashActivity> list = flasesaleMapper.search(id);
		if(list.isEmpty()) return ;
		for(int i=standardIds.size();i>=0;i--){
			for(FlashActivity ac : list){
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
	public void contructData(List<FlasesaleGoods> flashsales){
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
		for(FlasesaleGoods flg : flashsales){
			Long sId = flg.getGoodsStandardId();
			for(GoodsStandard gs : goodsStandards){
				Long standardId = gs.getId();
				if(Objects.equals(sId, standardId)){
					flg.setBaseUnit(gs.getBaseUnitName());
					flg.setGoodsId(gs.getGoodsId());
					flg.setGoodsImage(gs.getGoodsImage());
					flg.setGoodsName(gs.getGoodsName());
					flg.setPrice(gs.getGoodsPrice());
				}
			}
		}
	}
}
