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
import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.domain.QueryParam;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsStandardGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsStandardMapper;

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
	 * 根据商品id查询商品信息
	 * @param goodsId
	 * @return
	 */
	public GoodsStandard searchByGoodsId(long goodsId){
		return goodsStandardMapper.searchByGoodsId(goodsId);
	}
	
	public List<GoodsStandard> goodsStandards(List<Long> ids){
		return goodsStandardMapper.searchByIds(ids);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(GoodsStandardGirdParam param){
		int count = goodsStandardMapper.pageQueryCount(param);
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
		List<GoodsStandard> goods = goodsStandardMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goods);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 判断是否可以新增或者修改商品规格，
	 * 一种商品只有一个规格，并且barcode不能相同
	 * @param goodsStandard
	 * @return
	 */
	public boolean allowOperation(GoodsStandard goodsStandard){
		boolean success = false;
		Long id = goodsStandard.getId();
		QueryParam param = new QueryParam();
		param.setCards(goodsStandard.getGoodsId());
		//根据goodsId查询规格
		List<GoodsStandard> goods1 = goodsStandardMapper.selectByOthers(param);
		
		//根据barcode查询商品
		param.setCards(null);
		param.setBarCode(goodsStandard.getBarCode().trim());
		List<GoodsStandard> goods2 = goodsStandardMapper.selectByOthers(param);
		
		if(goods1.isEmpty() && goods2.isEmpty()){
			success = true;
			return success;
		}
		//存在重复的判断是否为新增操作
		if(Objects.isNull(id)){
			return success;
		}
		
		//判断是否存在相同的商品
		boolean flag = this.isTheSameStandard(id, goods1);
		if(!flag){
			return success;
		}
		//判断是否存在相同的barCode
		flag = this.isTheSameStandard(id, goods2);
		if(flag){
			success = true;
		}
		return success;
	}
	
	/**
	 * 判断是否同一个商品规格
	 * @param standardId
	 * @param list
	 * @return
	 */
	public boolean isTheSameStandard(Long standardId,
			List<GoodsStandard> list){
		boolean flag = true;
		if(list.isEmpty()){
			return flag;
		}
		for(GoodsStandard goods : list){
			if(!Objects.equals(standardId, goods.getId())){
				flag = false;
				break;
			}
		}
		return flag;
	}
}
