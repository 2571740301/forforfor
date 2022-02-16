package com.whx.tmall;

import com.whx.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableCaching
@EnableElasticsearchRepositories(basePackages = "com.whx.tmall.es")
@EnableJpaRepositories(basePackages = {"com.whx.tmall.dao","com.whx.tmall.pojo"})
@SpringBootApplication
public class Application {
    static {
        PortUtil.checkPort(6379,"Redis服务端",true);
        PortUtil.checkPort(9300,"ElasticSearch 服务端",true);
        PortUtil.checkPort(5601,"Kibana 服务端",true);
    }

  public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    //
  }
}
