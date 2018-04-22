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
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.GoodsUnit;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsUnitGridParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsUnitMapper;

/**GoodsUnitService
 * 商品中心
 * @author lynn
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
	
	/**
	 * 新增单位
	 * @param goodsUnit
	 * @return
	 */
	public boolean create(GoodsUnit goodsUnit){
		return goodsUnitMapper.insert(goodsUnit)>0;
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
		goodsUnitMapper.removeInbatch(list);
	}
	
	/**
	 * 修改商品单位
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(GoodsUnit goodsUnit){
		return goodsUnitMapper.update(goodsUnit)>0;
	}
	
	/**
	 * 根据id查询商品单位
	 * @param id
	 * @return
	 */
	public GoodsUnit goodsUnit(Long id){
		return goodsUnitMapper.select(id);
	}
	
	/**
	 * 查询所有的商品单位
	 * @return
	 */
	public List<GoodsUnit> search(){
		return goodsUnitMapper.search();
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	public GoodsUnit findByCode(String code){
		return goodsUnitMapper.findByCode(code);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageQueryObject pageQuery(GoodsUnitGridParam param){
		int count = goodsUnitMapper.pageQueryCount(param);
		int page = param.getPage();
		int rows = param.getRows();
		//起始行
		param.setStart((page-1)*rows);
		//总记录数
		int totalPages = (count%rows==0?count/rows:count/rows+1);
		if(totalPages < page){
			page = 1;
			param.setPage(page);
			param.setStart(0);
		}
		List<GoodsUnit> goodsUnits = goodsUnitMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goodsUnits);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(count);
		return result;
	}
}
