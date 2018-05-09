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
import com.lhiot.mall.wholesale.activity.domain.RewardConfig;
import com.lhiot.mall.wholesale.activity.domain.RewardCoupon;
import com.lhiot.mall.wholesale.activity.domain.gridparam.RewardConfigGridParam;
import com.lhiot.mall.wholesale.activity.mapper.RewardConfigMapper;
import com.lhiot.mall.wholesale.base.PageQueryObject;

/**
 * 奖励配置--注册活动
 * @author lynn
 *
 */
@Service
@Transactional
public class RewardService {
	
	private final RewardConfigMapper rewardConfigMapper;
	@Autowired
	public RewardService(RewardConfigMapper rewardConfigMapper){
		this.rewardConfigMapper = rewardConfigMapper;
	}
	
	/**
	 * 新增
	 * @param activityId活动id,standardIds
	 * @return
	 */
	public boolean create(RewardConfig rewardConfig){
		
		String couponIds = rewardConfig.getCouponConfigIds();
		Long activityId = rewardConfig.getActivityId();
		
		if(StringUtils.isBlank(couponIds)){
			return false;
		}
		List<Long> ids = Arrays.asList(couponIds.split(",")).stream()
								.map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
		duplicate(ids,activityId);
		if(ids.isEmpty()){
			return false;
		}
		//构建参数
		RewardConfig reward = null;
		List<RewardConfig> list = new ArrayList<>();
		for(Long id : ids){
			reward = new RewardConfig();
			reward.setCouponConfigId(id);
			reward.setActivityId(activityId);
			reward.setRewardAmount(1);//默认设置为奖励一张
			
			list.add(reward);
		}
		return rewardConfigMapper.insert(list)>0;
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
		rewardConfigMapper.removeInbatch(list);
	}
	
	/**
	 * 修改rewardConfig
	 * @param 
	 * @return
	 */
	public boolean update(RewardConfig rewardConfig){
		//修改初始时候修改库存时，剩余数量与库存数相等
		return rewardConfigMapper.update(rewardConfig)>0;
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public RewardCoupon reward(Long id){
		return rewardConfigMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(RewardConfigGridParam param){
		int count = rewardConfigMapper.pageQueryCount(param);
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
		List<RewardCoupon> flashsales = rewardConfigMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(flashsales);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 去重复的
	 * @param couponIds
	 * @param id活动id
	 */
	public void duplicate(List<Long> couponIds,Long id){
		List<RewardCoupon> list = rewardConfigMapper.selectByActivity(id);
		if(list.isEmpty()) return ;
		for(int i=couponIds.size()-1;i>=0;i--){
			for(RewardCoupon rc : list){
				if(Objects.equals(couponIds.get(i), rc.getCouponConfigId())){
					couponIds.remove(i);
				}
			}
		}
	}
}
