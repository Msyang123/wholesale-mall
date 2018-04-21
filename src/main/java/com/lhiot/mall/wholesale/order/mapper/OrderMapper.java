package com.lhiot.mall.wholesale.order.mapper;


import com.lhiot.mall.wholesale.order.vo.Order;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;
import java.util.Map;

/**
 * Created by HuFan on 2018/4/21.
 */
@Mapper
public interface OrderMapper {

    int insert(Order order);

    int update(Order order);

    void remove(long id);

    Order select(long id);

    List<Order> search(Map<String, Object> where);

}
