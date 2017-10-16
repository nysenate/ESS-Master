package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TransportationAllowance;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.travelallowance.TravelAllowanceService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class TravelAllowanceServiceTest extends BaseTest{
    @Autowired
    TravelAllowanceService travelAllowanceService;

    @Test
    public void lessThan35milesTotal_allowanceEquals0() {
        ArrayList<TravelDestination> dests = new ArrayList<>();
        dests.add(new TravelDestination(LocalDate.now(), LocalDate.now(),
                new Address("515 Loudon Road", "Loudonville", "NY", "12211")));
        Itinerary itinerary = new Itinerary(new Address("100 South Swan Street", "Albany", "NY", "12210"), dests);

        TransportationAllowance ta = travelAllowanceService.updateTravelAllowance(itinerary);
        assertEquals(ta.getMileage().toString(), "0.00");
    }

    @Test
    public void lessThan35milesOneDirection_allowanceEquals0() {
        ArrayList<TravelDestination> dests = new ArrayList<>();
        dests.add(new TravelDestination(LocalDate.now(), LocalDate.now(),
                new Address("Schenectady County Airport, 21 Airport Rd, Scotia, NY 12302")));
        Itinerary itinerary = new Itinerary(new Address("100 South Swan Street", "Albany", "NY", "12210"), dests);

        TransportationAllowance ta = travelAllowanceService.updateTravelAllowance(itinerary);
        assertEquals(ta.getMileage().toString(), "0.00");
    }

    @Test
    public void moreThan35miles_giveFullReimbursement() {
        ArrayList<TravelDestination> dests = new ArrayList<>();
        dests.add(new TravelDestination(LocalDate.now(), LocalDate.now(),
                new Address("181 Fort Edward Road", "Fort Edward", "NY", "12828")));
        Itinerary itinerary = new Itinerary(new Address("100 South Swan Street", "Albany", "NY", "12210"), dests);

        TransportationAllowance ta = travelAllowanceService.updateTravelAllowance(itinerary);
        assertEquals(ta.getMileage().toString(), "56.12");
    }

    @Test
    public void moreThan35milesMultiLegged_giveFullReimbursement() {
        ArrayList<TravelDestination> dests = new ArrayList<>();
        dests.add(new TravelDestination(LocalDate.now(), LocalDate.now(),
                new Address("181 Fort Edward Road", "Fort Edward", "NY", "12828")));
        dests.add(new TravelDestination(LocalDate.now(), LocalDate.now(),
                new Address("Martha's Dandee Creme, 1133 U.S. 9, Queensbury, NY 12804")));
        Itinerary itinerary = new Itinerary(new Address("100 South Swan Street", "Albany", "NY", "12210"), dests);

        TransportationAllowance ta = travelAllowanceService.updateTravelAllowance(itinerary);
        assertEquals(ta.getMileage().toString(), "61.90");
    }

}
