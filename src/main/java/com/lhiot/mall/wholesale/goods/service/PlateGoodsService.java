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
import com.lhiot.mall.wholesale.goods.domain.PlateGoods;
import com.lhiot.mall.wholesale.goods.domain.PlateGoodsResult;
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
		Long plateId = param.getPlateId();
		String[] standardId = (param.getGoodsStandardIds()).split(",");
		//查询最大排序号
		Integer maxRank = this.findMaxRankNum(plateId);
		
		PlateGoods plateGoods = null;
		List<PlateGoods> list = new ArrayList<>();
		
		int size = standardId.length;
		for(int i=0;i<size;i++){
			plateGoods = new PlateGoods();
			plateGoods.setPlateId(plateId);
			plateGoods.setRank(maxRank+i+1);//新增的默认自增添加排序号
			plateGoods.setGoodsStandardId(Long.parseLong(standardId[i]));
			list.add(plateGoods);
		}
		this.duplicate(plateId, list);
		if(list.isEmpty()){
			return false;
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
	public PlateGoodsResult plateGoods(Long id){
		return plateGoodsMapper.select(id);
	}
	
	/**
	 * 查询所有的商品版块商品
	 * @return
	 */
	public List<PlateGoodsResult> search(){
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
		List<PlateGoodsResult> goodsUnits = plateGoodsMapper.pageQuery(param);
		PageQueryObject result = new PageQueryObject();
		result.setRows(goodsUnits);
		result.setPage(page);
		result.setRecords(rows);
		result.setTotal(totalPages);
		return result;
	}
	
	/**
	 * 批量修改商品版块的分类
	 * @param param
	 * @return
	 */
	public boolean modifyPlate(PlateGoods param) {
		boolean success = false;
		Long plateId = param.getPlateId();
		//将主键id和standardId组装成一一对应的list,作为update操作的参数
		String[] idAndStandardIds = param.getIdAndStandardIds().split(",");
		List<PlateGoods> list = new ArrayList<>(idAndStandardIds.length);
		PlateGoods plateGoods = null;
		for(String idAndStandardId : idAndStandardIds){
			String[] str = idAndStandardId.split("s");//idAndStandardId是有主键id和standardId以s分割组成的字符串，如1s3
			plateGoods = new PlateGoods();
			plateGoods.setPlateId(plateId);
			plateGoods.setId(Long.parseLong(str[0]));
			plateGoods.setGoodsStandardId(Long.parseLong(str[1]));
			list.add(plateGoods);
		}
		//去除给分类下已有的商品
		this.duplicate(plateId, list);
		if(list.isEmpty()){
			return success;
		}
		//获取该分类下的最大排序号
		Integer maxRank = this.findMaxRankNum(plateId);
		//给每个商品加上排序号
		int rank = 1;
		for(PlateGoods p : list){
			p.setRank(maxRank+rank);
			rank++;
		}
		success = plateGoodsMapper.updateInBatch(list)>0;
		return success;
	}
	
	/**
	 * 查询当前分类下商品的最大排序号，以给新增商品默认排序号，自增
	 * @param plateId
	 * @return
	 */
	public Integer findMaxRankNum(Long plateId){
		Integer maxRank = 0;
		Integer rank = plateGoodsMapper.maxRank(plateId);
		if(null != rank){
			maxRank = rank;
		}
		return maxRank;
	}
	
	/**
	 * 去除重复加入的商品
	 * @param plateId 分类id
	 * @param paramList 
	 */
	public void duplicate (Long plateId,List<PlateGoods> paramList){
		if(Objects.isNull(plateId) || paramList.isEmpty()){
			return ;
		}
		//查询当前分类下的所有商品
		List<PlateGoods> list = plateGoodsMapper.findByPlateId(plateId);
		if(list.isEmpty()){
			return ;
		}
		for(PlateGoods plateGoods : list){
			Long standardId = plateGoods.getGoodsStandardId();
			for(int i=paramList.size()-1;i>=0;i--){
				Long id = (paramList.get(i)).getGoodsStandardId();
				if(Objects.equals(standardId, id)){
					paramList.remove(i);
					break;
				}
			}
		}
	}
}
