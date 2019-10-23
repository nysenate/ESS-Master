package gov.nysenate.ess.travel.application.route;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.application.route.destination.Destination;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Route {

    public static final Route EMPTY_ROUTE = new Route(ImmutableList.of(), ImmutableList.of());

    private final ImmutableList<Leg> outboundLegs;
    private final ImmutableList<Leg> returnLegs;

    public Route(List<Leg> outboundLegs, List<Leg> returnLegs) {
        this.outboundLegs = ImmutableList.copyOf(outboundLegs);
        this.returnLegs = ImmutableList.copyOf(returnLegs);
    }

    public MealPerDiems mealPerDiems() {
        return new MealPerDiems(destinations().stream()
                .map(Destination::mealPerDiems)
                .flatMap(m -> m.allMealPerDiems().stream())
                .collect(Collectors.toList()));
    }

    public LodgingPerDiems lodgingPerDiems() {
        return new LodgingPerDiems(destinations().stream()
                .map(Destination::lodgingPerDiems)
                .flatMap(l -> l.allLodgingPerDiems().stream())
                .collect(Collectors.toList()));
    }

    public MileagePerDiems mileagePerDiems() {
        return new MileagePerDiems(getAllLegs());
    }

    public Address origin() {
        if (getOutboundLegs().size() > 0) {
            return getOutboundLegs().get(0).fromAddress();
        }
        return null;
    }

    /**
     * @return A list of destinations the employee is visiting on the outgoing portion of their trip.
     */
    public List<Destination> destinations() {
        return getOutboundLegs().stream()
                .map(Leg::to)
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
                .map(Leg::travelDate)
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
                .map(Leg::travelDate)
                .max(LocalDate::compareTo)
                .get();
    }

    public ImmutableList<Leg> getOutboundLegs() {
        return outboundLegs;
    }

    public ImmutableList<Leg> getReturnLegs() {
        return returnLegs;
    }

    public ImmutableList<Leg> getAllLegs() {
        return Stream.concat(getOutboundLegs().stream(), getReturnLegs().stream())
                .collect(ImmutableList.toImmutableList());
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
