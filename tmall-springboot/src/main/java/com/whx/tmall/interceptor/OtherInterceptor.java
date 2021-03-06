package com.whx.tmall.interceptor;

import com.whx.tmall.pojo.Category;
import com.whx.tmall.pojo.OrderItem;
import com.whx.tmall.pojo.User;
import com.whx.tmall.service.CategoryService;
import com.whx.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class OtherInterceptor implements HandlerInterceptor {
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    CategoryService categoryService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {
        //Controller方法处理完之后，DispatcherServlet进行视图的渲染之前拦截,需要preHandler为true才行
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute("user");
        //功能一：视图渲染显示某一用户购物车里有多少件商品
        //逻辑：通过用户从数据库OrderITem表中读出具体数量
        int cartTotalItemNumber = 0;
        if (null != user) {
            List<OrderItem> orderItems = orderItemService.listByUser(user);
            for (OrderItem orderItem : orderItems) {
                cartTotalItemNumber += orderItem.getNumber();
            }
        }
        //将数量设置到session上
   

        //页面渲染全局变量定义
        //Web应用中servlet可以使用getServletContext上下文得到：
        //1.在调用期间保存和检索属性的功能，并与其他servlet共享这些属性。
        //2.读取Web应用中文件内容和其他静态资源的功能。
        //3.互相发送请求的方式。
        //4.记录错误和信息化消息的功能。
        //功能二：优化路径
        
        //将上下文的值，写在servlet的上，以共享.
        //将上下文路径的内容在前端以${application.contextPath}获得
        
        //路径:include/fore/search.html
        //功能三 搜索栏下设置搜索的分类,同样地，也是写在Servlet上。以${application.categories_below_search}读取
        List<Category> cs= categoryService.list();//getServletContext.getContextPath();为获取路径上下文
        String contextPath = httpServletRequest.getServletContext().getContextPath();
        httpServletRequest.getServletContext().setAttribute("categories_below_search",cs);
        
        httpServletRequest.getServletContext().setAttribute("contextPath", contextPath);
        session.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
