package com.whx.tmall.service;

import com.whx.tmall.dao.PropertyValueDAO;
import com.whx.tmall.pojo.Product;
import com.whx.tmall.pojo.Property;
import com.whx.tmall.pojo.PropertyValue;
import com.whx.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {
    @Autowired
    PropertyService propertyService;
    @Autowired
    PropertyValueDAO propertyValueDAO;

    //这个方法的作用是初始化PropertyValue。 为什么要初始化呢？
    // 因为对于PropertyValue的管理，没有增加，只有修改。
    // 所以需要通过初始化来进行自动地增加，以便于后面的修改。
    public void init(Product product){
        PropertyValueService propertyValueService = SpringContextUtil.getBean(PropertyValueService.class);
        
        
        //通过分类获得分类属性
        List<Property> properties = propertyService.listByCategory(product.getCategory());
        //通过分类属性的id和产品id获得对应的属性值
        for (Property property:properties){
            PropertyValue propertyValue = propertyValueService.getByPropertyAndProduct(product,property);
            if (null==propertyValue) {
                propertyValue = new PropertyValue();
                propertyValue.setProperty(property);
                propertyValue.setProduct(product);
                propertyValueDAO.save(propertyValue);
            }
        }
    }
    
    //获取属性值
    @Cacheable(key = "'propertyValues-pid-'+#p0.id")
    public List<PropertyValue> list(Product product){
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }
    
    @Cacheable(key = "'propertyValues-one-pid'+#p0.id+'-ptid-'+#p1.id")
    public PropertyValue getByPropertyAndProduct(Product product,Property property){
        return propertyValueDAO.getByPropertyAndProduct(property,product);
    }
    
    //更新
    @CacheEvict(allEntries = true)
    public void update(PropertyValue bean){
        propertyValueDAO.save(bean);
    }
}
