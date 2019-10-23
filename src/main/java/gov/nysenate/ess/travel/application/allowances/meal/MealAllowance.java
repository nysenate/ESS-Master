package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.provider.gsa.meal.MealTier;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class MealAllowance {

    private final UUID id;
    private final TravelAddress address;
    private final LocalDate date;
    private final MealTier mealTier;
    private final boolean isMealsRequested;

    public MealAllowance(UUID id, TravelAddress address, LocalDate date, MealTier mealTier, boolean isMealsRequested) {
        this.id = id;
        this.address = address;
        this.date = date;
        this.mealTier = mealTier;
        this.isMealsRequested = isMealsRequested;
    }

    public Dollars allowance() {
        if (isMealsRequested()) {
            return mealTier.total();
        }
        return Dollars.ZERO;
    }

    protected UUID getId() {
        return id;
    }

    protected TravelAddress getAddress() {
        return address;
    }

    protected LocalDate getDate() {
        return date;
    }

    protected MealTier getMealTier() {
        return mealTier;
    }

    protected boolean isMealsRequested() {
        return isMealsRequested;
    }

    @Override
    public String toString() {
        return "MealAllowanceDay{" +
                "address=" + address +
                ", date=" + date +
                ", mealTier=" + mealTier +
                ", isMealsRequested=" + isMealsRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealAllowance that = (MealAllowance) o;
        return isMealsRequested == that.isMealsRequested &&
                Objects.equals(address, that.address) &&
                Objects.equals(date, that.date) &&
                Objects.equals(mealTier, that.mealTier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, date, mealTier, isMealsRequested);
    }
}
