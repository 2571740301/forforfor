package com.whx.tmall.dao;

import com.whx.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Integer> {
    //用于注册时检验用户是否存在
    User findByName(String name);
//登录时通过账号密码获取用户
    User getByNameAndPassword(String name,String password);
}


