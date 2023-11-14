package com.opentool.ai.tool.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/29 21:15
 */
public class TtsLocalCache {
    // 缓存时长
    public static final long TIMEOUT = 9 * DateUnit.MINUTE.getMillis();
    // 清除问题
    private static final long CLEAN_TIMEOUT = 9 * DateUnit.MINUTE.getMillis();
    // 缓存对象
    public  static final TimedCache<String, Object> CACHE = CacheUtil.newTimedCache(TIMEOUT);

    static {
        // 启动定时任务

        CACHE.schedulePrune(CLEAN_TIMEOUT);
    }
}