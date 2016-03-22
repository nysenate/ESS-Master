package gov.nysenate.ess.supply.order;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

import java.time.LocalDateTime;
import java.util.SortedMap;

/**
 * A collection of order versions
 */
public final class OrderHistory {

    private final ImmutableSortedMap<LocalDateTime, OrderVersion> orderVersionMap;

    private OrderHistory(ImmutableSortedMap<LocalDateTime, OrderVersion> orderVersionMap) {
        this.orderVersionMap = orderVersionMap;
    }

    /**
     * Static constructors
     */

    public static OrderHistory of(LocalDateTime modifyDateTime, OrderVersion version) {
        return new OrderHistory(ImmutableSortedMap.of(modifyDateTime, version));
    }

    public static OrderHistory of(SortedMap<LocalDateTime, OrderVersion> orderVersionMap) {
        return new OrderHistory(ImmutableSortedMap.copyOf(orderVersionMap));
    }

    public static OrderHistory of(ImmutableSortedMap<LocalDateTime, OrderVersion> orderVersionMap) {
        return new OrderHistory(orderVersionMap);
    }

    /**
     * Functional Methods
     */

    public LocalDateTime getOrderedDateTime() {
        return orderVersionMap.firstKey();
    }

    public OrderVersion get(LocalDateTime modifiedDateTime) {
        return orderVersionMap.get(modifiedDateTime);
    }

    public OrderVersion current() {
        return orderVersionMap.get(orderVersionMap.lastKey());
    }

    public LocalDateTime getModifiedDateTime() {
        return orderVersionMap.lastKey();
    }

    public ImmutableSortedMap<LocalDateTime, OrderVersion> getHistory() {
        return orderVersionMap;
    }

    protected OrderHistory addVersion(LocalDateTime modifiedDateTime, OrderVersion version) {
        ImmutableSortedMap versions = new ImmutableSortedMap.Builder<LocalDateTime, OrderVersion>(Ordering.natural())
                .putAll(orderVersionMap).put(modifiedDateTime, version).build();
        return OrderHistory.of(versions);
    }

    protected int size() {
        return orderVersionMap.size();
    }

    @Override
    public String toString() {
        return "OrderHistory{" +
               "orderVersionMap=" + orderVersionMap +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderHistory that = (OrderHistory) o;
        return !(orderVersionMap != null ? !orderVersionMap.equals(that.orderVersionMap) : that.orderVersionMap != null);
    }

    @Override
    public int hashCode() {
        return orderVersionMap != null ? orderVersionMap.hashCode() : 0;
    }
}