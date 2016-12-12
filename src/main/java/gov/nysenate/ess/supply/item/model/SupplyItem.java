package gov.nysenate.ess.supply.item.model;

import gov.nysenate.ess.supply.allowance.ItemVisibility;

public final class SupplyItem {

    private final int id;
    private final String commodityCode;
    private final String description;
    private final ItemStatus status;
    private final Category category;
    private final ItemAllowance allowance;
    private final ItemUnit unit;
    /** Number of items per unit. eg. 12/PKG would equal 12 */
    private final ItemVisibility visibility;

    public SupplyItem(int id, String commodityCode, String description, ItemStatus status,
                      Category category, ItemAllowance allowance,
                      ItemUnit unit, ItemVisibility visibility) {
        this.id = id;
        this.commodityCode = commodityCode;
        this.description = description;
        this.status = status;
        this.category = category;
        this.allowance = allowance;
        this.unit = unit;
        this.visibility = visibility;
    }

    public int getId() {
        return id;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public String getDescription() {
        return description;
    }

    public String getUnitDescription() {
        return unit.getDescription();
    }

    public Category getCategory() {
        return category;
    }

    public int getOrderMaxQty() {
        return allowance.getPerOrderAllowance();
    }

    public int getMonthlyMaxQty() {
        return allowance.getPerMonthAllowance();
    }

    public int getUnitQuantity() {
        return unit.getQuantity();
    }

    public ItemVisibility getVisibility() {
        return visibility;
    }

    public boolean requiresSynchronization() {
        return status.requiresSynchronization();
    }

    public boolean isExpendable() {
        return status.isExpendable();
    }

    @Override
    public String toString() {
        return "SupplyItem{" +
                "id=" + id +
                ", commodityCode='" + commodityCode + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", category=" + category +
                ", allowance=" + allowance +
                ", unit=" + unit +
                ", visibility=" + visibility +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplyItem that = (SupplyItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
