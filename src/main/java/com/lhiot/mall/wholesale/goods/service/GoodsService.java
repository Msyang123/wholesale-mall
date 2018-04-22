package com.lhiot.mall.wholesale.goods.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.lhiot.mall.wholesale.goods.domain.GoodsFlashsale;
import com.lhiot.mall.wholesale.goods.domain.GoodsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.common.wrapper.PageObject;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsMapper;

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
		int page = param.getPage();
		int rows = param.getRows();
		//起始行
		param.setStart((page-1)*rows);
		//总记录数
		int totalPages = (count%rows==0?count/rows:count/rows+1);
		if(totalPages < page){
			param.setPage(1);
			param.setStart(0);
		}
		List<Goods> goods = goodsMapper.pageQuery(param);
		PageObject obj = new PageObject();
		obj.setPage(param.getPage());
		obj.setRows(param.getRows());
		obj.setSidx(param.getSidx());
		obj.setSord(param.getSord());
		return ArrayObject.of(goods, obj);
	}


	public GoodsInfo goodsInfo(long id){
		return goodsMapper.goodsInfo(id);
	}

	public GoodsFlashsale goodsFlashsale(long goodsStandardId){
		return goodsMapper.goodsFlashsale(goodsStandardId);
	}

	public List<GoodsInfo> inventoryList(long userId){
		return goodsMapper.inventoryList(userId);
	}

	public List<GoodsInfo> recommendList(long plateId){
		return goodsMapper.inventoryList(plateId);
	}
}
