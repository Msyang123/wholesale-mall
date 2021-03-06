package com.lhiot.mall.wholesale.goods.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.GoodsMinPrice;
import com.lhiot.mall.wholesale.goods.domain.GoodsPriceRegion;
import com.lhiot.mall.wholesale.goods.domain.girdparam.PriceRegionGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsPriceRegionMapper;

/**GoodsPriceRegionService
 * 商品中心
 * @author lynn
 *
 */
@Service
@Transactional
public class GoodsPriceRegionService {
	
	private final GoodsPriceRegionMapper goodsPriceRegionMapper;
	
	@Autowired
	public GoodsPriceRegionService(GoodsPriceRegionMapper goodsPriceRegionMapper){
		this.goodsPriceRegionMapper = goodsPriceRegionMapper;
	}
	
	/**
	 * 新增价格区间
	 * @param goodsUnit
	 * @return
	 */
	public boolean create(GoodsPriceRegion goodsPriceRegion){
		return goodsPriceRegionMapper.insert(goodsPriceRegion)>0;
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
		goodsPriceRegionMapper.removeInbatch(list);
	}
	
	/**
	 * 修改价格区间
	 * @param goodsUnit
	 * @return
	 */
	public boolean update(GoodsPriceRegion goodsPriceRegion){
		return goodsPriceRegionMapper.update(goodsPriceRegion)>0;
	}
	
	/**
	 * 根据id查询价格区间信息
	 * @param id
	 * @return
	 */
	public GoodsPriceRegion GoodsPriceRegion(Long id){
		return goodsPriceRegionMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(PriceRegionGirdParam param){
		int count = goodsPriceRegionMapper.pageQueryCount(param);
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
		List<GoodsPriceRegion> goods = goodsPriceRegionMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goods);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}

	public List<GoodsPriceRegion> selectPriceRegion(long goodsStandardId){
		return goodsPriceRegionMapper.selectPriceRegion(goodsStandardId);
	}
	
	/**
	 * 根据商品id查询价格区间中的最低价格和最高价格
	 * @param goodsIds
	 * @return
	 */
	public List<GoodsMinPrice> minAndMaxPrices(List<Long> standardIds){
		return goodsPriceRegionMapper.priceRegion(standardIds);
	}
}
