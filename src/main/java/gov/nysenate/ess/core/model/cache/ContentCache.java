package gov.nysenate.ess.core.model.cache;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Arrays;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum ContentCache
{
    EMPLOYEE,
    ACCRUAL_ANNUAL,
    HOLIDAY,
    PAY_PERIOD,
    TRANSACTION,
    SUPERVISOR_EMP_GROUP,
    ACTIVE_TIME_RECORDS,
    LOCATION
    ;

    private static final ImmutableSet<ContentCache> allContentCaches =
            Sets.immutableEnumSet(Arrays.asList(ContentCache.values()));

    public static ImmutableSet<ContentCache> getAllContentCaches() {
        return allContentCaches;
    }
}