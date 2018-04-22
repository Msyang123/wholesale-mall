package com.lhiot.mall.wholesale.goods.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.common.wrapper.PageObject;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.goods.domain.GoodsPriceRegion;
import com.lhiot.mall.wholesale.goods.domain.girdparam.PriceRegionGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsPriceRegionMapper;
import com.lhiot.mall.wholesale.util.PageUtil;

/**GoodsPriceRegionService
 * 商品中心
 * @author lynn
 *
 */
@Service
@Transactional
public class GoodsPriceRegionService {
	
	private final GoodsPriceRegionMapper goodsPriceRegionMapper;
	
	@Autowired
	public GoodsPriceRegionService(GoodsPriceRegionMapper goodsPriceRegionMapper){
		this.goodsPriceRegionMapper = goodsPriceRegionMapper;
	}
	
	/**
	 * 新增价格区间
	 * @param goodsUnit
	 * @return
	 */
	public boolean create(GoodsPriceRegion goodsPriceRegion){
		return goodsPriceRegionMapper.insert(goodsPriceRegion)>0;
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
		goodsPriceRegionMapper.removeInbatch(list);
	}
	
	/**
	 * 修改价格区间
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(GoodsPriceRegion goodsPriceRegion){
		return goodsPriceRegionMapper.update(goodsPriceRegion)>0;
	}
	
	/**
	 * 根据id查询价格区间信息
	 * @param id
	 * @return
	 */
	public GoodsPriceRegion GoodsPriceRegion(Long id){
		return goodsPriceRegionMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public ArrayObject<PageObject> pageQuery(PriceRegionGirdParam param){
		int count = goodsPriceRegionMapper.pageQueryCount(param);
		List<GoodsPriceRegion> goods = goodsPriceRegionMapper.pageQuery(param);
		return PageUtil.query(param, count, goods);
	}


	public List<GoodsPriceRegion> selectPriceRegion(long goodsStandardId){
		return goodsPriceRegionMapper.selectPriceRegion(goodsStandardId);
	}
	
}
