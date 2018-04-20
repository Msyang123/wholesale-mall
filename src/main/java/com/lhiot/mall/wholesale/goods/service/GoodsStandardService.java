package com.lhiot.mall.wholesale.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhiot.mall.wholesale.goods.mapper.GoodsStandardMapper;

/**GoodsService
 * 商品中心
 * @author yj
 *
 */
@Service
@Transactional
public class GoodsStandardService {
	
	private final GoodsStandardMapper goodsStandardMapper;
	
	@Autowired
	public GoodsStandardService(GoodsStandardMapper goodsStandardMapper){
		this.goodsStandardMapper = goodsStandardMapper;
	}
	
	
}
