package com.whx.tmall.service;

import com.whx.tmall.dao.PropertyDAO;
import com.whx.tmall.pojo.Category;
import com.whx.tmall.pojo.Property;
import com.whx.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "properties")
public class PropertyService {
    @Autowired
    PropertyDAO propertyDAO;
    @Autowired
    CategoryService categoryService;

    //添加属性
    @CacheEvict(allEntries = true)
    public void add(Property property) {
        propertyDAO.save(property);
    }

    //删除属性
    @CacheEvict(allEntries = true)
    public void delete(int id) {
        propertyDAO.delete(id);
    }

    //获取属性
    @Cacheable(key = "'properties-one-'+#p0")
    public Property get(int id) {
        return propertyDAO.findOne(id);
    }

    //更新
    @CacheEvict(allEntries = true)
    public void update(Property property) {
        propertyDAO.save(property);
    }

    //PropertyService, 增加通过分类获取所有属性集合的方法
    @Cacheable(key = "'properties-cid-'+#p0.id")
    public List<Property> listByCategory(Category category){
        return propertyDAO.findByCategory(category);
    }


    //返回自定义分页对象用于在控制器上返回数据
    @Cacheable(key = "'properties-cid-'+#p0+'-page-'+#p1+'-'+#p2")
    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Property> pageFromJPA = propertyDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

}
