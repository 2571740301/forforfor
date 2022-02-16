package com.whx.tmall.service;

import com.whx.tmall.dao.OrderDAO;
import com.whx.tmall.pojo.Order;
import com.whx.tmall.pojo.OrderItem;
import com.whx.tmall.pojo.User;
import com.whx.tmall.util.Page4Navigator;
import com.whx.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = "orders")
public class OrderService {

    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    OrderDAO orderDAO;

    @Autowired
    OrderItemService orderItemService;

    @Cacheable(key = "'orders-page-'+#p0+'-'+#p1")
    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    //removeOrderFromOrderItem
    public void removeFromOrderItem(List<Order> orders) {
        //从列表中取出每个order，获得每个order下的orderItem，再将OrderItem下的Order删除
        for (Order order : orders) {
            removeFromOrderItem(order);
        }
    }

    public void removeFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        //将list中的OrderItem中的每个Order设置为空
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }

    //获得单个数据
    @Cacheable(key = "'orders-one-'+#p0")
    public Order get(int id) {
        return orderDAO.findOne(id);
    }

    //如果发货就更新数据
    @CacheEvict(allEntries = true)
    public void update(Order bean) {
        orderDAO.save(bean);
    }

    //添加为每个订单项添加订单
    @CacheEvict(allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    //如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。这是默认值。
    public float add(Order order, List<OrderItem> ois) {
        float total = 0;
        //添加订单到数据库
        add(order);
        if (false)
            throw new RuntimeException();
        for (OrderItem oi : ois) {
            oi.setOrder(order);                                                         
            orderItemService.update(oi);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
        }
        return total;
    }
    
    //添加订单
    @CacheEvict(allEntries = true)
    public void add(Order order) {
        orderDAO.save(order);
    }

    //根据用户和订单的对应状态获取商品（状态非delete）
    public List<Order> listByUserWithoutDelete(User user) {
        OrderService orderService = SpringContextUtil.getBean(OrderService.class);
        List<Order> orders = orderService.listByUserAndNotDelete(user);
        //将订单项的数据填充到对应的订单中，然后返回全部信息= 订单项+订单信息
        //order.setTotal(total);
        //order.setOrderItems(orderItems);
        //order.setTotalNumber(totalNumber);
        orderItemService.fill(orders);
        return orders;
    }

    @Cacheable(key = "'order-uid-'+#p0.id")
    public List<Order> listByUserAndNotDelete(User user) {
        //获取状态非delete的订单list
        return orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
    }

    public void calc(Order o) {
        List<OrderItem> orderItems = o.getOrderItems();
        float total = 0;
        for (OrderItem orderItem : orderItems) {
            total = orderItem.getProduct().getPromotePrice() * orderItem.getNumber();
        }
        o.setTotal(total);
    }
}
