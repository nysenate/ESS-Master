package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverrides;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TravelApplication {

    /** Sort amendments by the declaration order of {@link Version} */
    private final static Comparator<Amendment> amendmentComparator = Comparator.comparing(Amendment::version);

    protected int appId;
    protected Employee traveler;
    protected SortedSet<Amendment> amendments;

    public TravelApplication(Employee traveler) {
        this(0, traveler);
    }

    public TravelApplication(int id, Employee traveler) {
        this.appId = id;
        this.traveler = Objects.requireNonNull(traveler, "Travel Application requires a non null traveler.");
        this.amendments = new TreeSet<>(amendmentComparator);
    }

    // The active amendment is the most recent approved amendment
    // or, if none are approved, the most recent amendment.
    public Amendment activeAmendment() {
//        Optional<Amendment> approvedAmd = amendments.stream()
//                .filter(a -> a.status.isApproved())
//                .reduce((first, second) -> second);
//        return approvedAmd.orElse(amendments.last());
        return amendments.last(); // FIXME for first implementation, just return most recent amendment.
    }

    /**
     * Amends this travel application with a new amendment.
     *
     * Sets the Version of the given amendment to the next version
     * and adds to the list of amendments.
     * @param a
     * @return
     */
    public Amendment amend(Amendment a) {
        // todo ensure amendment appId is correct??
        a.version = amendments.last().version.next();
        amendments.add(a);
        return a;
    }

    public Dollars mileageAllowance() {
        return activeAmendment().perDiemOverrides.isMileageOverridden()
                ? activeAmendment().perDiemOverrides.mileageOverride()
                : getRoute().mileagePerDiems().requestedPerDiem();
    }

    public Dollars mealAllowance() {
        return activeAmendment().perDiemOverrides.isMealsOverridden()
                ? activeAmendment().perDiemOverrides.mealsOverride()
                : getRoute().mealPerDiems().requestedPerDiem();
    }

    public Dollars lodgingAllowance() {
        return activeAmendment().perDiemOverrides.isLodgingOverridden()
                ? activeAmendment().perDiemOverrides.lodgingOverride()
                : getRoute().lodgingPerDiems().requestedPerDiem();
    }

    public Dollars tollsAllowance() {
        return getAllowances().tolls();
    }

    public Dollars parkingAllowance() {
        return getAllowances().parking();
    }

    public Dollars trainAndPlaneAllowance() {
        return getAllowances().trainAndPlane();
    }

    public Dollars alternateTransportationAllowance() {
        return getAllowances().alternateTransportation();
    }

    public Dollars registrationAllowance() {
        return getAllowances().registration();
    }

    /**
     * Total transportation allowance.
     * Used as a field on the print form.
     */
    public Dollars transportationAllowance() {
        return mileageAllowance().add(trainAndPlaneAllowance());
    }

    /**
     * Sum of tolls and parking allowances.
     * Used as a field on the print form.
     */
    public Dollars tollsAndParkingAllowance() {
        return tollsAllowance().add(parkingAllowance());
    }

    /**
     * Total allowance for this travel application.
     */
    public Dollars totalAllowance() {
        return mileageAllowance()
                .add(mealAllowance())
                .add(lodgingAllowance())
                .add(tollsAllowance())
                .add(parkingAllowance())
                .add(trainAndPlaneAllowance())
                .add(alternateTransportationAllowance())
                .add(registrationAllowance());
    }

    /**
     * @return The travel start date or {@code null} if no destinations.
     */
    public LocalDate startDate() {
        return getRoute().startDate();
    }

    /**
     * @return The travel end date or {@code null} if no destinations.
     */
    public LocalDate endDate() {
        return getRoute().endDate();
    }

    public TravelApplicationStatus status() {
        return activeAmendment().status;
    }

    void setStatus(TravelApplicationStatus status) {
        activeAmendment().status = status;
    }

    // TODO move to amendment
    public void approve() {
        activeAmendment().status.approve();
    }

    // TODO move to amendment
    public void disapprove(String notes) {
        activeAmendment().status.disapprove(notes);
    }

    public int getAppId() {
        return appId;
    }

    public int getVersionId() {
        return activeAmendment().version.ordinal();
    }

    public Employee getTraveler() {
        return traveler;
    }

    public String getPurposeOfTravel() {
        return activeAmendment().purposeOfTravel;
    }

    public void setPurposeOfTravel(String purposeOfTravel) {
        this.activeAmendment().purposeOfTravel = purposeOfTravel;
    }

    public Route getRoute() {
        return activeAmendment().route;
    }

    public void setRoute(Route route) {
        this.activeAmendment().route = route;
    }

    public Allowances getAllowances() {
        return activeAmendment().allowances;
    }

    public void setAllowances(Allowances allowances) {
        this.activeAmendment().allowances = allowances;
    }

    public PerDiemOverrides getPerDiemOverrides() {
        return activeAmendment().perDiemOverrides;
    }

    public void setPerDiemOverrides(PerDiemOverrides perDiemOverrides) {
        this.activeAmendment().perDiemOverrides = perDiemOverrides;
    }

    public LocalDateTime getSubmittedDateTime() {
        return amendments.first().createdDateTime;
    }

    void setSubmittedDateTime(LocalDateTime submittedDateTime) {
        // TODO Remove/fix this
        amendments.first().createdDateTime = submittedDateTime;
    }

    public LocalDateTime getModifiedDateTime() {
        return amendments.last().createdDateTime;
    }

    void setModifiedDateTime(LocalDateTime modifiedDateTime) {
        // TODO Fix
        amendments.last().createdDateTime = modifiedDateTime;
    }

    public Employee getModifiedBy() {
        return amendments.last().createdBy;
    }

    void setModifiedBy(Employee modifiedBy) {
        // TODO Fix
        amendments.last().createdBy = modifiedBy;
    }

    public List<TravelAttachment> getAttachments() {
        return activeAmendment().attachments;
    }

    void setAttachments(List<TravelAttachment> attachments) {
        // TODO Fix
        activeAmendment().attachments = attachments;
    }

    void addAttachments(List<TravelAttachment> attachments) {
        getAttachments().addAll(attachments);
    }

    void deleteAttachment(String attachmentId) {
        getAttachments().removeIf(a -> a.getId().equals(attachmentId));
    }
}
