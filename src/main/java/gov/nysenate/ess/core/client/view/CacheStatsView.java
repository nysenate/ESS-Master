package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import net.sf.ehcache.statistics.StatisticsGateway;

public class CacheStatsView implements ViewObject
{
    protected String cacheName;
    protected long heapSizeMb;
    protected long size;
    protected long hitCount;
//  TODO figure out why StatisticsGateway::cacheHitRatio always returns NaN
//    protected double hitRatio;
    protected long missCount;
    protected long addedCount;
    protected long updatedCount;
    protected long removeCount;
    protected long evictedCount;
    protected long expiredCount;

    public CacheStatsView(StatisticsGateway stats) {
        if (stats != null) {
            this.cacheName = stats.getAssociatedCacheName();
            this.heapSizeMb = stats.getLocalHeapSizeInBytes() / (1024 * 1024);
            this.size = stats.getSize();
            this.hitCount = stats.cacheHitCount();
            this.missCount = stats.cacheMissCount();
            this.addedCount = stats.cachePutAddedCount();
            this.updatedCount = stats.cachePutUpdatedCount();
            this.removeCount = stats.cacheRemoveCount();
            this.evictedCount = stats.cacheEvictedCount();
            this.expiredCount = stats.cacheExpiredCount();
        }
    }

    @Override
    public String getViewType() {
        return "cache-stats";
    }

    public String getCacheName() {
        return cacheName;
    }

    public long getHeapSizeMb() {
        return heapSizeMb;
    }

    public long getSize() {
        return size;
    }

    public long getHitCount() {
        return hitCount;
    }
//
//    public double getHitRatio() {
//        return hitRatio;
//    }

    public long getMissCount() {
        return missCount;
    }

    public long getAddedCount() {
        return addedCount;
    }

    public long getUpdatedCount() {
        return updatedCount;
    }

    public long getRemoveCount() {
        return removeCount;
    }

    public long getEvictedCount() {
        return evictedCount;
    }

    public long getExpiredCount() {
        return expiredCount;
    }
}

