package gov.nysenate.ess.core.service.period;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.dao.period.PayPeriodDao;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodNotFoundEx;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import gov.nysenate.ess.core.util.SortOrder;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

@Service
public class EssCachedPayPeriodService implements PayPeriodService, CachingService<PayPeriodType>
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedPayPeriodService.class);

    @Autowired private PayPeriodDao payPeriodDao;
    @Autowired private EventBus eventBus;
    @Autowired private EhCacheManageService cacheManageService;

    private Cache payPeriodCache;

    @PostConstruct
    public void init() {
        this.eventBus.register(this);
        this.payPeriodCache = this.cacheManageService.registerEternalCache(getCacheType().name());
        if (this.cacheManageService.isWarmOnStartup()) {
            cacheAfPayPeriods();
        }
    }

    /**
     * Since we lookup pay periods by determining if the given date intersects the period's date range,
     * we need to store the periods in a tree map to avoid having to loop through each one to find the
     * one we want.
     */
    private static class PayPeriodCacheTree
    {
        private final RangeMap<LocalDate, PayPeriod> rangeMap = TreeRangeMap.create();

        public PayPeriodCacheTree(TreeSet<PayPeriod> periodSet) {
            periodSet.forEach(p -> {
                rangeMap.put(Range.closed(p.getStartDate(), p.getEndDate()), p);
            });
        }

        public PayPeriod getPayPeriod(LocalDate date) {
            PayPeriod period = rangeMap.get(date);
            if (period == null) throw new PayPeriodNotFoundEx("Pay period containing date " + date + " could not be found.");
            return period;
        }

        public List<PayPeriod> getPayPeriodsInRange(Range<LocalDate> dateRange, SortOrder dateOrder) {
            List<PayPeriod> payPeriods = new ArrayList<>(rangeMap.subRangeMap(dateRange).asMapOfRanges().values());
            if (dateOrder.equals(SortOrder.DESC)) {
                Collections.reverse(payPeriods);
            }
            return payPeriods;
        }
    }

    /** --- Pay Period Service Implemented Methods --- */

    @Override
    public PayPeriod getPayPeriod(PayPeriodType type, LocalDate date) throws PayPeriodNotFoundEx {
        if (type.equals(PayPeriodType.AF)) {
            PayPeriodCacheTree cacheTree = getCachedPayPeriodTree(type, true);
            if (cacheTree != null) {
                cacheTree.getPayPeriod(date);
            }
        }
        return payPeriodDao.getPayPeriod(type, date);
    }

    @Override
    public List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder) {
        if (type.equals(PayPeriodType.AF)) {
            PayPeriodCacheTree cacheTree = getCachedPayPeriodTree(type, true);
            if (cacheTree != null) {
                cacheTree.getPayPeriodsInRange(dateRange, dateOrder);
            }
        }
        return payPeriodDao.getPayPeriods(type, dateRange, dateOrder);
    }

    /** --- Caching Service Implemented Methods ---
     * @see CachingService */

    /** {@inheritDoc} */
    @Override
    public ContentCache getCacheType() {
        return ContentCache.PAY_PERIOD;
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(PayPeriodType key) {
        payPeriodCache.remove(key);
    }

    /** {@inheritDoc} */
    @Override
    public void evictCache() {
        logger.info("Clearing {} cache..", getCacheType());
        payPeriodCache.removeAll();
    }

    /** {@inheritDoc} */
    @Override
    public void warmCache() {
        cacheAfPayPeriods();
    }

    /** --- Internal Methods --- */

    private PayPeriodCacheTree getCachedPayPeriodTree(PayPeriodType type, boolean createIfEmpty) {
        payPeriodCache.acquireReadLockOnKey(type);
        Element element = payPeriodCache.get(type);
        payPeriodCache.releaseReadLockOnKey(type);
        if (element != null) {
            return (PayPeriodCacheTree) element.getObjectValue();
        }
        if (createIfEmpty) {
            cachePayPeriods(type);
            // Try again.
            payPeriodCache.acquireReadLockOnKey(type);
            element = payPeriodCache.get(type);
            payPeriodCache.releaseReadLockOnKey(type);
            if (element == null) throw new IllegalStateException(type + " Pay Periods are not caching properly.");
            return (PayPeriodCacheTree) element.getObjectValue();
        }
        return null;
    }

    @Scheduled(cron = "${cache.cron.period}")
    private void cacheAfPayPeriods() {
        cachePayPeriods(PayPeriodType.AF);
    }

    private void cachePayPeriods(PayPeriodType type) {
        logger.debug("Fetching all {} pay period recs for caching...", type);
        Range<LocalDate> cacheRange = Range.upTo(LocalDate.now().plusYears(2), BoundType.CLOSED);
        TreeSet<PayPeriod> payPeriods =
            new TreeSet<>(payPeriodDao.getPayPeriods(type, cacheRange, SortOrder.ASC));
        payPeriodCache.acquireWriteLockOnKey(type);
        payPeriodCache.remove(type);
        payPeriodCache.put(new Element(type, new PayPeriodCacheTree(payPeriods)));
        payPeriodCache.releaseWriteLockOnKey(type);
        logger.info("Done caching {} {} pay period records.", payPeriods.size(), type);
    }
}