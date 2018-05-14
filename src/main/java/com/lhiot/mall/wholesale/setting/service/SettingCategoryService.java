package com.lhiot.mall.wholesale.setting.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.setting.domain.ParamCategory;
import com.lhiot.mall.wholesale.setting.domain.ParamSettingTree;
import com.lhiot.mall.wholesale.setting.domain.gridparam.ParamCategoryGirdParam;
import com.lhiot.mall.wholesale.setting.mapper.SettinCategoryMapper;

@Service
@Transactional
public class SettingCategoryService {

    private final SettinCategoryMapper settinCategoryMapper;

    @Autowired
    public SettingCategoryService(SettinCategoryMapper settinCategoryMapper) {
        this.settinCategoryMapper = settinCategoryMapper;
    }
    
	/**
	 * 新增
	 * @param param
	 * @return
	 */
	public boolean create(ParamCategory param){
		//确保只有一个roll和list的类型，如果已经存在则都改成tile类型
		return settinCategoryMapper.insert(param)>0;
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
		settinCategoryMapper.removeInbatch(list);
	}
	
	/**
	 * 修改
	 * @param param
	 * @return
	 */
	public boolean update(ParamCategory param){
		//确保只有一个roll和list的类型，如果已经存在则都改成tile类型
		return settinCategoryMapper.update(param)>0;
	}
	
	/**
	 * 根据id查询商品版块
	 * @param id
	 * @return
	 */
	public ParamCategory paramCategory(Long id){
		return settinCategoryMapper.select(id);
	}
	/**
	 * 分页查询
	 * @return
	 */
	public PageQueryObject pageQuery(ParamCategoryGirdParam param){
		int count = settinCategoryMapper.pageQueryCount(param);
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
		List<ParamCategory> plateCategorys = settinCategoryMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(plateCategorys);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 获取分类的树结构
	 * @return
	 */
	public List<ParamSettingTree> tree(){
		List<ParamSettingTree> result = new ArrayList<>();
		List<ParamCategory> list = settinCategoryMapper.findTree();
		ParamSettingTree tree = null;
		for(ParamCategory p : list){
			tree = new ParamSettingTree();
			tree.setId(p.getId());
			tree.setPId(p.getParentId());
			tree.setName(p.getParamCategoryName());
			tree.setShowType(p.getShowType());
			tree.setIsParent(p.getParentId().toString().equals("0")?true:false);
			result.add(tree);
		}
		return result;
	}
}
