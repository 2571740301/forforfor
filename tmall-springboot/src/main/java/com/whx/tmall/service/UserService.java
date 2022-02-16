package com.whx.tmall.service;

import com.whx.tmall.dao.UserDAO;
import com.whx.tmall.pojo.User;
import com.whx.tmall.util.Page4Navigator;
import com.whx.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "users")
public class UserService {
    @Autowired
    UserDAO userDAO;

    //返回分页对象及其数据
    @Cacheable(key = "'user-page-'+#p0+'-'+#p1")
    public Page4Navigator<User> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = userDAO.findAll(pageable);
        return new Page4Navigator<User>(pageFromJPA, navigatePages);
    }

    //用于校验用户是否存在
    public boolean isExist(String name) {
        UserService userService = SpringContextUtil.getBean(UserService.class);
        User user = userService.getByName(name);
        return null != user;
    }

    @Cacheable(key = "'users-one-name-'+#p0")
    public User getByName(String name) {
        return userDAO.findByName(name);
    }

    //添加用户
    @CacheEvict(allEntries = true)
    public void add(User bean) {
        userDAO.save(bean);
    }

    @Cacheable(key = "'users-one-name-'+#p0+'-password-'+#p1")
    public User get(String name, String password) {
        return userDAO.getByNameAndPassword(name, password);
    }
}
