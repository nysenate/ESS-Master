package gov.nysenate.ess.travel.application;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.MileageAllowances;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowances;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowances;
import gov.nysenate.ess.travel.application.destination.Destinations;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.route.Route;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TravelApplication {

    private long id;
    private Employee traveler;
    private Employee submitter;
    private String purposeOfTravel;
    private Route route;
    private Destinations destinations;
    private MileageAllowances mileageAllowances;
    private MealAllowances mealAllowances;
    private LodgingAllowances lodgingAllowances;
    private Dollars tolls;
    private Dollars parking;
    private Dollars alternate; // Bus, subway, and train.
    private Dollars registration;
    private LocalDateTime submittedDateTime; // DateTime application was submitted for approval.
    private List<TravelAttachment> attachments;

    public TravelApplication(long id, Employee traveler, Employee submitter) {
        this.id = id;
        this.traveler = Objects.requireNonNull(traveler, "Travel Application requires non null traveler.");
        this.submitter = Objects.requireNonNull(submitter, "Travel Application requires non null submitter.");
        this.purposeOfTravel = "";
        this.route = Route.EMPTY_ROUTE;
        this.destinations = new Destinations(ImmutableList.of());
        this.mileageAllowances = new MileageAllowances(Lists.newArrayList(), Lists.newArrayList());
        this.mealAllowances = new MealAllowances(Lists.newArrayList());
        this.lodgingAllowances = new LodgingAllowances(Lists.newArrayList());
        this.tolls = Dollars.ZERO;
        this.parking = Dollars.ZERO;
        this.alternate = Dollars.ZERO;
        this.registration = Dollars.ZERO;
        this.attachments = new ArrayList<>();
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

    /**
     * Get the total lodging allowance for all Accommodations in this application.
     * @return
     */
    public Dollars lodgingAllowance() {
        return getLodgingAllowances().totalAllowance();
    }

    /**
     * Get the total meal allowance for all accommodations in this application.
     * @return
     */
    public Dollars mealAllowance() {
        return getMealAllowances().totalAllowance();
    }

    /**
     * Get the total mileage allowance.
     * @return
     */
    public Dollars mileageAllowance() {
        return getMileageAllowances().totalAllowance();
    }

    /**
     * Get the total allowance for this application.
     * @return
     */
    public Dollars totalAllowance() {
        return lodgingAllowance()
                .add(mealAllowance())
                .add(mileageAllowance())
                .add(getTolls())
                .add(getParking())
                .add(getAlternate())
                .add(getRegistration());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Employee getTraveler() {
        return traveler;
    }

    public Employee getSubmitter() {
        return submitter;
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public void setPurposeOfTravel(String purposeOfTravel) {
        this.purposeOfTravel = purposeOfTravel;
    }

    public Route getRoute() {
        return route;
    }

    protected void setRoute(Route route) {
        this.route = route;
    }

    public Destinations getDestinations() {
        return destinations;
    }

    protected void setDestinations(Destinations destinations) {
        this.destinations = destinations;
    }

    public MileageAllowances getMileageAllowances() {
        return mileageAllowances;
    }

    protected void setMileageAllowances(MileageAllowances mileageAllowances) {
        this.mileageAllowances = mileageAllowances;
    }

    public MealAllowances getMealAllowances() {
        return mealAllowances;
    }

    protected void setMealAllowances(MealAllowances mealAllowances) {
        this.mealAllowances = mealAllowances;
    }

    public LodgingAllowances getLodgingAllowances() {
        return lodgingAllowances;
    }

    protected void setLodgingAllowances(LodgingAllowances lodgingAllowances) {
        this.lodgingAllowances = lodgingAllowances;
    }

    public Dollars getTolls() {
        return tolls;
    }

    public void setTolls(Dollars tolls) {
        this.tolls = tolls;
    }

    public Dollars getParking() {
        return parking;
    }

    public void setParking(Dollars parking) {
        this.parking = parking;
    }

    public Dollars getAlternate() {
        return alternate;
    }

    public void setAlternate(Dollars alternate) {
        this.alternate = alternate;
    }

    public Dollars getRegistration() {
        return registration;
    }

    public void setRegistration(Dollars registration) {
        this.registration = registration;
    }

    public LocalDateTime getSubmittedDateTime() {
        return submittedDateTime;
    }

    public void setSubmittedDateTime(LocalDateTime submittedDateTime) {
        this.submittedDateTime = submittedDateTime;
    }

    public List<TravelAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<TravelAttachment> attachments) {
        this.attachments = attachments;
    }

    public void addAttachments(List<TravelAttachment> attachments) {
        getAttachments().addAll(attachments);
    }

    public void deleteAttachment(String attachmentId) {
        getAttachments().removeIf(a -> a.getId().equals(attachmentId));
    }
}
