package com.whx.tmall.service;

import com.whx.tmall.dao.ProductImageDAO;
import com.whx.tmall.pojo.OrderItem;
import com.whx.tmall.pojo.Product;
import com.whx.tmall.pojo.ProductImage;
import com.whx.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "productImages")
public class ProductImageService {
    //创建ProductImageService，提供CRD。
    //同时还提供了两个常量，分别表示单个图片和详情图片：
    public static final String type_single = "single";
    public static final String type_detail = "detail";

    @Autowired
    ProductImageDAO productImageDAO;
    @Autowired ProductService productService;
    @Autowired CategoryService categoryService;


    //添加图片
    @CacheEvict(allEntries = true)
    public void add(ProductImage productImage) {
        productImageDAO.save(productImage);
    }

    //查找单个照片列表
    @Cacheable(key = "'productImages-single-pid-'+#p0.id")
    public List<ProductImage> listSingleProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_single);
    }

    //查找详情图片列表
    @Cacheable(key = "'productImages-detail-pid-'+#p0")
    public List<ProductImage> listDetailProductImage(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_detail);
    }

    //获取图片
    @Cacheable(key = "'productImages-one-'+#p0")
    public ProductImage get(int id) {
        return productImageDAO.findOne(id);
    }

    //删除图片
    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productImageDAO.delete(id);
    }

    //设置预览图
    public void setFirstProductImage(Product product) {
        ProductImageService productImageService = SpringContextUtil.getBean(ProductImageService.class);
        //取出ProductImage 列表对象 一个Product id对应多个ProductImages对象 ，在乎你传入什么Product
        List<ProductImage> list = productImageService.listSingleProductImages(product);//获取该产品所有的图片
        //将取出的ProductImage 对象逐一赋值到对应的Product上
        if (!list.isEmpty()) 
            product.setFirstProductImage(list.get(0));//如果不是空的就把第一张图设置为预览图
        else 
            product.setFirstProductImage(new ProductImage());
        
    }

    //为多个Product对象设置预览图
    // （Ps:listProduct页显示多个Product，所以是为多个Product对象在内部使用setter的方式对Pro~img对象赋值）
    public void setFirstProductImage(List<Product> products) {
        for (Product product:products){
            setFirstProductImage(product);//获取多个product，然后为集合中的所有product设置预览图
        }
    }
    
    public void setFirstProductImageOnOrderItem(List<OrderItem> ois){
        for (OrderItem orderItem:ois){
            setFirstProductImage(orderItem.getProduct());
        }
    }
}
