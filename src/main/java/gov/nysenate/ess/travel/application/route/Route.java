package gov.nysenate.ess.travel.application.route;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Route {

    public static final Route EMPTY_ROUTE = new Route(ImmutableList.of(), ImmutableList.of());
    private static final double MILE_THRESHOLD = 35.0;

    private final ImmutableList<Leg> outboundLegs;
    private final ImmutableList<Leg> returnLegs;

    public Route(List<Leg> outboundLegs, List<Leg> returnLegs) {
        this.outboundLegs = ImmutableList.copyOf(outboundLegs);
        this.returnLegs = ImmutableList.copyOf(returnLegs);
    }

    public PerDiemList mealPerDiems() {
        return PerDiemList.ofLists(destinations().stream()
                .map(Destination::mealPerDiems)
                .collect(Collectors.toList()));
    }

    public PerDiemList lodgingPerDiems() {
        return PerDiemList.ofLists(destinations().stream()
        .map(Destination::lodgingPerDiems)
        .collect(Collectors.toList()));
    }

    public double totalMiles() {
        return getAllLegs().stream()
                .map(Leg::getMiles)
                .reduce(0.0, Double::sum);
    }

    public Dollars mileageExpense() {
        if (outgoingReimbursableMiles() < MILE_THRESHOLD) {
            return new Dollars("0");
        }

        return getAllLegs().stream()
                .map(Leg::mileageExpense)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    public Destination origin() {
        if (getOutboundLegs().size() > 0) {
            return getOutboundLegs().get(0).getFrom();
        }
        return null;
    }

    /**
     * @return A list of destinations the employee is visiting on the outgoing portion of their trip.
     */
    public List<Destination> destinations() {
        return getOutboundLegs().stream()
                .map(Leg::getTo)
                .collect(Collectors.toList());
    }

    /**
     * @return The first day of travel.
     */
    public LocalDate startDate() {
        if (getOutboundLegs().size() == 0) {
            return null;
        }
        return getOutboundLegs().stream()
                .map(Leg::getTravelDate)
                .min(LocalDate::compareTo)
                .get();
    }

    /**
     * @return The last day of travel.
     */
    public LocalDate endDate() {
        if (getReturnLegs().size() == 0) {
            return null;
        }
        return getReturnLegs().stream()
                .map(Leg::getTravelDate)
                .max(LocalDate::compareTo)
                .get();
    }

    public ImmutableList<Leg> getOutboundLegs() {
        return outboundLegs;
    }

    public ImmutableList<Leg> getReturnLegs() {
        return returnLegs;
    }

    protected ImmutableList<Leg> getAllLegs() {
        return Stream.concat(getOutboundLegs().stream(), getReturnLegs().stream())
                .collect(ImmutableList.toImmutableList());
    }

    private double outgoingReimbursableMiles() {
        return getOutboundLegs().stream()
                .filter(leg -> leg.qualifiesForMileageReimbursement())
                .mapToDouble(Leg::getMiles)
                .sum();
    }

    @Override
    public String toString() {
        return "Route{" +
                "outgoingLegs=" + outboundLegs +
                ", returnLegs=" + returnLegs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(outboundLegs, route.outboundLegs) &&
                Objects.equals(returnLegs, route.returnLegs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outboundLegs, returnLegs);
    }
}
