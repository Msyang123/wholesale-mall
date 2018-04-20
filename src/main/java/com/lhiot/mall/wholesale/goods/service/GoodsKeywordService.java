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
import com.lhiot.mall.wholesale.goods.domain.GoodsKeywords;
import com.lhiot.mall.wholesale.goods.domain.girdparam.KeywordsGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsKeywordsMapper;
import com.lhiot.mall.wholesale.util.PageUtil;

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
	/**
	 * 新增关键词
	 * @param goodsUnit
	 * @return
	 */
	public boolean create(GoodsKeywords goodsKeywords){
		return goodsKeywordsMapper.insert(goodsKeywords)>0;
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
		goodsKeywordsMapper.removeInbatch(list);
	}
	
	/**
	 * 修改关键词
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(GoodsKeywords goodsKeywords){
		return goodsKeywordsMapper.update(goodsKeywords)>0;
	}
	
	/**
	 * 根据id查询关键词信息
	 * @param id
	 * @return
	 */
	public GoodsKeywords GoodsKeywords(Long id){
		return goodsKeywordsMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public ArrayObject<PageObject> pageQuery(KeywordsGirdParam param){
		int count = goodsKeywordsMapper.pageQueryCount(param);
		List<GoodsKeywords> goods = goodsKeywordsMapper.pageQuery(param);
		return PageUtil.query(param, count, goods);
	}
}
