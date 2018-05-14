package com.lhiot.mall.wholesale.user.service;

import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.ShopResult;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.mapper.SalesUserMapper;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.sgsl.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.*;
@Slf4j
@Service
@Transactional
public class SalesUserService {

    private final SalesUserMapper salesUserMapper;

    private final RabbitTemplate rabbit;

    private final UserMapper userMapper;

    private final PaymentProperties properties;

    private final RestTemplate restTemplate;


    @Autowired
    public SalesUserService(SalesUserMapper salesUserMapper, RabbitTemplate rabbit, UserMapper userMapper, PaymentProperties properties, RestTemplate restTemplate) {
        this.salesUserMapper = salesUserMapper;
        this.rabbit = rabbit;
        this.userMapper = userMapper;
        this.properties = properties;
        this.restTemplate = restTemplate;
    }


    public List<SalesUserRelation> selectRelation(Map<String,Object> param) {
        return salesUserMapper.selectRelation(param);
    }

    public int updateUserSaleRelationship(SalesUserRelation salesUserRelation){
        return salesUserMapper.updateUserSaleRelationship(salesUserRelation);
    }

    public SalesUser searchSalesUser(long id){
         return salesUserMapper.findById(id);
    }

    public SalesUser searchSalesUserByOpenid(String openid){
        return salesUserMapper.searchSalesUserByOpenid(openid);
    }

    public int insertRelation(SalesUserRelation salesUserRelation){
        return salesUserMapper.insertRelation(salesUserRelation);
    }

    public SalesUserRelation isSeller(long userId){
        return salesUserMapper.isSeller(userId);
    }

    public SalesUserRelation searchSaleRelationship(SalesUserRelation salesUserRelation){
        return salesUserMapper.searchSaleRelationship(salesUserRelation);
    }

    public List<ShopResult> searchShopInfo(long salesId){
        return salesUserMapper.searchShopInfo(salesId);
    }

    /**
     * 门店审核
     * @param salesUserRelation
     * @return
     */
    public boolean userCheck(SalesUserRelation salesUserRelation){
        User user = userMapper.user(salesUserRelation.getUserId());
        if (salesUserMapper.updateUserSaleRelationship(salesUserRelation)>0){
            if (Objects.equals(salesUserRelation.getAuditStatus(),"agree")){
                if (userMapper.updateUserStatus(salesUserRelation.getUserId())>0){//用户表改已认证或未认证
                    //审核通过的时候发送发券广播消息
                    rabbit.convertAndSend("store-check-event","", salesUserRelation.getUserId());

                    //发送短信
                    Map<String, Object> body = ImmutableMap.of("phone",user.getPhone());
                    HttpEntity<Map<String, Object>> request = properties.getSendSms().createRequest(body);
                    String messageUrl= MessageFormat.format(properties.getSendSms().getUrl(),"regist-pass", user.getPhone());
                    String result = restTemplate.postForObject(messageUrl, request, String.class);
                    log.info("result: " + result);
                }
            }else {
                //发送短信
                Map<String, Object> body = ImmutableMap.of("phone",user.getPhone());
                HttpEntity<Map<String, Object>> request = properties.getSendSms().createRequest(body);
                String messageUrl= MessageFormat.format(properties.getSendSms().getUrl(),"regist-unpass",user.getPhone());
                String result=restTemplate.postForObject(messageUrl, request, String.class);
                log.info("result:"+result);
            }
        }
        return true;
    }


    /***************后台管理系统*******************/
    public int create(SalesUser salesUser){
        return this.salesUserMapper.create(salesUser);
    }

    public int updateById(SalesUser salesUser){
        return this.salesUserMapper.updateById(salesUser);
    }

    public int deleteByIds(String ids){
        List<String> idList = Arrays.asList(ids.split(","));
        return this.salesUserMapper.deleteByIds(idList);
    }

    public List<SalesUser> list(SalesUser salesUser){
        return this.salesUserMapper.list(salesUser);
    }

    public PageQueryObject page(Map<String,Object> param){
        PageQueryObject result = new PageQueryObject();
        int count = salesUserMapper.pageCount(param);
        int page = (Integer)param.get("page");
        int rows = (Integer)param.get("rows");
        //起始行
        param.put("start",(page-1)*rows);
        //param.setStart((page-1)*rows);
        //总记录数
        int totalPages = (count%rows==0?count/rows:count/rows+1);
        if(totalPages < page){
            page = 1;
            param.put("page",page);
            param.put("start",0);
        }
        List<SalesUser> salesUserPerformances = salesUserMapper.page(param);
        result.setRows(salesUserPerformances);
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        return result;
    }


    public SalesUser findById(Long id){
        return this.salesUserMapper.findById(id);
    }

    public SalesUser findCode(String code){
        return this.salesUserMapper.findCode(code);
    }

    public SalesUser login(String acount){
        return this.salesUserMapper.login(acount);
    }

    public List<SalesUser> salesUsers(){
    	return this.salesUserMapper.salesUsers();
    }

    public int assginShop(String assginUserId, String shopId, String oldUserId) {
        int result = 0;
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("userId",shopId);
        param.put("salesmanId",assginUserId);
        param.put("oldSalesmanId",oldUserId);
        if(StringUtils.isNotBlank(assginUserId)){
            if(StringUtils.isNotBlank(shopId)){
                //salesUserMapper.updateUserSaleRelationship()
                //updateSalesmanIdByUserId\
                //updateSalesmanIdBySalesmanId
                result = salesUserMapper.updateSalesmanIdByUserId(param);
            }
            if(StringUtils.isNotBlank(oldUserId)){
                result = salesUserMapper.updateSalesmanIdBySalesmanId(param);
            }
        }
        return result;
    }
}
