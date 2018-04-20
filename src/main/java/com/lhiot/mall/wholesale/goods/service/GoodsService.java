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
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsMapper;
import com.lhiot.mall.wholesale.util.PageUtil;

/**GoodsService
 * 商品中心
 * @author yj
 *
 */
@Service
@Transactional
public class GoodsService {
	
	private final GoodsMapper goodsMapper;
	@Autowired
	public GoodsService(GoodsMapper goodsMapper){
		this.goodsMapper = goodsMapper;
	}
	
	/**
	 * 新增商品
	 * @param goodsUnit
	 * @return
	 */
	public boolean create(Goods goods){
		return goodsMapper.insert(goods)>0;
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
		goodsMapper.removeInbatch(list);
	}
	
	/**
	 * 修改商品
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(Goods goods){
		return goodsMapper.update(goods)>0;
	}
	
	/**
	 * 根据id查询商品信息
	 * @param id
	 * @return
	 */
	public Goods goods(Long id){
		return goodsMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public ArrayObject<PageObject> pageQuery(GoodsGirdParam param){
		int count = goodsMapper.pageQueryCount(param);
		List<Goods> goods = goodsMapper.pageQuery(param);
		return PageUtil.query(param, count, goods);
	}
}
