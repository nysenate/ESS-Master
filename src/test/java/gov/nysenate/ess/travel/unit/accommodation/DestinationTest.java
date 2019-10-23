package gov.nysenate.ess.travel.unit.accommodation;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.destination.Destination;
import gov.nysenate.ess.travel.accommodation.Day;
import gov.nysenate.ess.travel.accommodation.Night;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.fixtures.AccommodationFixture;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@UnitTest
public class DestinationTest {

//    @Test
//    public void calculatesAllowances() {
//        Destination a = AccommodationFixture.twoDayOneNightAccommodation();
//        assertEquals(new Dollars("102.00"), a.mealAllowance());
//        assertEquals(new Dollars("120.00"), a.lodgingAllowance());
//    }
//
//    @Test
//    public void usersCanRequestNoAllowances() {
//        Day dayOne = new Day(AccommodationFixture.MONDAY, AccommodationFixture.TIER, false);
//        Day dayTwo = new Day(AccommodationFixture.TUESDAY, AccommodationFixture.TIER, false);
//        ImmutableSet<Day> days = ImmutableSet.of(dayOne, dayTwo);
//
//        Night night = new Night(AccommodationFixture.TUESDAY, AccommodationFixture.LODGING_RATE, false);
//        ImmutableSet<Night> nights = ImmutableSet.of(night);
//
//        Destination a = new Destination(new Address(), days, nights);
//        assertEquals(new Dollars("0"), a.mealAllowance());
//        assertEquals(new Dollars("0"), a.lodgingAllowance());
//    }
//
//    @Test
//    public void arrivalDate() {
//        Destination a = AccommodationFixture.twoDayOneNightAccommodation();
//        assertEquals(LocalDate.of(2018, 1, 1), a.arrivalDate());
//    }
//
//    @Test
//    public void departureDate() {
//        Destination a = AccommodationFixture.twoDayOneNightAccommodation();
//        assertEquals(LocalDate.of(2018, 1, 2), a.departureDate());
//    }
}
