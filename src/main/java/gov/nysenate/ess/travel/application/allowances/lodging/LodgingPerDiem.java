package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public final class LodgingPerDiem {

    private int id;
    private final TravelAddress address;
    private final PerDiem perDiem;
    private boolean isReimbursementRequested;

    public LodgingPerDiem(TravelAddress address, PerDiem perDiem) {
        this(0, address, perDiem, true);
    }

    public LodgingPerDiem(int id, TravelAddress address, PerDiem perDiem, boolean isReimbursementRequested) {
        this.id = id;
        this.address = address;
        this.perDiem = perDiem;
        this.isReimbursementRequested = isReimbursementRequested;
    }

    public int id() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Dollars maximumPerDiem() {
        return rate();
    }

    public Dollars requestedPerDiem() {
        return isReimbursementRequested() ? maximumPerDiem() : Dollars.ZERO;
    }

    public TravelAddress address() {
        return address;
    }

    public LocalDate date() {
        return perDiem.getDate();
    }

    public Dollars rate() {
        return new Dollars(perDiem.getRate());
    }

    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    @Override
    public String toString() {
        return "LodgingPerDiem{" +
                "address=" + address +
                ", perDiem=" + perDiem +
                ", isReimbursementRequested=" + isReimbursementRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LodgingPerDiem that = (LodgingPerDiem) o;
        return isReimbursementRequested == that.isReimbursementRequested &&
                Objects.equals(address, that.address) &&
                Objects.equals(perDiem, that.perDiem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, perDiem, isReimbursementRequested);
    }
}
