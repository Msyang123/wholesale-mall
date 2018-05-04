package com.lhiot.mall.wholesale.coupon.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.coupon.domain.CouponEntityResult;
import com.lhiot.mall.wholesale.coupon.domain.CouponEntity;
import com.lhiot.mall.wholesale.coupon.domain.UserCouponParam;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;
import com.lhiot.mall.wholesale.coupon.mapper.CouponEntityMapper;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.UserService;

@Service
@Transactional
public class CouponEntityService {

    private final CouponEntityMapper couponEntityMapper;
    private final UserService userService;
    public CouponEntityService(CouponEntityMapper couponEntityMapper,
    		UserService userService) {
        this.couponEntityMapper = couponEntityMapper;
        this.userService = userService;
    }

	/**
	 * 新增
	 * @param couponConfig
	 * @return
	 */
	public boolean create(CouponEntity couponEntityParam){
		return couponEntityMapper.insert(couponEntityParam)>0;
	}
	
	/**
	 * 批量新增
	 * @param couponConfig
	 * @return
	 */
	public boolean createInbatch(List<CouponEntity> couponEntityParams){
		return couponEntityMapper.insertBatch(couponEntityParams)>0;
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
	 * 修改广告
	 * @param goodsUnit
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
	public CouponEntityResult couponConfig(Long id){
		CouponEntityResult coupon = couponEntityMapper.select(id);
		User user = userService.user(id);
		//组装用户信息
		this.constructUser(user, coupon);
		return couponEntityMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(CouponGridParam param){
		String phone = param.getPhone();
		List<Long> userIds = null;
		//通过电话号码查询
		if(StringUtils.isNotBlank(phone)){
			//根据电话查询用户信息
			List<User> users = userService.fuzzySearch(phone);
			userIds = new ArrayList<>();
			for(User user : users){
				userIds.add(user.getId());
			}
		}
		if(null != userIds){
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
		List<User> users = userService.users(uIds);
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
	 * @param user
	 * @param coupongEntity
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
/*	
	
	public List<CouponEntity> contructUserCoupon(Long userId,List<Long> ){
		
	}*/
}

