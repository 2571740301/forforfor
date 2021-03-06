package com.whx.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "orderItem")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    //一个用户的订单列中有多个产品
    @ManyToOne
    @JoinColumn(name = "pid")
    private Product product;

    //一个订单中有多个订单项
    @ManyToOne
    @JoinColumn(name = "oid")
    private Order order;

    //一个用户有多个订单项
    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    //商品订单购买数量
    private int number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
