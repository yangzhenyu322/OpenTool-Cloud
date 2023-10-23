package com.opentool.system.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;

/**
 * 本地缓存
 * / @Author: ZenSheep
 * / @Date: 2023/10/19 15:10
 */
public class LocalCache {
     // 缓存时长
    public static final long TIMEOUT = 5 * DateUnit.MINUTE.getMillis();
     // 清除问题
    private static final long CLEAN_TIMEOUT = 5 * DateUnit.MINUTE.getMillis();
    // 缓存对象
    public  static final TimedCache<String, Object> CACHE = CacheUtil.newTimedCache(TIMEOUT);

    static {
        // 启动定时任务
        CACHE.schedulePrune(CLEAN_TIMEOUT);
    }
}
