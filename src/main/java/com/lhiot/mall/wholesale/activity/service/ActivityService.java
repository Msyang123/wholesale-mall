package com.lhiot.mall.wholesale.activity.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.BeanUtils;
import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.activity.domain.Activity;
import com.lhiot.mall.wholesale.activity.domain.ActivityType;
import com.lhiot.mall.wholesale.activity.domain.FlashActivityGoods;
import com.lhiot.mall.wholesale.activity.domain.gridparam.ActivityGirdParam;
import com.lhiot.mall.wholesale.activity.mapper.ActivityMapper;
import com.lhiot.mall.wholesale.base.PageQueryObject;

/**
 * 活动中心
 * @author lynn
 *
 */
@Service
@Transactional
public class ActivityService {
	
	private final ActivityMapper activityMapper;
	
	@Autowired
	public ActivityService(ActivityMapper activityMapper){
		this.activityMapper = activityMapper;
	}
	
	/**
	 * 新增
	 * @param activity
	 * @return
	 */
	public boolean create(Activity activity){
		
		return activityMapper.insert(activity)>0;
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
		activityMapper.removeInbatch(list);
	}
	
	/**
	 * 修改广告
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(Activity activity){
		return activityMapper.update(activity)>0;
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Activity activity(Long id){
		return activityMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(ActivityGirdParam param){
		int count = activityMapper.pageQueryCount(param);
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
		List<Activity> activitys = activityMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(activitys);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	 
	/**
	 * 批量查询活动,true可以删除，false不可以删除
	 * @param list
	 * @return
	 */
	public boolean canDelete(String ids){
		boolean success = true;
		if(StringUtils.isBlank(ids)){
			success = false;
			return success;
		}
		List<Long> list = Arrays.asList(ids.split(",")).stream()
				.map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
		List<Activity> activities = activityMapper.search(list);
		LocalDate currentTime = LocalDate.now();
		for(Activity activity : activities){
			boolean afterBeginTime = this.isAfterNow(activity, currentTime);
			//如果活动已经开启或者当前时间大于活动开始时间，则不能删除活动
			boolean vailid = "yes".equals(activity.getVaild());
			if(afterBeginTime || vailid){
				success = false;
				break;
			}
		}
		return success;
	}
	
	/**
	 * 判断是否可以添加或者修改活动，同一种活动同一时间
	 * 只能添加一个开启的活动
	 * @param activity
	 * @return
	 */
	public boolean allowOperation(Activity activity){
		boolean success = true;
		//判断修改的时候，值是否发生改变
		if(!this.activityDataHasChange(activity)){
			success = false;
			return success;
		}
		if("no".equals(activity.getVaild())){
			return success;
		}
		List<Activity> activities = activityMapper.avtivityIsOpen(activity);
		//如果给定时间不存在开启的活动则可以操作
		if(activities.isEmpty()){
			return success;
		}
		int size = activities.size();
		//给定时间范围，存在多个开启的活动，则不能操作
		if(size > 1){
			success = false;
			return success;
		}
		//给定时间范围，存在一个开启的活动，则判断是否为自身,是本身则可以操作
		Activity ac = activities.get(0);
		if(!Objects.equals(activity.getId(), ac.getId())){
			success = false;
		}
		return success;
	}
	
	/**
	 * 根据活动类型查询当前开启的活动
	 * @return
	 * @param type
	 */
	public FlashActivityGoods currentActivity(ActivityType type){
		return activityMapper.currentActivity(type.toString());
	}
	
	/**
	 * 查询下期开启的活动
	 * @param type
	 * @return
	 */
	public FlashActivityGoods nextActivity(ActivityType type){
		FlashActivityGoods curActivity = this.currentActivity(type);
		Map<String,Object> param = ImmutableMap.of("activityType", ActivityType.flashsale.toString(),
				"time", curActivity.getEndTime());
		return activityMapper.nextActivity(param);
	}

	/**
	 * 根据活动id查询抢购活动商品
	 * @param activityId
	 * @return
	 */
	public Activity flashGoods(Long activityId){
		return activityMapper.flashActivity(activityId);
	}
	
	/**
	 * 当前时间在开始时间之后
	 * @param activity
	 * @return
	 */
	public boolean isAfterNow(Activity activity,LocalDate currentTime){
		String st = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(activity.getStartTime());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
		LocalDate beginTime = LocalDate.parse(st,formatter);
		boolean afterBeginTime = currentTime.isAfter(beginTime);
		return afterBeginTime;
	}
	
	/**
	 * 修改的时候判断数据是否已经改变
	 * @param activity
	 * @return
	 */
	public boolean activityDataHasChange(Activity activity){
		boolean success = true;
		Long id = activity.getId();
		//新增
		if(Objects.isNull(id)){
			return success;
		}
		//修改
		Activity activityInDb = this.activity(id);
		//如果当前时间在活动开启时间之后，则判断除却状态的其他属性是否改变
		if(!this.isAfterNow(activityInDb, LocalDate.now())){
			return success;
		}
		if("no".equals(activity.getVaild())){
			return success;
		}
		Map<String,Object> dataInParam = BeanUtils.toMap(activity);
		Map<String,Object> dataInDb = BeanUtils.toMap(activityInDb);
		//暂时不判断活动的状态值的改变
		dataInParam.remove("vaild");
		dataInDb.remove("vaild");
		//判断通一属性的值是否发生修改
		for(String key1 : dataInParam.keySet()){
			for(String key2 : dataInDb.keySet()){
				if(key1.equals(key2)){
					if(!Objects.equals(dataInParam.get(key1), 
							dataInDb.get(key2))){
						success = false;
						break;
					}
				}
			}
		}
		return success;
	}
}
