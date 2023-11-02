package com.example.User_Service.Service;


import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

@Service
@EnableCaching
public class TokenBlacklistService {

    private final Cache blacklistCache;

    public TokenBlacklistService(RedisCacheManager cacheManager) {
        this.blacklistCache = cacheManager.getCache("blacklist");
    }

    public void blacklistToken(String token) {
        System.out.println("line 21 blaklist token : "+ token);
        blacklistCache.put(token, true);
    }

//    @Cacheable(cacheNames = "blacklist")
    public boolean isTokenBlacklisted(String token) {
        System.out.println("inside is token blacklisted");
        Cache.ValueWrapper valueWrapper = blacklistCache.get(token);
        if(valueWrapper==null){
            return false;
        }
        System.out.println("line 28 "+ valueWrapper.get());
        return true;
    }
}
