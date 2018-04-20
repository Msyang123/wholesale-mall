package com.lhiot.mall.wholesale.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhiot.mall.wholesale.goods.mapper.GoodsKeywordsMapper;

/**GoodsService
 * 商品中心
 * @author yj
 *
 */
@Service
@Transactional
public class GoodsKeywordService {
	
	private final GoodsKeywordsMapper goodsKeywordsMapper;
	@Autowired
	public GoodsKeywordService(GoodsKeywordsMapper goodsKeywordsMapper){
		this.goodsKeywordsMapper = goodsKeywordsMapper;
	}
}
