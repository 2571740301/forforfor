package com.whx.tmall.service;

import com.whx.tmall.dao.ProductDAO;
import com.whx.tmall.es.ProductESDAO;
import com.whx.tmall.pojo.Category;
import com.whx.tmall.pojo.Product;
import com.whx.tmall.util.Page4Navigator;
import com.whx.tmall.util.SpringContextUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ReviewService reviewService;

    //添加ES支持
    @Autowired
    ProductESDAO productESDAO;

    @Cacheable(key = "'products-cid-'+#p0+'-page-'+#p1+'-'+ #p2")
    //加载数据
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> pageFromJPA = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    @CacheEvict(allEntries = true)
    //添加数据
    public void add(Product bean) {
        productDAO.save(bean);
        //添加es支持，即除了dao的支持外还需要将数据通过ElasticsearchRepository同步到es上
        productESDAO.save(bean);
    }


    //删除数据
    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productDAO.delete(id);
        productESDAO.delete(id);
    }

    //更新数据 传入整个bean
    @CacheEvict(allEntries = true)
    public void update(Product product) {
        productDAO.save(product);
        productESDAO.save(product);
    }

    //获取单个数据
    @Cacheable(key = "'products-one-'+#p0")
    public Product get(int id) {
        return productDAO.findOne(id);
    }

    public void fill(List<Category> categories) {
        for (Category category : categories) {
            fill(category);
        }
    }

    public void fill(Category category) {
        //springboot 的缓存机制是通过切面编程 aop来实现的。
        //从fill方法里直接调用 listByCategory 方法， aop 是拦截不到的，也就不会走缓存了。
        //所以要通过这种 绕一绕 的方式故意诱发 aop, 这样才会想我们期望的那样走redis缓存。
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProductImage(products);
        category.setProducts(products);
    }

    @Cacheable(key = "'products-cid-'+#p0.id")
    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    //为多个分类填充推荐产品集合，即把分类下的产品集合，按照8个为一行，拆成多行，以利于后续页面上进行显示
    public void fillByRow(List<Category> categories) {
        int productNumberEachRow = 8;//每行最多八个产品
        for (Category category : categories) {  //将List<Product> 进行分拆装入List<List<Product>>
            List<Product> products = category.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i += productNumberEachRow) { //i = productNumberEachRow + i
                int size = i + productNumberEachRow;// size = 8;
                size = size > products.size() ? products.size() : size;// 判断product的数量是否大于8
                List<Product> productOfEachRow = products.subList(i, size);
                productsByRow.add(productOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    //实现为产品设置销量和评价数量的方法
    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);
    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products) {
            setSaleAndReviewNumber(product);
        }
    }

    //取出搜索结果
    public List<Product> search(String keyword, int start, int size) {
        /* ------------------       es搜索         ------------------      */
        //初始化搜索结果到es上
        initDatabase2ES();
        //使用FunctionScoreQueryBuilder优化es搜索结果  FunctionScoreQueryBuilder: 功能评分查询生成器
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery() // QueryBuilders 查询生成器
                .add(QueryBuilders.matchPhraseQuery("name", keyword), //matchPhraseQuery匹配短语查询
                        ScoreFunctionBuilders.weightFactorFunction(100)) //ScoreFunctionBuilders 评分函数构建器 weightFactorFunction 权重因子函数
                .scoreMode("sum")//评分模式:设置权重分 求和模式  
                .setMinScore(10);//设置权重分最低分
        // 设置分页
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        //传入es搜索
        SearchQuery searchQuery = new NativeSearchQueryBuilder() // searchQuery 搜索查询
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
        Page<Product> page = productESDAO.search(searchQuery);
        return page.getContent();

    }

    //初始化数据到es上
    public void initDatabase2ES() {
        //搜索前试看es中是否存在相应的数据，如果不存在则通过dao从数据库中获取数据然后初始化到es上
        Pageable pageable = new PageRequest(0, 5);
        Page<Product> page = productESDAO.findAll(pageable);
        if (page.getContent().isEmpty()) {
            List<Product> products = productDAO.findAll();
            for (Product product : products) {
                productESDAO.save(product);
            }
        }
    }


    /* ------------------       es          ------------------      */
}
