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
import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsStandardGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsStandardMapper;
import com.lhiot.mall.wholesale.util.PageUtil;

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
	/**
	 * 新增商品规格
	 * @param goodsUnit
	 * @return
	 */
	public boolean create(GoodsStandard goodsStandard){
		return goodsStandardMapper.insert(goodsStandard)>0;
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
		goodsStandardMapper.removeInbatch(list);
	}
	
	/**
	 * 修改商品规格
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(GoodsStandard goodsStandard){
		return goodsStandardMapper.update(goodsStandard)>0;
	}
	
	/**
	 * 根据id查询商品规格信息
	 * @param id
	 * @return
	 */
	public GoodsStandard goodsStandard(Long id){
		return goodsStandardMapper.select(id);
	}
	
	/**
	 * 根据关键词查询商品
	 * @param keywords
	 * @return
	 */
	public List<GoodsStandard> findByKeywords(String keywords){
		return goodsStandardMapper.fuzzySearch(keywords);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public ArrayObject<PageObject> pageQuery(GoodsStandardGirdParam param){
		int count = goodsStandardMapper.pageQueryCount(param);
		List<GoodsStandard> goods = goodsStandardMapper.pageQuery(param);
		return PageUtil.query(param, count, goods);
	}
}
