package com.lhiot.mall.wholesale.goods.service;

import java.util.ArrayList;
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
import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.domain.PlateGoods;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsStandardGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.PlateGoodsMapper;

/**GoodsUnitService
 * 商品中心
 * @author lynn
 *
 */
@Service
@Transactional
public class PlateGoodsService {
	
	private final PlateGoodsMapper plateGoodsMapper;
	
	@Autowired
	public PlateGoodsService(PlateGoodsMapper plateGoodsMapper){
		this.plateGoodsMapper = plateGoodsMapper;
	}
	
	/**
	 * 新增版块商品
	 * @param goodsUnit
	 * @return
	 */
	public boolean create(PlateGoods param){
		String[] standardIds = param.getGoodsStandardIds().split(",");
		Long plateId = param.getId();
		PlateGoods plateGoods = null;
		List<PlateGoods> list = new ArrayList<>();
		for(String str : standardIds){
			plateGoods = new PlateGoods();
			plateGoods.setPlateId(plateId);
			plateGoods.setGoodsStandardId(Long.parseLong(str));
			list.add(plateGoods);
		}
		return plateGoodsMapper.insertInbatch(list)>0;
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
		plateGoodsMapper.removeInbatch(list);
	}
	
	/**
	 * 修改商品版块
	 * @param plateGoods
	 * @return
	 */
	public boolean updatePlate(PlateGoods plateGoods){
		return plateGoodsMapper.update(plateGoods)>0;
	}
	
	/**
	 * 根据id查询商品版块商品
	 * @param id
	 * @return
	 */
	public GoodsStandard plateGoods(Long id){
		return plateGoodsMapper.select(id);
	}
	
	/**
	 * 查询所有的商品版块商品
	 * @return
	 */
	public List<GoodsStandard> search(){
		return plateGoodsMapper.search();
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(GoodsStandardGirdParam param){
		int count = plateGoodsMapper.pageQueryCount(param);
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
		List<GoodsStandard> goodsUnits = plateGoodsMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goodsUnits);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
}
