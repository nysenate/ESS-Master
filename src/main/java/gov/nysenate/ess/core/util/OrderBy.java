package gov.nysenate.ess.core.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

public class OrderBy
{
    private static OrderBy NO_ORDER = new OrderBy();

    /** An immutable mapping of column names to sort order. */
    private ImmutableMap<String, SortOrder> sortColumns = ImmutableMap.of();

    /** --- Constructors --- */

    public OrderBy() {}

    public OrderBy(ImmutableMap<String, SortOrder> sortColumns) {
        this.sortColumns = sortColumns;
    }

    public OrderBy(String k1, SortOrder v1) {
        this.sortColumns = ImmutableSortedMap.of(k1, v1);
    }

    public OrderBy(String k1, SortOrder v1, String k2, SortOrder v2) {

        this.sortColumns = ImmutableSortedMap.of(k1, v1, k2, v2);
    }

    public OrderBy(String k1, SortOrder v1, String k2, SortOrder v2, String k3, SortOrder v3) {
        this.sortColumns = ImmutableSortedMap.of(k1, v1, k2, v2, k3, v3);
    }

    /** --- Basic Getters/Setters --- */

    public ImmutableMap<String, SortOrder> getSortColumns() {
        return sortColumns;
    }
}
