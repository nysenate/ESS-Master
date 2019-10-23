package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.model.unit.Address;

import java.time.LocalDate;
import java.util.Objects;

public class Leg {

    private final Address from;
    private final Address to;
    private final ModeOfTransportation modeOfTransportation;
    private final LocalDate travelDate;

    public Leg(Address from, Address to, ModeOfTransportation modeOfTransportation, LocalDate travelDate) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.modeOfTransportation = Objects.requireNonNull(modeOfTransportation);
        this.travelDate = Objects.requireNonNull(travelDate);
    }

    public Address getFrom() {
        return from;
    }

    public Address getTo() {
        return to;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    @Override
    public String toString() {
        return "Leg{" +
                "from=" + from +
                ", to=" + to +
                ", modeOfTransportation=" + modeOfTransportation +
                ", travelDate=" + travelDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leg leg = (Leg) o;
        return Objects.equals(from, leg.from) &&
                Objects.equals(to, leg.to) &&
                Objects.equals(modeOfTransportation, leg.modeOfTransportation) &&
                Objects.equals(travelDate, leg.travelDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, modeOfTransportation, travelDate);
    }
}
