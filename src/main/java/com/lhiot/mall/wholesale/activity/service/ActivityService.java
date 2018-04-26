package com.lhiot.mall.wholesale.activity.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.activity.domain.Activity;
import com.lhiot.mall.wholesale.activity.domain.ActivityType;
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
		activity.setVaildInt(activity.getVaild()?1:0);
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
		activity.setVaildInt(activity.getVaild()?1:0);
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
		List<Activity> goodsUnits = activityMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goodsUnits);
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
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
		for(Activity activity : activities){
			LocalDate beginTime = LocalDate.parse(activity.getStartTime(), formatter);
			boolean afterBeginTime = currentTime.isAfter(beginTime);
			//如果活动已经开启或者当前时间大于活动开始时间，则不能删除活动
			boolean vailid = activity.getVaild();
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
		boolean vaild = activity.getVaild();
		//如果是关闭状态是可以操作的
		if(!vaild){
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
	public Activity findActivityByType(ActivityType type){
		return activityMapper.currentActivity(type.toString());
	}
}