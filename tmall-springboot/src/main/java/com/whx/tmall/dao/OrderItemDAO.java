package com.whx.tmall.dao;

import com.whx.tmall.pojo.Order;
import com.whx.tmall.pojo.OrderItem;
import com.whx.tmall.pojo.Product;
import com.whx.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem, Integer> {
    //一个订单可能包含多个商品
    public List<OrderItem> findByOrderOrderByIdDesc(Order order);

    //通过产品找对应的订单数 一个product主键id对应多个Pid
    public List<OrderItem> findByProduct(Product product);

    //基于用户对象user，查询没有生成订单的订单项集合
    public List<OrderItem> findByUserAndOrderIsNull(User user);
}
