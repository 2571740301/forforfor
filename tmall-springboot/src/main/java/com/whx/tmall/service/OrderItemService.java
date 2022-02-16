package com.whx.tmall.service;

import com.whx.tmall.dao.OrderItemDAO;
import com.whx.tmall.pojo.Order;
import com.whx.tmall.pojo.OrderItem;
import com.whx.tmall.pojo.Product;
import com.whx.tmall.pojo.User;
import com.whx.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "orderItems")////添加Redis缓存，并由对应键来控制该类
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;
    //为订单填充订单项集合
    //springboot 的缓存机制是通过切面编程 aop来实现的。
    //从fill方法里直接调用 listByCategory 方法， aop 是拦截不到的，也就不会走缓存了。
    //所以要通过这种 绕一绕 的方式故意诱发 aop, 这样才会想我们期望的那样走redis缓存。
    public void fill(List<Order> orders) {
        for (Order order : orders) {
            fill(order);
        }
    }

    public void fill(Order order) {
        OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
        List<OrderItem> orderItems = orderItemService.listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi : orderItems) {
            total = oi.getProduct().getPromotePrice() * oi.getNumber();
            totalNumber = oi.getNumber();
            productImageService.setFirstProductImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);
    }

    @Cacheable(key = "'orderItems-oid-'+#p0.id")
    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }

    @Cacheable(key = "'orderItems-pid-'+#p0.id")
    public List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }

    //根据订单的pid 获得产品的销量
    public int getSaleCount(Product product) {
        OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);
        List<OrderItem> list = orderItemService.listByProduct(product);
        int result = 0;
        for (OrderItem oi : list) {
            //订单非空判断
            if (null != oi.getOrder()) {
                if (null != oi.getOrder() && null != oi.getOrder().getPayDate()) ;
                result += oi.getNumber();
            }
        }
        return result;
    }

    //基于用户查询订单项中没有生成订单的订单项
    @Cacheable(key = "'orderItems-uid-'+#p0.id")
    public List<OrderItem> listByUser(User user) {
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }

    //更新数据
    @CacheEvict(allEntries = true)
    public void update(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    //添加数据
    @CacheEvict(allEntries = true)
    public void add(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    //获取数据
    @Cacheable(key = "'orderItems-one-'+#p0")
    public OrderItem get(int id) {
        return orderItemDAO.findOne(id);
    }

    //删除数据
    @CacheEvict(allEntries = true)
    public void delete(int id) {
        orderItemDAO.delete(id);
    }
}
