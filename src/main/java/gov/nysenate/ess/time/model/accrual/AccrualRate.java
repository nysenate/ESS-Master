package gov.nysenate.ess.time.model.accrual;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import gov.nysenate.ess.core.util.RangeUtils;

import java.math.BigDecimal;

import static gov.nysenate.ess.time.util.AccrualUtils.roundSickVacHours;

/**
 * Provides accrual rates for vacation and sick time based on number of biweekly pay periods.
 * These rates apply only to regular annual employees.
 */
public enum AccrualRate
{
    /**
     * Vacation rates increase as you work longer until you reach 5.5
     */
    VACATION(
            RangeUtils.toRangeMap(
                    ImmutableSortedMap.<Integer, BigDecimal>naturalOrder()
                            .put(0, BigDecimal.ZERO)
                            .put(13, new BigDecimal("31.5"))
                            .put(14, new BigDecimal("3.5"))
                            .put(27, new BigDecimal("3.75"))
                            .put(53, new BigDecimal("4"))
                            .put(79, new BigDecimal("5.5"))
                            .build()
            ),
            new BigDecimal("210")
    ),

    /** Sick rates are fixed at 3.5 */
    SICK(
            ImmutableRangeMap.<Integer, BigDecimal>builder()
                    .put(Range.atLeast(0), new BigDecimal("3.5"))
                    .build(),
            new BigDecimal("1400")
    ),
    ;

    private ImmutableRangeMap<Integer, BigDecimal> accRateMap;
    private BigDecimal maxHoursBanked;

    AccrualRate(RangeMap<Integer, BigDecimal> accRateMap, BigDecimal maxHoursBanked) {
        this.accRateMap = ImmutableRangeMap.copyOf(accRateMap);
        this.maxHoursBanked = maxHoursBanked;
    }

    /**
     * Retrieve the accrual rate based on the payPeriods.
     *
     * @param payPeriods int
     * @return BigDecimal with rate stored
     */
    public BigDecimal getRate(int payPeriods) {
        if (payPeriods < 0) {
            throw new IllegalStateException("Cannot have negative amount of pay periods.  Received: " + payPeriods);
        }
        return accRateMap.get(payPeriods);
    }

    /**
     * Retrieves the rate using a prorated percentage (occurs when one does not
     * work 1820 hours and accrue at a rate proportional to the number of hours
     * they are expected to work in a year).
     *
     * @param payPeriods int
     * @param proratePercentage BigDecimal (percentage e.g 0.5)
     * @return BigDecimal with prorated accrual rate to the nearest .25.
     */
    public BigDecimal getRate(int payPeriods, BigDecimal proratePercentage) {
        return roundSickVacHours(getRate(payPeriods).multiply(proratePercentage));
    }

    /**
     * Returns the maximum number of hours that can be rolled over to the next year.
     * @return BigDecimal
     */
    public BigDecimal getMaxHoursBanked() {
        return maxHoursBanked;
    }
}
