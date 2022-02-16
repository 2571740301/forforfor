package com.whx.tmall.web;

import com.whx.tmall.pojo.Product;
import com.whx.tmall.pojo.ProductImage;
import com.whx.tmall.service.ProductImageService;
import com.whx.tmall.service.ProductService;
import com.whx.tmall.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//	传统风格	                                        Restful风格
//      url	                            method	  url	           method
//增加	/addCategory?name=xxx	         POST	 /categories	    POST
//删除	/deleteCategory?id=123	         GET	 /categories/123	DELETE
//修改	/updateCategory?id=123&name=yyy	 POST	 /categories/123	PUT
//获取	/getCategory?id=123	             GET	 /categories/123	GET
//查询	/listCategory	                 GET	 /categories	    GET

@RestController
public class ProductImageController {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;

    @GetMapping("products/{pid}/productImages")
    //  var url =  "products/"+pid+"/"+this.uri+"?type=single";
    public List<ProductImage> list(@PathVariable int pid,
                                   @RequestParam("type") String type) throws Exception {
        Product product = productService.get(pid);
        //判断类型是single还是detail
        if (ProductImageService.type_single.equals(type)) { //single
            List<ProductImage> singles = productImageService.listSingleProductImages(product);
            return singles;
        }
        if (ProductImageService.type_detail.equals(type)) { //detail
            List<ProductImage> details = productImageService.listDetailProductImage(product);
            return details;
        }
        return new ArrayList<>();
    }

    //添加图片
    // productImages?type=single&pid=pid~~~
    //MultipartFile image为post提交的图片信息
    @PutMapping("productImages")
    public Object add(@RequestParam("type") String type, @RequestParam("pid") int pid,
                      MultipartFile image, HttpServletRequest request) throws Exception { // MultipartFile image 接收上传的文件
        //获取新增的ProductImages信息，并添加到数据库中
        ProductImage bean = new ProductImage();
        Product product = productService.get(pid);
        bean.setProduct(product);
        bean.setType(type);
        productImageService.add(bean);

        //将图片文件保存在本地
        String folder = "img/";
        if (ProductImageService.type_single.equals(bean.getType())) {
            folder = folder + "productSingle";
        } else {
            folder = folder + "productDetail";
        }
        ////通过req.getServletContext().getRealPath("") 获取当前项目的真实路径
        File imageFolder = new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder, bean.getId() + ".jpg");
        String fileName = file.getName();
        //第一次运行的时候，这个文件所在的目录往往是不存在的，这里需要创建一下目录
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            image.transferTo(file);//把浏览器上传的文件复制到希望的位置
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ProductImageService.type_single.equals(bean.getType())) {
            String imageFolder_small = request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle = request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(file, 56, 56, f_small);
            ImageUtil.resizeImage(file, 217, 190, f_middle);
        }
        return bean;
    }

    //删除图片
    @DeleteMapping("productImages/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) throws Exception {
        ProductImage bean = productImageService.get(id);
        productImageService.delete(id);

        String folder = "img/";
        if (ProductImageService.type_single.equals(bean.getType())) {
            folder = folder + "productSingle";
        } else {
            folder = folder + "productDetail";
        }
        File imageFolder = new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder, bean.getId() + ".jpg");
        String fileName = file.getName();
        file.delete();

        if (ProductImageService.type_single.equals(bean.getType())) {
            String imageFolder_small = request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle = request.getServletContext().getRealPath("img/imageSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.delete();
            f_middle.delete();
        }
        return null;
    }
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