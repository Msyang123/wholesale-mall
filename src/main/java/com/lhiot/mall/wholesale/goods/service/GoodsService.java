package com.lhiot.mall.wholesale.goods.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lhiot.mall.wholesale.goods.domain.GoodsUnit;

/**GoodsService
 * 游戏数据服务类
 * @author yj
 *
 */
@Service
public class GoodsService {
	
	private final SqlSession sqlSession;
	
	@Autowired
	public GoodsService(SqlSession sqlSession){
		this.sqlSession = sqlSession;
	}
	
	public List<GoodsUnit> tet(){
		return sqlSession.selectList("t_whs_goods_unit.findAll");
	}
}
