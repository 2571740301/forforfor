package com.whx.tmall.web;

import com.whx.tmall.pojo.Product;
import com.whx.tmall.pojo.PropertyValue;
import com.whx.tmall.service.ProductService;
import com.whx.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;

    @GetMapping("products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid") int pid) throws Exception {
        Product product = productService.get(pid);
        //初始化数据
        propertyValueService.init(product);
        //获得数据列表
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        return propertyValues;
    }
    
    //更新数据
    @PutMapping("propertyValues")
    public Object update(@RequestBody PropertyValue bean) throws Exception{
        propertyValueService.update(bean);
        return bean;
    }
        
}
