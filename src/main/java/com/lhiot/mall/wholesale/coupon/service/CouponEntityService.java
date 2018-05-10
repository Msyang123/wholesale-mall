package com.lhiot.mall.wholesale.coupon.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.activity.domain.ActivityType;
import com.lhiot.mall.wholesale.activity.domain.FlashActivityGoods;
import com.lhiot.mall.wholesale.activity.service.ActivityService;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.coupon.domain.ActivityCoupon;
import com.lhiot.mall.wholesale.coupon.domain.CouponConfig;
import com.lhiot.mall.wholesale.coupon.domain.CouponEntity;
import com.lhiot.mall.wholesale.coupon.domain.CouponEntityResult;
import com.lhiot.mall.wholesale.coupon.domain.CouponStatusType;
import com.lhiot.mall.wholesale.coupon.domain.CouponType;
import com.lhiot.mall.wholesale.coupon.domain.ReleaseCouponParam;
import com.lhiot.mall.wholesale.coupon.domain.UserCouponParam;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;
import com.lhiot.mall.wholesale.coupon.mapper.CouponEntityMapper;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;

@Slf4j
@Service
@Transactional
public class CouponEntityService {

    private final CouponEntityMapper couponEntityMapper;
    private final UserMapper userMapper;
    private final CouponConfigService couponConfigService;
    private final ActivityService activityService;
    public CouponEntityService(CouponEntityMapper couponEntityMapper,
    		CouponConfigService couponConfigService,
    		ActivityService activityService,
    		UserMapper userMapper) {
        this.couponEntityMapper = couponEntityMapper;
        this.couponConfigService = couponConfigService;
        this.activityService = activityService;
        this.userMapper = userMapper;
    }

	/**
	 * 新增
	 * @param couponEntityParam
	 * @return
	 */
	public boolean create(CouponEntity couponEntityParam){
		return couponEntityMapper.insert(couponEntityParam)>0;
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
		couponEntityMapper.removeInbatch(list);
	}

