package com.whx.tmall.comparator;

import com.whx.tmall.pojo.Product;

import java.util.Comparator;

//价格比较器
//把 销量高的放前面
public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return (int) (p1.getPromotePrice()- p2.getPromotePrice());
    }
}
