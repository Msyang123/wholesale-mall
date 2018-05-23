package com.lhiot.mall.wholesale.goods.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.GoodsKeywords;
import com.lhiot.mall.wholesale.goods.domain.KeywordsType;
import com.lhiot.mall.wholesale.goods.domain.girdparam.KeywordsGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsKeywordsMapper;

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
	public PageQueryObject pageQuery(KeywordsGirdParam param){
		int count = goodsKeywordsMapper.pageQueryCount(param);
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
		List<GoodsKeywords> goods = goodsKeywordsMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goods);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 查询商品类型或品类类型的关键词、热搜
	 * @param keyword 关键词
	 * @param type 关键词类型
	 * @param hotSearch 是否为热搜商品
	 * @return
	 */
	public List<GoodsKeywords> keywords(String keyword,KeywordsType type,
			Boolean hotSearch){
		String kwType = null;
		String hs = "no";
		if(!Objects.isNull(type)){
			kwType = type.toString();
		}
		if(!Objects.isNull(hotSearch)){
			hs = hotSearch ? "yes" : "no";
		}
		Map<String,Object> param = ImmutableMap.of("keyword", keyword, "kwType", kwType, 
				"hotSearch", hs);
		return goodsKeywordsMapper.keywords(param);
	}
}
