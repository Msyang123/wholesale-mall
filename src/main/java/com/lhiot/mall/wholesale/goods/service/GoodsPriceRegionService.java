package com.lhiot.mall.wholesale.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhiot.mall.wholesale.goods.mapper.GoodsPriceRegionMapper;

/**GoodsService
 * 商品中心
 * @author yj
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
	
	
}
