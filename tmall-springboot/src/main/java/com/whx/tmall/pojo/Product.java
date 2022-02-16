package com.whx.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
// 告诉es如何匹配此类
@Document(indexName = "tmall_springboot", type = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @GeneratedValue注解存在的意义主要就是为一个实体生成一个唯一标识的主键、@GeneratedValue提供了主键的生成策略,-IDENTITY 主键由数据库生成, 采用数据库自增长, Oracle不支持这种方式
    @Column(name = "id") // 对应数据库中的id
    private int id;

    @ManyToOne
    @JoinColumn(name = "cid")
    private Category category; // 种类

    // 如果既没有指明 关联到哪个Column,又没有明确要用@Transient忽略，那么就会自动关联到表对应的同名字段
    private String name; // 产品名称
    private String subTitle; // 小标题
    private float originalPrice; // 原始价格
    private float promotePrice; // 优惠价格
    private int stock; // 库存
    private Date createDate; // 创建日期

    // 产品图片对象，因为可能一个产品包含多个图片对象，所以这里接收的是序列化的图片对象
    // 该字段非数据库所拥有
    @Transient
    private ProductImage firstProductImage;

    // 前端页面显示属性
    @Transient
    private List<ProductImage> productSingleImages;//单个图片集合
    @Transient
    private List<ProductImage> productDetailImages;//详细图片集合
    @Transient
    private int reviewCount;//销量
    @Transient
    private int saleCount;//累计评价


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public float getPromotePrice() {
        return promotePrice;
    }

    public void setPromotePrice(float promotePrice) {
        this.promotePrice = promotePrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ProductImage getFirstProductImage() {
        return firstProductImage;
    }

    public void setFirstProductImage(ProductImage firstProductImage) {
        this.firstProductImage = firstProductImage;
    }

    public List<ProductImage> getProductSingleImages() {
        return productSingleImages;
    }

    public void setProductSingleImages(List<ProductImage> productSingleImages) {
        this.productSingleImages = productSingleImages;
    }

    public List<ProductImage> getProductDetailImages() {
        return productDetailImages;
    }

    public void setProductDetailImages(List<ProductImage> productDetailImages) {
        this.productDetailImages = productDetailImages;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public Category getCategory() {
        return category;
    }
}
