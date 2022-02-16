package com.whx.tmall.web;

import com.whx.tmall.pojo.Category;
import com.whx.tmall.service.CategoryService;
import com.whx.tmall.util.ImageUtil;
import com.whx.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//JPA:M_Controller
//这个就是专门用来提供 RESTFUL 服务器控制器,用来操作Service
//就是 HTTP 协议里面，四个表示操作方式的动词：GET、POST、PUT、DELETE。
// 它们分别对应四种基本操作：GET 用来获取资源，POST 用来新建资源，PUT 用来更新资源，DELETE 用来删除资源。

@RestController  //表示这是一个控制器，并且对每个方法的返回值都会直接转换为 json 数据格式。
public class CategoryController {
    @Autowired
    CategoryService categoryService;

//      @GetMapping("/categories")
//    public List<Category> list() throws Exception {
//        return categoryService.list();
//    }

    //分页处理 处理并返回超链数以及相关信息  获取资源
    /*RequestParam处理的是请求参数，而PathVariable处理的是路径变量,*/
    @GetMapping("/categories")
    public Page4Navigator<Category> list(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        //规定最大超链接数为5
        Page4Navigator<Category> page = categoryService.list(start, size, 5);
        return page;
    }

    //分类信息以及图片上传
    @PostMapping("categories")  //新建资源
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        //从请求中读出post请求的formData数据，自动装配name属性到bean上后保存到数据库中
        categoryService.add(bean);
        //将分类图片不保存在本地数据库上，而是选择保存上项目的中
        //接受上传图片，并保存到 img/category目录下
        //文件名使用新增分类的id
        savaOrUpdateImageFile(bean, image, request);
        return bean;
    }

    //上传文件，这个有问题 如果上传png他只能保存到缓存当中
    public void savaOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, bean.getId() + ".jpg");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        image.transferTo(file); //把内存图片写入磁盘中
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

    //删除信息  返回 null, 会被RESTController 转换为空字符串。  删除资源
    @DeleteMapping("/categories/{id}") //PathVariable处理的是路径变量
    public String delete(@PathVariable("id") int id, HttpServletRequest request) {
        categoryService.delete(id);
        //从文件流中删除对应id路径下的文件
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id + ".jpg");
        file.delete();
        return null;
    }

    //更新资源 更新某个数据
    @PutMapping("/category/{id}")
    //发送过来的id参数可自动封装到Category上
    //put的参数要使用request.getParameter获取
    public Category update(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        String name = request.getParameter("name");
        bean.setName(name);
        categoryService.update(bean);
        //并将本地文件进行覆盖
        if (image != null) {
            savaOrUpdateImageFile(bean, image, request);
        }
        return bean;
    }

//传统风格	                                        Restful风格
//      url	                            method	  url	           method
//增加	/addCategory?name=xxx	         POST	 /categories	    POST
//删除	/deleteCategory?id=123	         GET	 /categories/123	DELETE
//修改	/updateCategory?id=123&name=yyy	 POST	 /categories/123	PUT
//获取	/getCategory?id=123	             GET	 /categories/123	GET
//查询	/listCategory	                 GET	 /categories	    GET
}


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