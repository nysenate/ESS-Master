package gov.nysenate.ess.travel.unit.application.allowance;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.provider.gsa.meal.MealTier;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@UnitTest
public class MealTierTest {
    
    private static final UUID id = UUID.randomUUID();

    @Test
    public void senateRateIsTierTotal() {
        MealTier tier = new MealTier(id, "50", "10", "15", "20", "5");
        assertEquals(new Dollars("50.00"), tier.total());
    }

    @Test
    public void testComparable() {
        MealTier a = new MealTier(id, "50", "10", "15", "20", "5");
        MealTier b = new MealTier(id, "50", "10", "15", "20", "5");
        assertEquals(a.compareTo(b), 0);
        assertEquals(a, b);

        b = new MealTier(id, "60", "10", "15", "20", "15");
        assertNotEquals(a.compareTo(b), 0);
        assertNotEquals(a, b);

        b = new MealTier(id, "50", "10", "10", "20", "0");
        assertNotEquals(a.compareTo(b), 0);
        assertNotEquals(a, b);
    }
}
