package com.lhiot.mall.wholesale.demand.service;

import com.leon.microx.util.SnowflakeId;

import com.lhiot.mall.wholesale.base.DataMergeUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.demand.domain.gridparam.DemandGoodsGridParam;
import com.lhiot.mall.wholesale.demand.mapper.DemandGoodsMapper;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@Transactional
public class DemandGoodsService {


    private final SnowflakeId snowflakeId;

    private final DemandGoodsMapper demandGoodsMapper;

    private final UserService userService;


    @Autowired
    public DemandGoodsService(DemandGoodsMapper demandGoodsMapper, SnowflakeId snowflakeId, UserService userService) {
        this.demandGoodsMapper = demandGoodsMapper;
        this.snowflakeId = snowflakeId;
        this.userService = userService;
    }

    /**
     * 后台管理系统--分页查询新品需求信息
     * @param param
     * @return
     */
    public PageQueryObject pageQuery(DemandGoodsGridParam param) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        String namePhone = param.getNamePhone();
        User userParam = new User();
        userParam.setNamePhone(namePhone);
        List<DemandGoods> demandGoodsList = new ArrayList<DemandGoods>();
        List<User> userList = new ArrayList<User>();
        List<DemandGoodsResult> demandGoodsResults = new ArrayList<>();
        int count = 0;
        int page = param.getPage();
        int rows = param.getRows();
        //总记录数
        int totalPages = 0;
        if(namePhone == null ){//未传手机号/用户名 查询条件,先根据条件查询分页的新品需求列表及用户ids，再根据ids查询用户信息列表
            count = demandGoodsMapper.pageQueryCount(param);
            //起始行
            param.setStart((page-1)*rows);
            //总记录数
            totalPages = (count%rows==0?count/rows:count/rows+1);
            if(totalPages < page){
                page = 1;
                param.setPage(page);
                param.setStart(0);
            }
            demandGoodsList = demandGoodsMapper.pageQuery(param);
            List<Long> userIds = new ArrayList<Long>();
            if(demandGoodsList != null && demandGoodsList.size() > 0){//查询新品需求对应的用户ID列表与新品需求ID列表
                for(DemandGoods demandGoods : demandGoodsList){
                    long userId = demandGoods.getUserId();
                    if(!userIds.contains(userId)){//用户id去重
                        userIds.add(userId);
                    }
                }
            }
            userList = userService.search(userIds);//根据用户ID列表查询用户信息
        }else{//传了手机号查询条件，先根据条件查询用户列表及用户ids，再根据ids和新品需求其他信息查询新品需求信息列表
            userList = userService.searchByPhoneOrName(userParam);
            List<Long> userIds = new ArrayList<Long>();
            if(userList != null && userList.size() > 0){
                for(User user : userList){
                    userIds.add(user.getId());
                }
                param.setUserIds(userIds);
                count = demandGoodsMapper.pageQueryCount(param);
                //起始行
                param.setStart((page-1)*rows);
                //总记录数
                totalPages = (count%rows==0?count/rows:count/rows+1);
                if(totalPages < page){
                    page = 1;
                    param.setPage(page);
                    param.setStart(0);
                }
                demandGoodsList = demandGoodsMapper.pageQuery(param);//根据用户ID列表及其他查询条件查询用户信息
            }
        }
        PageQueryObject result = new PageQueryObject();
        if(demandGoodsList != null && demandGoodsList.size() > 0){//如果新品需求信息不为空,将新品需求列表与用户信息列表进行行数据组装
            demandGoodsResults = DataMergeUtils.dataMerge(demandGoodsList,userList,"userId","id",DemandGoodsResult.class);
        }
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        result.setRows(demandGoodsResults);//将查询记录放入返回参数中
        return result;
    }

    /**
     * 查询新品需求详情
     * @return
     */
    public DemandGoodsResult detail(Long id) {
        //新品需求详情信息
        DemandGoodsResult demandGoodsResult = demandGoodsMapper.select(id);
        if (Objects.nonNull(demandGoodsResult)){
            //用户信息
            User user1 = userService.user(demandGoodsResult.getUserId());
            if (Objects.nonNull(user1)) {
                demandGoodsResult.setShopName(user1.getShopName());
                demandGoodsResult.setUserName(user1.getUserName());
                demandGoodsResult.setPhone(user1.getPhone());
            }
        }
        return demandGoodsResult;
    }

    public int insertDemandGoods(DemandGoods demandGoods){
        return demandGoodsMapper.insertDemandGoods(demandGoods);
    }

    /**
     * 导出需求详情
     * @return
     */
    public List<Map<String, Object>> exportData(DemandGoodsGridParam param){
        return demandGoodsMapper.exportData(param);
    }
}
