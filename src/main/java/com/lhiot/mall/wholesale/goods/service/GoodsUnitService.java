package com.lhiot.mall.wholesale.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhiot.mall.wholesale.goods.mapper.GoodsUnitMapper;

/**GoodsService
 * 商品中心
 * @author yj
 *
 */
@Service
@Transactional
public class GoodsUnitService {
	
	private final GoodsUnitMapper goodsUnitMapper;
	
	@Autowired
	public GoodsUnitService(GoodsUnitMapper goodsUnitMapper){
		this.goodsUnitMapper = goodsUnitMapper;
	}
	
	
}
