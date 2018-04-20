package com.lhiot.mall.wholesale.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhiot.mall.wholesale.goods.mapper.GoodsCategoryMapper;

/**
 * 商品中心
 * @author yj
 *
 */
@Service
@Transactional
public class GoodsCategoryService {
	
	private final GoodsCategoryMapper goodsCategoryMapper;
	
	@Autowired
	public GoodsCategoryService(GoodsCategoryMapper goodsCategoryMapper){
		this.goodsCategoryMapper = goodsCategoryMapper;
	}
	
	
}
