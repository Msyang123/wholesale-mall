package com.lhiot.mall.wholesale.advertise.service;

import java.util.ArrayList;
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
import com.lhiot.mall.wholesale.activity.service.ActivityService;
import com.lhiot.mall.wholesale.advertise.domain.Advertise;
import com.lhiot.mall.wholesale.advertise.domain.AdvertiseType;
import com.lhiot.mall.wholesale.advertise.domain.gridparam.AdvertiseGirdParam;
import com.lhiot.mall.wholesale.advertise.mapper.AdvertiseMapper;
import com.lhiot.mall.wholesale.base.PageQueryObject;

/**AdvertiseService
 * 广告中心
 * @author lynn
 *
 */
@Service
@Transactional
public class AdvertiseService {
	
	private final AdvertiseMapper advertiseMapper;
	private final ActivityService activityService;
	
	@Autowired
	public AdvertiseService(AdvertiseMapper advertiseMapper,
			ActivityService activityService){
		this.advertiseMapper = advertiseMapper;
		this.activityService = activityService;
	}
	
	/**
	 * 新增单位
	 * @param goodsUnit
	 * @return
	 */
	public boolean create(Advertise advertise){
		return advertiseMapper.insert(advertise)>0;
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
		advertiseMapper.removeInbatch(list);
	}
	
	/**
	 * 修改广告
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(Advertise advertise){
		return advertiseMapper.update(advertise)>0;
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Advertise advertise(Long id){
		return advertiseMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(AdvertiseGirdParam param){
		int count = advertiseMapper.pageQueryCount(param);
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
		List<Advertise> goodsUnits = advertiseMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goodsUnits);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 根据广告类型查询广告
	 * @param type
	 * @return
	 */
	public List<Advertise> findByType(AdvertiseType type){
		//如果是限时抢购判断当前是否存在活动
		List<Advertise> result = new ArrayList<>();
		List<Advertise> flashActivies = advertiseMapper.findByType(type.toString());
		//如果存在广告则,返回空
		if(flashActivies.isEmpty()){
			return result;
		}
		
		if(AdvertiseType.flashSale.equals(type)){
			Activity flashActivity = activityService.currentActivity(ActivityType.flashsale);
			//如果不存在活动，则返回空
			if(Objects.isNull(flashActivity)){
				return result;
			}
			//限时抢购就是一张广告图片
			result.add(flashActivies.get(0));
			return result;
		}
		//轮播图返回多张
		if(AdvertiseType.sowing.equals(type)){
			return flashActivies;
		}
		//其他取一则广告
		result.add(flashActivies.get(0));
		return result;
	}
}
