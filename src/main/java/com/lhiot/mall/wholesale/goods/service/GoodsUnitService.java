package com.lhiot.mall.wholesale.goods.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.common.wrapper.PageObject;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.GoodsCategory;
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
	 * 分页查询
	 * @return
	 */
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
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 查询编码是否重复，进而判断是否可以进行修改和增加操作
	 * @param goodsCategory
	 * @return true允许操作，false 不允许操作
	 */
	public boolean allowOperation(GoodsUnit goodsUnit){
		boolean success = true;
		List<GoodsUnit> gcs = goodsUnitMapper.findByCode(goodsUnit.getUnitCode());
		Long id = goodsUnit.getId();
		//如果不存在重复的编码
		if(gcs.isEmpty()){
			return success;
		}
		//存在重复的编码,则判断是否为本身
		if(null == id){
			success = false;
			return success;
		}
		for(GoodsUnit gc : gcs){
			Long uId = gc.getId();
			if(!Objects.equals(uId, id)){
				success = false;
				break;
			}
		}
		return success;
	}
	
	/**
	 * 判断是否可以进行，1 商品中存在该单位不能被删除；
	 * 2 商品规格中存在该单位不能被删除；
	 * @param ids
	 * @return
	 */
	public String canDelete(String ids){
		String result = "ok";//运行删除
		List<Long> list = Arrays.asList(ids.split(",")).stream()
								.map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
		List<Long> uIds = new ArrayList<>();
		//查询商品和规格中的单位
		List<Long> guids = goodsUnitMapper.searchFromGoods();
		for(Long id : list){
			for(Long gId : guids){
				if(Objects.equals(gId, id)){
					uIds.add(id);
				}
			}
		}
		if(uIds.isEmpty()){
			return result;
		}
		List<GoodsUnit> goodsUnits = goodsUnitMapper.searchInbatch(uIds);
		for(GoodsUnit gsc : goodsUnits){
			result = result.concat(","+ gsc.getUnitName());
		}
		return result.substring(result.indexOf(',')+1, result.length());
	}
}
