package com.whx.tmall.dao;

import com.whx.tmall.pojo.Product;
import com.whx.tmall.pojo.Property;
import com.whx.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {
    
    List<PropertyValue> findByProductOrderByIdDesc(Product product);

    PropertyValue getByPropertyAndProduct(Property property, Product product);
}
