package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverrides;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A set of changes to a Application.
 */
public class Amendment {

    private int amendmentId; // 0 if this amendment has not been saved to the database.
    private Version version;
    private PurposeOfTravel purposeOfTravel;
    private Route route;
    private Allowances allowances;
    private PerDiemOverrides perDiemOverrides;
    private TravelApplicationStatus status;
    private List<TravelAttachment> attachments;
    private LocalDateTime createdDateTime;
    private Employee createdBy;
    private MealPerDiems mealPerDiems;
    private LodgingPerDiems lodgingPerDiems;

    public Amendment(Builder builder) {
        amendmentId = builder.amendmentId;
        version = builder.version;
        purposeOfTravel = builder.purposeOfTravel;
        route = builder.route;
        allowances = builder.allowances;
        perDiemOverrides = builder.perDiemOverrides;
        mealPerDiems = builder.mealPerDiems;
        lodgingPerDiems = builder.lodgingPerDiems;
        status = builder.status;
        attachments = builder.attachments;
        createdDateTime = builder.createdDateTime;
        createdBy = builder.createdBy;
    }

    public void approve() {
        status().approve();
    }

    public void disapprove(String reason) {
        status().disapprove(reason);
    }

    public Dollars mileageAllowance() {
        return perDiemOverrides().isMileageOverridden()
                ? perDiemOverrides().mileageOverride()
                : route().mileagePerDiems().requestedPerDiem();
    }

    public Dollars mealAllowance() {
        return perDiemOverrides().isMealsOverridden()
                ? perDiemOverrides().mealsOverride()
                : mealPerDiems.requestedPerDiem();
    }

    public Dollars lodgingAllowance() {
        return perDiemOverrides().isLodgingOverridden()
                ? perDiemOverrides().lodgingOverride()
                : lodgingPerDiems.requestedPerDiem();
    }

    public Dollars tollsAllowance() {
        return allowances().tolls();
    }

    public Dollars parkingAllowance() {
        return allowances().parking();
    }

    public Dollars trainAndPlaneAllowance() {
        return allowances().trainAndPlane();
    }

    public Dollars alternateTransportationAllowance() {
        return allowances().alternateTransportation();
    }

    public Dollars registrationAllowance() {
        return allowances().registration();
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
        return route().startDate();
    }

    /**
     * @return The travel end date or {@code null} if no destinations.
     */
    public LocalDate endDate() {
        return route().endDate();
    }

    public int amendmentId() {
        return amendmentId;
    }

    public Version version() {
        return version;
    }

    public PurposeOfTravel purposeOfTravel() {
        return purposeOfTravel;
    }

    public void setPurposeOfTravel(PurposeOfTravel purposeOfTravel) {
        this.purposeOfTravel = purposeOfTravel;
    }

    public Route route() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
        updateMealPerDiems();
        updateLodgingPerDiems();
    }

    public MealPerDiems mealPerDiems() {
        return this.mealPerDiems;
    }

    public void setMealPerDiems(MealPerDiems mpds) {
        this.mealPerDiems = mpds;
    }

    private void updateMealPerDiems() {
        Set<MealPerDiem> mealPerDiemSet = new HashSet<>();
        for (Destination d : route().destinations()) {
            for (PerDiem pd : d.mealPerDiems()) {
                mealPerDiemSet.add(new MealPerDiem(d.getAddress(), pd));
            }
        }
        this.mealPerDiems = new MealPerDiems(mealPerDiemSet);
    }

    public LodgingPerDiems lodgingPerDiems() {
        return this.lodgingPerDiems;
    }

    public void setLodingPerDiems(LodgingPerDiems lpds) {
        this.lodgingPerDiems = lpds;
    }

    private void updateLodgingPerDiems() {
        Set<LodgingPerDiem> lodgingPerDiemSet = new HashSet<>();
        for (Destination d : route().destinations()) {
            for (PerDiem pd : d.lodgingPerDiems()) {
                lodgingPerDiemSet.add(new LodgingPerDiem(d.getAddress(), pd));
            }
        }
        this.lodgingPerDiems = new LodgingPerDiems(lodgingPerDiemSet);
    }

    protected Allowances allowances() {
        return allowances;
    }

    public void setAllowances(Allowances allowances) {
        this.allowances = allowances;
    }

    public PerDiemOverrides perDiemOverrides() {
        return perDiemOverrides;
    }

    public TravelApplicationStatus status() {
        return status;
    }

    public List<TravelAttachment> attachments() {
        return attachments;
    }

    public LocalDateTime createdDateTime() {
        return createdDateTime;
    }

    public Employee createdBy() {
        return createdBy;
    }

    public void setPerDiemOverrides(PerDiemOverrides perDiemOverrides) {
        this.perDiemOverrides = perDiemOverrides;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public void setAmendmentId(Integer amendmentId) {
        this.amendmentId = amendmentId;
    }


    /**
     * Amendment Builder
     */
    public static class Builder {
        private int amendmentId = 0;
        private Version version = Version.A;
        private PurposeOfTravel purposeOfTravel = null;
        private Route route = Route.EMPTY_ROUTE;
        private Allowances allowances = new Allowances();
        private PerDiemOverrides perDiemOverrides = new PerDiemOverrides();
        private MealPerDiems mealPerDiems = new MealPerDiems(new HashSet<>());
        private LodgingPerDiems lodgingPerDiems = new LodgingPerDiems(new HashSet<>());
        private TravelApplicationStatus status = new TravelApplicationStatus();
        private List<TravelAttachment> attachments = new ArrayList<>();
        private LocalDateTime createdDateTime;
        private Employee createdBy;

        public Builder() {
        }

        public Builder withAmendmentId(int id) {
            this.amendmentId = id;
            return this;
        }

        public Builder withVersion(Version version) {
            this.version = version;
            return this;
        }

        public Builder withPurposeOfTravel(PurposeOfTravel purposeOfTravel) {
            this.purposeOfTravel = purposeOfTravel;
            return this;
        }

        public Builder withRoute(Route route) {
            this.route = route;
            return this;
        }

        public Builder withAllowances(Allowances allowances) {
            this.allowances = allowances;
            return this;
        }

        public Builder withPerDiemOverrides(PerDiemOverrides perDiemOverrides) {
            this.perDiemOverrides = perDiemOverrides;
            return this;
        }

        public Builder withMealPerDiems(MealPerDiems mpds) {
            this.mealPerDiems = mpds;
            return this;
        }

        public Builder withLodgingPerDiems(LodgingPerDiems lpds) {
            this.lodgingPerDiems = lpds;
            return this;
        }

        public Builder withStatus(TravelApplicationStatus status) {
            this.status = status;
            return this;
        }

        public Builder withAttachments(List<TravelAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder withCreatedDateTime(LocalDateTime createdDateTime) {
            this.createdDateTime = createdDateTime;
            return this;
        }

        public Builder withCreatedBy(Employee createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Amendment build() {
            return new Amendment(this);
        }
    }
}
