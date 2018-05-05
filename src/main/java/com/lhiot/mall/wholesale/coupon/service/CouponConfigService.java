package com.lhiot.mall.wholesale.coupon.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.coupon.domain.ActivityCoupon;
import com.lhiot.mall.wholesale.coupon.domain.CouponConfig;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;
import com.lhiot.mall.wholesale.coupon.mapper.CouponConfigMapper;

@Service
@Transactional
public class CouponConfigService {

    private final CouponConfigMapper couponConfigMapper;

    public CouponConfigService(CouponConfigMapper couponConfigMapper) {
        this.couponConfigMapper = couponConfigMapper;
    }

	/**
	 * 新增
	 * @param couponConfig
	 * @return
	 */
	public boolean create(CouponConfig couponConfig){
		return couponConfigMapper.insert(couponConfig)>0;
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
		couponConfigMapper.removeInbatch(list);
	}
	
	/**
	 * 修改广告
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(CouponConfig couponConfig){
		return couponConfigMapper.update(couponConfig)>0;
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public CouponConfig couponConfig(Long id){
		return couponConfigMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(CouponGridParam param){
		int count = couponConfigMapper.pageQueryCount(param);
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
		List<CouponConfig> goodsUnits = couponConfigMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goodsUnits);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 优惠券配置查询
	 * @param ids
	 * @return
	 */
	public List<CouponConfig> couponConfigs(List<Long> ids){
		return couponConfigMapper.search(ids);
	}
	
	/**
	 * 查询活动配置的优惠券
	 * @param activityId
	 * @return
	 */
	public List<ActivityCoupon> activityCoupons(Long activityId){
		return couponConfigMapper.activityCoupon(activityId);
	}
}

