package gov.nysenate.ess.travel.application.route.destination;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.route.PerDiem;
import gov.nysenate.ess.travel.application.route.PerDiemList;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Destination {

    private int id;
    private final Address address;
    private final Range<LocalDate> dateRange;
    private final TreeMap<LocalDate, Dollars> mealPerDiems;
    private final TreeMap<LocalDate, Dollars> lodgingPerDiems;

    public Destination(Address address, LocalDate arrival, LocalDate departure) {
        this(0, address, arrival, departure, new TreeMap<>(), new TreeMap<>());
    }

    public Destination(int id, Address address, LocalDate arrival, LocalDate departure,
                       Map<LocalDate, Dollars> mealPerDiems,
                       Map<LocalDate, Dollars> lodgingPerDiems) {
        this.id = id;
        this.address = address;
        this.dateRange = arrival != null && departure != null ? Range.closed(arrival, departure) : null;
        this.mealPerDiems = new TreeMap<>(mealPerDiems);
        this.lodgingPerDiems = new TreeMap<>(lodgingPerDiems);
    }

    public PerDiemList mealPerDiems() {
        return new PerDiemList(getMealPerDiems().entrySet().stream()
                .map(entry -> new PerDiem(getAddress(), entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
    }

    public PerDiemList lodgingPerDiems() {
        return new PerDiemList(getLodgingPerDiems().entrySet().stream()
                .map(entry -> new PerDiem(getAddress(), entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));

    }

    public int getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * @param date
     * @return true if this destination will be visited on {@code date}. Otherwise false.
     */
    public boolean wasVisitedOn(LocalDate date) {
        return getDateRange().contains(date);
    }

    /**
     * @return A list of dates, each representing an overnight stay at this destination.
     */
    public List<LocalDate> nights() {
        if (arrivalDate().equals(departureDate())) {
            return new ArrayList<>();
        }

        List<LocalDate> nights = new ArrayList<>();
        for (LocalDate date = arrivalDate().plusDays(1); date.isBefore(departureDate().plusDays(1)); date = date.plusDays(1)) {
            nights.add(date);
        }

        return nights;
    }

    public void addMealPerDiem(LocalDate date, Dollars perDiem) {
        getMealPerDiems().put(date, perDiem);
    }

    public void addLodgingPerDiem(LocalDate date, Dollars perDiem) {
        getLodgingPerDiems().put(date, perDiem);
    }

    void setId(int id) {
        this.id = id;
    }

    LocalDate arrivalDate() {
        return getDateRange().lowerEndpoint();
    }

    LocalDate departureDate() {
        return getDateRange().upperEndpoint();
    }

    Range<LocalDate> getDateRange() {
        return dateRange;
    }

    TreeMap<LocalDate, Dollars> getMealPerDiems() {
        return mealPerDiems;
    }

    TreeMap<LocalDate, Dollars> getLodgingPerDiems() {
        return lodgingPerDiems;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "address=" + address +
                ", dateRange=" + dateRange +
                ", mealPerDiems=" + mealPerDiems +
                ", lodgingPerDiems=" + lodgingPerDiems +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(dateRange, that.dateRange) &&
                Objects.equals(mealPerDiems, that.mealPerDiems) &&
                Objects.equals(lodgingPerDiems, that.lodgingPerDiems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, dateRange, mealPerDiems, lodgingPerDiems);
    }
}