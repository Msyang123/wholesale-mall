package com.lhiot.mall.wholesale.advertise.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.advertise.domain.Advertise;
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
	
	@Autowired
	public AdvertiseService(AdvertiseMapper advertiseMapper){
		this.advertiseMapper = advertiseMapper;
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
}
