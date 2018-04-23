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
import com.lhiot.mall.wholesale.goods.domain.CategoryTree;
import com.lhiot.mall.wholesale.goods.domain.GoodsCategory;
import com.lhiot.mall.wholesale.goods.domain.PlateCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsStandardGirdParam;
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
	 * 查询所有的商品版块
	 * @return
	 */
	public List<PlateCategory> search(){
		return plateCategoryMapper.search();
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageQueryObject pageQuery(GoodsStandardGirdParam param){
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
			categoryTree.setParentClassName(p.getParentPlateNameName());
			categoryTree.setIsParent(p.getParentId().toString().equals("0")?true:false);
			categoryTree.setLevel(p.getLevels());
			result.add(categoryTree);
		}
		return result;
	}
}
