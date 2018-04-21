package com.lhiot.mall.wholesale.order.service;

import com.leon.microx.util.BeanUtils;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.order.vo.Order;
import com.lhiot.mall.wholesale.order.vo.SearchOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by HuFan on 2018/4/21.
 */
@Service
@Transactional
public class OrderService {

    private final SnowflakeId snowflakeId;

    private final OrderMapper orderMapper;

    @Autowired
    public OrderService(OrderMapper orderMapper, SnowflakeId snowflakeId) {
        this.orderMapper = orderMapper;
        this.snowflakeId = snowflakeId;
    }

    public boolean save(Order order) {
        if (order.getId() > 0) {
            return orderMapper.update(order) > 0;
        } else {
            order.setId(snowflakeId.longId());
            return orderMapper.insert(order) > 0;
        }
    }

    public void delete(long id) {
        orderMapper.remove(id);
    }

    public Order order(long id) {
        return orderMapper.select(id);
    }

    public List<Order> order(SearchOrder param) {
        return orderMapper.search(BeanUtils.toMap(param));
    }


}
