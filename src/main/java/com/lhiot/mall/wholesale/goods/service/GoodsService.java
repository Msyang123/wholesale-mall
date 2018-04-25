package com.lhiot.mall.wholesale.goods.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.GoodsCategory;
import com.lhiot.mall.wholesale.goods.domain.GoodsFlashsale;
import com.lhiot.mall.wholesale.goods.domain.GoodsInfo;
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
	public PageQueryObject pageQuery(GoodsGirdParam param){
		int count = goodsMapper.pageQueryCount(param);
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
		List<Goods> goods = goodsMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goods);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
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
	
	/**
	 * 根据商品分类id批量查询商品
	 * @param list
	 * @return
	 */
	public List<Goods> findGoodsByCategory(List<Long> list){
		return goodsMapper.searchByCategory(list);
	}
	
	/**
	 * 查询编码是否重复，进而判断是否可以进行修改和增加操作
	 * @param goodsCategory
	 * @return true允许操作，false 不允许操作
	 */
	public boolean allowOperation(Goods goods){
		boolean success = true;
		List<Goods> gcs = goodsMapper.searchByCode(goods.getGoodsCode());
		Long id = goods.getId();
		//如果不存在重复的编码
		if(gcs.isEmpty()){
			return success;
		}
		//存在重复的编码,则判断是否为本身
		if(null == id){
			success = false;
			return success;
		}
		for(Goods gc : gcs){
			Long categoryId = gc.getId();
			if(!Objects.equals(categoryId, id)){
				success = false;
				break;
			}
		}
		return success;
	}
}
