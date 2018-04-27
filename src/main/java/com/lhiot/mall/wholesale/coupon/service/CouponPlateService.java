package com.lhiot.mall.wholesale.coupon.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.coupon.domain.CouponPlate;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;
import com.lhiot.mall.wholesale.coupon.mapper.CouponPlateMapper;

@Service
@Transactional
public class CouponPlateService {

    private final CouponPlateMapper couponPlateMapper;

    public CouponPlateService(CouponPlateMapper couponPlateMapper) {
        this.couponPlateMapper = couponPlateMapper;
    }

	/**
	 * 新增
	 * @param goodsUnit
	 * @return
	 */
	public boolean create(CouponPlate couponPlate){
		return couponPlateMapper.insert(couponPlate)>0;
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
		couponPlateMapper.removeInbatch(list);
	}
	
	/**
	 * 修改广告
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(CouponPlate couponPlate){
		return couponPlateMapper.update(couponPlate)>0;
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public CouponPlate couponPlate(Long id){
		return couponPlateMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(CouponGridParam param){
		int count = couponPlateMapper.pageQueryCount(param);
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
		List<CouponPlate> goodsUnits = couponPlateMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goodsUnits);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
}

