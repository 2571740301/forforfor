package com.whx.tmall.dao;

import com.whx.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
//设计JPA:R   接口支持多继承
//继承Jpa 集成CRUD的底层JpaRepository，第一个参数要操作的对象，第二个参数为主键类型
public interface CategoryDAO extends JpaRepository<Category,Integer> {

}
