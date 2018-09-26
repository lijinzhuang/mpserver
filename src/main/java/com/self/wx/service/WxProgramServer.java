package com.self.wx.service;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.self.wx.auto.dao.BooksDAO;
import com.self.wx.auto.model.BooksDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lijz on 2018/3/21.
 */
@Component
public class WxProgramServer {
    @Autowired
    private BooksDAO booksDAO;
    private static final String ISEMPTY = "暂时未找该图书";
    private static LoadingCache<String,List<BooksDO>> cache = null;

    public String searchBooksByName(String name) {
        List<BooksDO> list = null;
        try {
            list = booksDAO.selectByName("%" + name + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(list);
    }
    public String searchBookByTypeId(String id){
//        List<BooksDO> list = null;
        try {
            if(null == cache) {
                cache = Caffeine.newBuilder()
                        .initialCapacity(1)
                        .maximumSize(100)
                        .expireAfterAccess(5, TimeUnit.MINUTES)
                        .build(new CacheLoader<String, List<BooksDO>>() {
                            @Override
                            public List<BooksDO> load(String key) throws Exception {
                                return booksDAO.selectByType(key);
                            }
                        });
//            list = booksDAO.selectByType(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(cache.get(id));
    }
}
