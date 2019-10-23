package gov.nysenate.ess.travel.accommodation;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public class Accommodation {

    private final Address address;
    private final ImmutableSet<Day> days;
    private final ImmutableSet<Night> nights;

    public Accommodation(Address address, ImmutableSet<Day> days, ImmutableSet<Night> nights) {
        this.address = address;
        this.days = days;
        this.nights = nights;
    }

    /**
     * @return The total meal allowance for this accommodation.
     */
    public Dollars mealAllowance() {
        return getDays().stream()
                .map(Day::mealAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * @return The total lodging allowance for this accommodation.
     */
    public Dollars lodgingAllowance() {
        return getNights().stream()
                .map(Night::lodgingAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * @return The planned date of arrival.
     */
    public LocalDate arrivalDate() {
        return getDays().asList().get(0).getDate();
    }

    /**
     * @return The planned date of departure.
     */
    public LocalDate departureDate() {
        return getDays().asList().reverse().get(0).getDate();
    }

    protected Address getAddress() {
        return address;
    }

    protected ImmutableSet<Day> getDays() {
        return days;
    }

    protected ImmutableSet<Night> getNights() {
        return nights;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "address=" + address +
                ", days=" + days +
                ", nights=" + nights +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accommodation that = (Accommodation) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(days, that.days) &&
                Objects.equals(nights, that.nights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, days, nights);
    }
}