package com.lhiot.mall.wholesale.goods.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.CategoryTree;
import com.lhiot.mall.wholesale.goods.domain.GoodsCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsCategoryGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.GoodsCategoryMapper;

/**
 * 商品中心
 * @author lynn
 *
 */
@Service
@Transactional
public class GoodsCategoryService {
	
	private final GoodsCategoryMapper goodsCategoryMapper;
	
	@Autowired
	public GoodsCategoryService(GoodsCategoryMapper goodsCategoryMapper){
		this.goodsCategoryMapper = goodsCategoryMapper;
	}
	
	/**
	 * 新增关键词
	 * @param GoodsCategory
	 * @return
	 */
	public boolean create(GoodsCategory goodsCategory){
		return goodsCategoryMapper.insert(goodsCategory)>0;
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
		goodsCategoryMapper.removeInbatch(list);
	}
	
	/**
	 * 修改关键词
	 * @param GoodsCategory
	 * @return
	 */
	public boolean update(GoodsCategory goodsCategory){
		return goodsCategoryMapper.update(goodsCategory)>0;
	}
	
	/**
	 * 根据id查询关键词信息
	 * @param id
	 * @return
	 */
	public GoodsCategory goodsCategory(Long id){
		return goodsCategoryMapper.select(id);
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageQueryObject pageQuery(GoodsCategoryGirdParam param){
		int count = goodsCategoryMapper.pageQueryCount(param);
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
		List<GoodsCategory> goods = goodsCategoryMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goods);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(count);
		return result;
	}
	
	/**
	 * 获取分类的树结构
	 * @return
	 */
	public List<CategoryTree> tree(){
		List<CategoryTree> result = new ArrayList<>();
		List<GoodsCategory> list = goodsCategoryMapper.findTree();
		CategoryTree categoryTree = null;
		for(GoodsCategory g : list){
			categoryTree = new CategoryTree();
			categoryTree.setId(g.getId());
			categoryTree.setPId(g.getParentId());
			categoryTree.setName(g.getCategoryName());
			categoryTree.setParentClassName(g.getParentCategoryName());
			categoryTree.setIsParent(g.getParentId().toString().equals("0")?true:false);
			categoryTree.setLevel(g.getLevels());
			result.add(categoryTree);
		}
		return result;
	}
}
