package com.whx.tmall.web;

import com.whx.tmall.pojo.Property;
import com.whx.tmall.service.PropertyService;
import com.whx.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

//JPA:M_Controller
//这个就是专门用来提供 RESTFUL 服务器控制器,用来操作Service
//就是 HTTP 协议里面，四个表示操作方式的动词：GET、POST、PUT、DELETE。
// 它们分别对应四种基本操作：GET 用来获取资源，POST 用来新建资源，PUT 用来更新资源，DELETE 用来删除资源。
@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    //映射属性分类，返回page对象用于分页
    @GetMapping("categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable("cid") int cid,
                                         @RequestParam(value = "start", defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<Property> page = propertyService.list(cid, start, size, 5);
        return page;
    }

    @GetMapping("properties/{id}")
    public Property get(@PathVariable("id") int id) throws Exception {
        Property bean = propertyService.get(id);
        return bean;
    }

    //当提交的数据是json或对象的形式时，使用RequestBody来接受
    @PostMapping("properties")
    public Object add(@RequestBody Property bean) throws Exception {
        System.out.println("bean" + bean);
        propertyService.add(bean);
        return bean;
    }

    //删除数据
    @DeleteMapping("properties/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) throws Exception {
        propertyService.delete(id);
        return null;
    }

    //更新数据
    @PutMapping("properties")
    public Object update(@RequestBody Property bean) throws Exception {
        propertyService.update(bean);
        return bean;
    }
}
