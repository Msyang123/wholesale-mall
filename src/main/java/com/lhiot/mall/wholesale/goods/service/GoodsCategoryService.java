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

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.CategoryTree;
import com.lhiot.mall.wholesale.goods.domain.Goods;
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
	private final GoodsService goodsService;
	private static final String ALLOW = "ok";//允许删除
	
	@Autowired
	public GoodsCategoryService(GoodsCategoryMapper goodsCategoryMapper,
			GoodsService goodsService){
		this.goodsCategoryMapper = goodsCategoryMapper;
		this.goodsService = goodsService;
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
		GoodsCategory goodsCategory = goodsCategoryMapper.select(id);
		Long parentId = goodsCategory.getParentId();
		if(parentId == 0){
			goodsCategory.setParentCategoryName("所有分类");
			goodsCategory.setParentCategoryCode("000");
		}else{
			GoodsCategory parent = goodsCategoryMapper.select(parentId);
			goodsCategory.setParentCategoryName(parent.getCategoryName());
			goodsCategory.setParentCategoryCode(parent.getCategoryCode());
		}
		return goodsCategory;
	}
	
	/**
	 * 分页查询
	 * @return
	 */
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
		result.setTotal(totalPages);
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
			categoryTree.setClassCode(g.getCategoryCode());
			categoryTree.setParentClassName(g.getParentCategoryName());
			categoryTree.setIsParent(g.getParentId().toString().equals("0")?true:false);
			categoryTree.setLevel(g.getLevels());
			result.add(categoryTree);
		}
		return result;
	}
	
	/**
	 * 查询编码是否重复，进而判断是否可以进行修改和增加操作
	 * @param goodsCategory
	 * @return true允许操作，false 不允许操作
	 */
	public boolean allowOperation(GoodsCategory goodsCategory){
		boolean success = true;
		List<GoodsCategory> gcs = goodsCategoryMapper.selectByCode(goodsCategory.getCategoryCode());
		Long id = goodsCategory.getId();
		//如果不存在重复的编码
		if(gcs.isEmpty()){
			return success;
		}
		//存在重复的编码,则判断是否为本身
		if(null == id){
			success = false;
			return success;
		}
		for(GoodsCategory gc : gcs){
			Long categoryId = gc.getId();
			if(!Objects.equals(categoryId, id)){
				success = false;
				break;
			}
		}
		return success;
	}
	
	/**
	 * 判断是否可以进行，1 商品中存在该分类不能被删除；2 该分类下存在子分类不能被删除；
	 * @param ids
	 * @return
	 */
	public String canDelete(String ids){
		String result = ALLOW;
		List<Long> list = Arrays.asList(ids.split(",")).stream()
								.map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
		Set<Long> set = new HashSet<>();
		//查询当前分类下是否存在子分类
		List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectByParent(list);
		for(Long id : list){
			for(GoodsCategory goodsCategory : goodsCategories){
				Long parentId = goodsCategory.getParentId();
				if(Objects.equals(parentId, id)){
					set.add(id);
				}
			}
		}
		//查询当前分类下是否存在商品
		List<Goods> goodses = goodsService.findGoodsByCategory(list);
		for(Long id : list){
			for(Goods goods : goodses){
				Long categoryId = goods.getCategoryId();
				if(Objects.equals(categoryId, id)){
					set.add(id);
				}
			}
		}
		if(set.isEmpty()){
			return result;
		}
		goodsCategories = goodsCategoryMapper.search(new ArrayList<>(set));
		for(GoodsCategory gsc : goodsCategories){
			result = result.concat(","+ gsc.getCategoryName());
		}
		return result.substring(result.indexOf(',')+1, result.length());
	}
	
	/**
	 * 根据父节点id查询分类
	 * @param parentId
	 * @return
	 */
	public List<GoodsCategory> findCategories(Long parentId){
		List<GoodsCategory> goodsCategories = goodsCategoryMapper.searchAll(parentId);
		if(Objects.equals(parentId, 0) || goodsCategories.isEmpty()){
			return goodsCategories;
		}
		//如果是子节点，则第一子节点的商品查询出来，作为默认显示
		GoodsCategory goodsCategory = goodsCategories.get(0);
		Long categoryId = goodsCategory.getId();
		List<Goods> goods = goodsService.findByCategory(categoryId);
		goodsCategory.setCategoryGoods(goods);
		return goodsCategories;
	}
}
