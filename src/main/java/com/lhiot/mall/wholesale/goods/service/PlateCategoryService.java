package com.lhiot.mall.wholesale.goods.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.CategoryTree;
import com.lhiot.mall.wholesale.goods.domain.LayoutType;
import com.lhiot.mall.wholesale.goods.domain.PlateCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.PlateGirdParam;
import com.lhiot.mall.wholesale.goods.mapper.PlateCategoryMapper;

/**PlateCategoryService
 * 商品中心
 * @author lynn
 *
 */
@Service
@Transactional
public class PlateCategoryService {
	
	private final PlateCategoryMapper plateCategoryMapper;
	private static final String ALLOW = "ok";//允许删除
	
	@Autowired
	public PlateCategoryService(PlateCategoryMapper plateCategoryMapper){
		this.plateCategoryMapper = plateCategoryMapper;
	}
	
	/**
	 * 新增版块
	 * @param PlateCategory
	 * @return
	 */
	public boolean create(PlateCategory plateCategory){
		//确保只有一个roll和list的类型，如果已经存在则都改成tile类型
		this.ensureUnique(plateCategory);
		return plateCategoryMapper.insert(plateCategory)>0;
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
		plateCategoryMapper.removeInbatch(list);
	}
	
	/**
	 * 修改商品版块
	 * @param PlateCategory
	 * @return
	 */
	public boolean update(PlateCategory plateCategory){
		//确保只有一个roll和list的类型，如果已经存在则都改成tile类型
		this.ensureUnique(plateCategory);
		return plateCategoryMapper.update(plateCategory)>0;
	}
	
	/**
	 * 根据id查询商品版块
	 * @param id
	 * @return
	 */
	public PlateCategory plateCategory(Long id){
		return plateCategoryMapper.select(id);
	}
	
	/**
	 * 根据类型查询所有的商品版块
	 * @return
	 */
	public List<PlateCategory> search(String layoutType){
		return plateCategoryMapper.searchByType(layoutType);
	}
	
	/**
	 * 推荐商品id的集合
	 * @param layoutType
	 * @return
	 */
	public List<Long> recommendGoodsId(LayoutType layoutType){
		return plateCategoryMapper.plateGoodsId(layoutType.toString());
	}
	
	/**
	 * 根据类型查询所有的商品版块
	 * @return
	 */
	public List<PlateCategory> searchAll(){
		return plateCategoryMapper.searchAll();
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(PlateGirdParam param){
		int count = plateCategoryMapper.pageQueryCount();
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
		List<PlateCategory> plateCategorys = plateCategoryMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(plateCategorys);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 获取版块分类的树结构
	 * @return
	 */
	public List<CategoryTree> tree(){
		List<CategoryTree> result = new ArrayList<>();
		List<PlateCategory> list = plateCategoryMapper.findTree();
		CategoryTree categoryTree = null;
		for(PlateCategory p : list){
			categoryTree = new CategoryTree();
			categoryTree.setId(p.getId());
			categoryTree.setPId(p.getParentId());
			categoryTree.setName(p.getPlateName());
			categoryTree.setParentClassName(p.getParentPlateName());
			categoryTree.setIsParent(p.getParentId().toString().equals("0")?true:false);
			categoryTree.setLevel(p.getLevels());
			result.add(categoryTree);
		}
		return result;
	}
	
	public String canDelete(String ids){
		String result = ALLOW;
		List<Long> list = Arrays.asList(ids.split(",")).stream()
								.map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
		List<PlateCategory> plateCategories = plateCategoryMapper.search(list);
		for(Long id : list){
			for(PlateCategory plateCategory : plateCategories){
				Long pId = plateCategory.getId();
				if(Objects.equals(id, pId)){
					result = result.concat(","+plateCategory.getPlateName());
				}
			}
		}
		return result.substring(result.indexOf(',')+1, result.length());
	}
	
	/**
	 * 确保滚动布局和推荐列表只有一个版块
	 * @param category
	 * @return
	 */
	public void ensureUnique(PlateCategory category){
		if(Objects.isNull(category)){
			return ;
		}
		String type = category.getLayout();
		boolean success = plateCategoryMapper.layout(type) > 0;
		//滚动布局和推荐列表的类型只能设置一个
		if(StringUtils.isNotBlank(type) && 
				!LayoutType.tile.toString().equals(type)){
			if(success){
				category.setLayout(LayoutType.tile.toString());
			}
		}
	}
}
