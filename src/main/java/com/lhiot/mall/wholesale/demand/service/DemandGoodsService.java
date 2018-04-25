package com.lhiot.mall.wholesale.demand.service;

import com.leon.microx.util.BeanUtils;
import com.leon.microx.util.SnowflakeId;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.demand.domain.gridparam.DemandGoodsGridParam;
import com.lhiot.mall.wholesale.demand.mapper.DemandGoodsMapper;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by HuFan on 2018/4/23.
 */

@Service
@Transactional
public class DemandGoodsService {

    private final SnowflakeId snowflakeId;

    private final DemandGoodsMapper demandGoodsMapper;

    private final UserMapper userMapper;


    @Autowired
    public DemandGoodsService(DemandGoodsMapper demandGoodsMapper, SnowflakeId snowflakeId,UserMapper userMapper) {
        this.demandGoodsMapper = demandGoodsMapper;
        this.snowflakeId = snowflakeId;
        this.userMapper = userMapper;
    }

    public boolean save(DemandGoods demandGoods) {
        if (demandGoods.getId() > 0) {
            return demandGoodsMapper.update(demandGoods) > 0;
        } else {
            demandGoods.setId(snowflakeId.longId());
            return demandGoodsMapper.insert(demandGoods) > 0;
        }
    }

    public void delete(long id) {
        demandGoodsMapper.remove(id);
    }

    public DemandGoods demandGoods(long id) {
        return demandGoodsMapper.select(id);
    }

    public List<DemandGoods> demandGoods(DemandGoodsGridParam param) {
        return demandGoodsMapper.search(BeanUtils.toMap(param));
    }

    /**
     * 分页查询
     * @return
     */
    public PageQueryObject pageQuery(DemandGoodsGridParam param){
        int count = demandGoodsMapper.pageQueryCount(param);
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
        //根据参数（手机号或姓名）获取用户信息
        List<User> userList = userMapper.pageQuery(param);
        PageQueryObject result = new PageQueryObject();
        //如果用户信息不为空，根据用户ids查询新品需求
        if(userList != null){
            List<Long> userIds = new ArrayList<Long>();
            for (User user:userList){
                userIds.add(user.getId());
            }
            List<DemandGoodsResult> demandGoodsResultList = demandGoodsMapper.pageQuery(param);
            //如果新品需求信息不为空，根据id匹配信息
            if (demandGoodsResultList != null) {
                for (DemandGoodsResult demandGoodsResult : demandGoodsResultList) {
                    Long demandGoodsUserId = demandGoodsResult.getUserId();
                    for (User user : userList) {
                        Long uId = user.getId();
                        if (Objects.equals(demandGoodsUserId, uId)) {
                            demandGoodsResult.setPhone(user.getPhone());
                            demandGoodsResult.setShopName(user.getShopName());
                            demandGoodsResult.setUserName(user.getUserName());
                            result.setRows(demandGoodsResultList);
                        }
                    }
                }
            }
            result.setPage(page);
            result.setRecords(rows);
            result.setTotal(totalPages);
        }
        return result;
    }

    /**
     * 查询详情
     * @return
     */
    public DemandGoodsResult detail(Long id) {
        DemandGoodsResult demandGoodsResult = new DemandGoodsResult();
        //新品需求详情信息
        DemandGoods demandGoods = demandGoodsMapper.select(id);
        if (demandGoods != null){
            //用户信息
            User user1 = userMapper.user(demandGoods.getUserId());

            demandGoodsResult.setId(demandGoods.getId());
            demandGoodsResult.setGoodsName(demandGoods.getGoodsName());
            demandGoodsResult.setGoodsBrand(demandGoods.getGoodsBrand());
            demandGoodsResult.setGoodsStandard(demandGoods.getGoodsStandard());
            demandGoodsResult.setReferencePrice(demandGoods.getReferencePrice());
            demandGoodsResult.setSupplier(demandGoods.getSupplier());
            demandGoodsResult.setComments(demandGoods.getComments());
            demandGoodsResult.setContactPhone(demandGoods.getContactPhone());
            demandGoodsResult.setUserId(demandGoods.getUserId());
            demandGoodsResult.setCreateTime(demandGoods.getCreateTime());
            demandGoodsResult.setShopName(user1.getShopName());
            demandGoodsResult.setUserName(user1.getUserName());
            demandGoodsResult.setPhone(user1.getPhone());
            return demandGoodsResult;
        }else{
            return null;
        }
    }
}

