package com.opentool.ai.tool.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;

/** Message本地缓存
 * / @Author: ZenSheep
 * / @Date: 2023/10/23 15:14
 */
public class MessageLocalCache {
    // 缓存时长
    public static final long TIMEOUT = 10 * DateUnit.HOUR.getMillis();
    // 清除问题
    private static final long CLEAN_TIMEOUT = 10 * DateUnit.HOUR.getMillis();
    // 缓存对象
    public  static final TimedCache<String, Object> CACHE = CacheUtil.newTimedCache(TIMEOUT);

    static {
        // 启动定时任务
        CACHE.schedulePrune(CLEAN_TIMEOUT);
    }
}
