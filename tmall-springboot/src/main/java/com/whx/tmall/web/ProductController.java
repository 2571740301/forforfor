package com.whx.tmall.web;

import com.whx.tmall.pojo.Product;
import com.whx.tmall.service.CategoryService;
import com.whx.tmall.service.ProductImageService;
import com.whx.tmall.service.ProductService;
import com.whx.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    CategoryService categoryService;

    //读出Product数据
    /*URL示例2：http://www.iteye.com/problems/101566?Key=123
        这个URL里面，服务器想获取问题编号101566，因为这个参数直接包含在请求路径部分中，所以代码中用的应该是@PathVariable；对于参数部分Key=123，如果想获取到该参数那么应该用参数获取的注解@RequestParam。实现代码如下：
        ————————————————
        @RequestMapping(value="/problems/{problemId}")
        public String showProblem(@PathVariable int problemId, @RequestParam int Key){
            ；    //方法体
        }*/
    @GetMapping("/categories/{cid}/product")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid,
                                        @RequestParam(value = "start", defaultValue = "0") int start,
                                        @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<Product> page = productService.list(cid, start, size, 5);
        productImageService.setFirstProductImage(page.getContent());
        return page;
    }

    //添加product数据
    //添加数据 接收从前台传过来的json数据
    @PostMapping("products")
    public Product add(@RequestBody Product bean) throws Exception {
        bean.setCreateDate(new Date());
        productService.add(bean);
        return bean;
    }

    //删除delete数据
    @DeleteMapping("/product/{id}")
    public String delete(@PathVariable int id, HttpServletRequest httpServletRequest) throws Exception {
        productService.delete(id);
        return null;
    }

    //更新product数据
    @PutMapping("products")
    public Product update(@RequestBody Product bean) throws Exception {
        productService.update(bean);
        return bean;
    }

    //获取单个数据
    @GetMapping("product/{id}")
    public Product get(@PathVariable("id") int id) throws Exception {
        Product product = productService.get(id);
        return product;
    }
}
//	传统风格	                                        Restful风格
//      url	                            method	  url	           method
//增加	/addCategory?name=xxx	         POST	 /categories	    POST
//删除	/deleteCategory?id=123	         GET	 /categories/123	DELETE
//修改	/updateCategory?id=123&name=yyy	 POST	 /categories/123	PUT
//获取	/getCategory?id=123	             GET	 /categories/123	GET
//查询	/listCategory	                 GET	 /categories	    GET

/*URL示例1：http://localhost:8080/Springmvc/user/page.do?pageSize=3&pageNow=2
        可以把这地址分开理解，其中问号前半部分：http://localhost:8080/Springmvc/user/page.do 这个就是路径，即为请求url；
        而问号的后面部分就是请求参数部分，是要向请求路径提交的参数信息，用的就是@RequestParam ，对于这种参数，如果你要用的话，代码应该如下：
        @RequestMapping(value="/page.do")
        public String page(@RequestParam int pageSize,@RequestParam  int pageNow){
        方法参数名称必须和URL中参数部分的参数名称对应。
            ;    方法体
            } */
/*URL示例2：http://www.iteye.com/problems/101566?Key=123
        这个URL里面，服务器想获取问题编号101566，因为这个参数直接包含在请求路径部分中，所以代码中用的应该是@PathVariable；对于参数部分Key=123，如果想获取到该参数那么应该用参数获取的注解@RequestParam。实现代码如下：
        ————————————————
        @RequestMapping(value="/problems/{problemId}")
        public String showProblem(@PathVariable int problemId, @RequestParam int Key){
            ；    //方法体
        }*/
/*URL示例3： http://szkingdom.com/service/callback/8888/1111/1.0.0/3333/1023
        该URL中包含有多层级的路径参数，callback是在service后的一级请求地址（目录），callback后的都是数字都是对应服务器要获取的参数，那么获取这些请求路径部分里的参数则应该列顺序表示层次关系，请求的RequestMapping的形式依旧是参照路径样式设置，具体的获取参数方法类如下：
        @Controller
@Scope("prototype")
@RequestMapping("/service")
public class FrontGateController {
@RequestMapping(   value = {  "/callback/{appId}/{chnId}/{chnVer}/{bankId}/{bizId}" }, 
method = { RequestMethod.POST, RequestMethod.GET }   )
//注意：编码拦截器 Spring AOP 按照入参顺序取值，请不要修改入参顺序
public ModelAndView doCallback(
            @PathVariable("appId") String appId,
            @PathVariable("chnId") String chnId,
            @PathVariable("chnVer") String chnVer,
            @PathVariable("bizId") String bizId,
            @PathVariable("bankId") String bankId,
            HttpServletRequest request,
            HttpServletResponse response) {
        ;    //方法体
}
//其它方法定义
｝*/