	/**
	 * 修改优惠券状态
	 * @param couponEntityParam
	 * @return
	 */
	public boolean update(CouponEntity couponEntityParam){
		return couponEntityMapper.update(couponEntityParam)>0;
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public CouponEntityResult coupon(Long id){
		return couponEntityMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(CouponGridParam param){
		String phone = param.getPhone();
		List<Long> userIds = new ArrayList<>();;
		//通过电话号码查询
		if(StringUtils.isNotBlank(phone)){
			//根据电话查询用户信息
			List<User> users = userMapper.fuzzySearchByPhone(phone);
			for(User user : users){
				userIds.add(user.getId());
			}
		}
		if(!userIds.isEmpty()){
			param.setUserIds(userIds);
		}
		int count = couponEntityMapper.pageQueryCount(param);
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
		List<CouponEntityResult> coupongEntities = couponEntityMapper.pageQuery(param);
		List<Long> uIds = this.userIds(coupongEntities);
		List<User> users = new ArrayList<>();
		if(!uIds.isEmpty()){
			users = userMapper.searchInbatch(uIds);
		}
		//组装用户数据
		this.constructUser(users, coupongEntities);
		PageQueryObject result = new PageQueryObject();
		result.setRows(coupongEntities);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 将用户信息组装到优惠券中
	 * @param user
	 * @param coupongEntity
	 * @return
	 */
	public void constructUser(User user,CouponEntityResult coupongEntity){
		if(Objects.isNull(user) || Objects.isNull(coupongEntity)){
			return ;
		}
		if(Objects.equals(user.getId(), coupongEntity.getUserId())){
			coupongEntity.setUserStatus(user.getUserStatus());
			coupongEntity.setPhone(user.getPhone());
			coupongEntity.setUserName(user.getNickname());
			coupongEntity.setShopName(user.getShopName());
			coupongEntity.setAddressDetail(user.getAddressDetail());
		}
	}
	
	/**
	 * 将用户信息组装到优惠券中
	 * @param users
	 * @param coupongEntities
	 * @return
	 */
	public void constructUser(List<User> users,List<CouponEntityResult> coupongEntities){
		for(CouponEntityResult couponEntity : coupongEntities){
			for(User user : users){
				this.constructUser(user, couponEntity);
			}
		}
	}
	
	/**
	 * 获取用户的id
	 * @param coupongEntities
	 * @return
	 */
	public List<Long> userIds(List<CouponEntityResult> coupongEntities){
		List<Long> ids = new ArrayList<>();
		coupongEntities.forEach(coupon -> {
			Long userId = coupon.getUserId();
			ids.add(userId);
		});
		return ids;
	}
	
	/**
	 * 根据用户id查询优惠券列表
	 * @return
	 */
	public List<CouponEntity> userCoupons(UserCouponParam userCouponParam){
		List<CouponEntity> list = couponEntityMapper.searchByUser(userCouponParam);
		Integer orderFee = userCouponParam.getOrderFee();
		if(Objects.isNull(orderFee) || Objects.equals(orderFee, 0)){
			return list;
		}
		//判断优惠券是否到达使用标准,将可用的优惠按照优惠金额倒序排列
		//到达使用标准的优惠券
		List<CouponEntity> availables = new ArrayList<>();
		for(CouponEntity couponEntity : list){
			int fullFee = couponEntity.getFullFee();
			if(orderFee >= fullFee){
				couponEntity.setIsValidate(true);
				availables.add(couponEntity);
			}
		}
		if(availables.isEmpty()){
			return list;
		}
		//没有达到使用标准的优惠券
		List<CouponEntity> unavailable = list.stream().filter(m -> !m.getIsValidate())
										 .collect(Collectors.toList());
		if(!unavailable.isEmpty()){
			availables.addAll(availables.size(), unavailable);
		}
		return availables;
	}
	
	/**
	 * 构建手动发券的参数,多种单张
	 * @param userIds
	 * @param couponConfigids
	 * @return
	 */
	public List<CouponEntity> couponParam(List<Long> userIds,
			List<Long> couponConfigids){
		List<CouponEntity> params = new ArrayList<>();
		if(couponConfigids.isEmpty() || userIds.isEmpty()){
			return params;
		}
		CouponEntity param = null;
		//获取优惠券信息
		List<CouponEntity> couponEntities = new ArrayList<>();
		List<CouponConfig> couponConfigs = couponConfigService.couponConfigs(couponConfigids);
		for(CouponConfig cf : couponConfigs){
			param = new CouponEntity();
			param.setCouponConfigId(cf.getId());
			param.setCouponStatus(CouponStatusType.unused.toString());
			param.setCouponFee(cf.getCouponFee());
			param.setFullFee(cf.getFullFee());
			param.setVaildDays(cf.getVaildDays());
			param.setCouponFrom(CouponType.artificial.toString());
			couponEntities.add(param);
		}
		
		if(couponEntities.isEmpty()){
			return params;
		}
		//将用户id配置优惠券参数中
		List<CouponEntity> temp = null;
		for(Long userId : userIds){
			temp = this.cloneList(couponEntities);
			for(CouponEntity couponEntity : temp){
				couponEntity.setUserId(userId);
				params.add(couponEntity);
			}
		}
		return params;
	}
	
	/**
	 * 领取或者活动发券，构建数据；多种多张
	 * @param activityId
	 * @param userId
	 * @return
	 */
	public List<CouponEntity> activityCoupons(Long userId,Long activityId){
		List<CouponEntity> params = new ArrayList<>();
		if(Objects.isNull(userId) || Objects.isNull(activityId)) {
			return params;
		}
		List<ActivityCoupon> acs = couponConfigService.activityCoupons(activityId);
		if(acs.isEmpty()){
			return params;
		}
		CouponEntity coupon = null;
		//多种优惠券
		for(ActivityCoupon ac : acs){
			int count = ac.getRewardAmount();
			coupon = new CouponEntity();
			coupon.setCouponConfigId(ac.getCouponConfigId());
			coupon.setCouponStatus(CouponStatusType.unused.toString());
			coupon.setCouponFee(ac.getCouponFee());
			coupon.setFullFee(ac.getFullFee());
			coupon.setVaildDays(ac.getVaildDays());
			coupon.setUserId(userId);
			coupon.setCouponFrom(CouponType.activity.toString());
			//多种优惠券
			for(int i=0;i< count;i++){
				params.add(coupon);
			}
		}
		return params;
	}
	
	/**
	 * 活动发放优惠券或者领取优惠券
	 * @param userId
	 * @param type
	 * @return
	 */
	public boolean addCoupon(Long userId,ActivityType type){
		boolean success = false;
		FlashActivityGoods ac = activityService.currentActivity(type);
		if(Objects.isNull(ac)){
			return success;
		}
		List<CouponEntity> params = this.activityCoupons(userId, ac.getId());
		if(params.isEmpty()){
			return success;
		}
		int count = couponEntityMapper.insertBatch(params);
		return count > 0;
	}


	@RabbitHandler
	@RabbitListener(queues = "add-coupon")
	public void couponPublisher(String message){
		log.info("add-coupon:"+message);
		this.addCoupon(Long.valueOf(message),ActivityType.registration);
	}
	/**
	 * 手动发券
	 * @param param
	 * @return
	 */
	public String releaseCoupon(ReleaseCouponParam param){
		String failureUser = "failure";
		if(Objects.isNull(param)){
			return failureUser;
		}
		List<String> pls = Arrays.asList(param.getPhones().split(","));
		List<Long> ccIds = Arrays.asList(param.getCouponConfigIds().split(",")).stream()
								 .map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
		//获取用户id
		List<User> users = userMapper.searchByPhones(pls);
		if(users.isEmpty()){
			return failureUser;
		}
		
		//获取userId的集合
		List<Long> userIds = new ArrayList<>();
		for(User user : users){
			userIds.add(user.getId());
		}
		
		//批量发放优惠券
		List<CouponEntity> params = this.couponParam(userIds, ccIds);
		boolean success = couponEntityMapper.insertBatch(params)>0;
		if(!success){
			return failureUser;
		}
		
		if(Objects.equals(pls.size(), users.size())){
			failureUser = "ok";
			return failureUser;
		}
		//获取发放失败的用户
		List<String> failure = new ArrayList<>();
        for(String phone:pls) {  
            boolean flag = false;  
            for(User user:users) {  
            	String po = user.getPhone();
                if(phone.equals(po)) {  
                    flag = true;  
                    break;  
                }  
            }  
            if(!flag){  
            	failure.add(phone);
            }  
        } 
		failureUser = StringUtils.collectionToDelimitedString(failure,",");
		return failureUser;
	}
    
	/**
	 * 深度拷贝一个对象
	 * @param source
	 * @return
	 */
    public List<CouponEntity> cloneList(List<CouponEntity> source){
    	List<CouponEntity> result = new ArrayList<>();
    	CouponEntity target = null;
    	for(CouponEntity coupon : source){
    		target = new CouponEntity();
    		BeanUtils.copyProperties(coupon, target);
    		result.add(target);
    	}
    	return result;
    }
}

