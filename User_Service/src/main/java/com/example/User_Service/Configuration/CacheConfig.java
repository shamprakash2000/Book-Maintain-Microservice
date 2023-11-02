//package com.example.User_Service.Configuration;
//
//
//import com.github.benmanes.caffeine.cache.Caffeine;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CachingConfigurerSupport;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.caffeine.CaffeineCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//@EnableCaching
//public class CacheConfig extends CachingConfigurerSupport {
//
//    @Bean
//    public CacheManager cacheManager() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager("blacklist");
//        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)); // Set the cache duration here
//        return cacheManager;
//    }
//}
