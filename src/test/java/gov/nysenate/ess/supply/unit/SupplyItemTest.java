package gov.nysenate.ess.supply.unit;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.item.model.*;
import gov.nysenate.ess.supply.item.model.ItemRestriction;
import gov.nysenate.ess.supply.unit.fixtures.SupplyItemFixture;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@org.junit.experimental.categories.Category(UnitTest.class)
public class SupplyItemTest {

    private SupplyItem.Builder builder;

    @Before
    public void setup() {
        builder = SupplyItemFixture.getDefaultBuilder();
    }

    @Test
    public void itemNotOrderedBySupply_DoesNotRequireSynchronization() {
        SupplyItem item = builder.withStatus(new ItemStatus(true, false, true, false)).build();
        assertSynchronizationNotRequired(item);

        item = builder.withStatus(new ItemStatus(false, false, true, false)).build();
        assertSynchronizationNotRequired(item);
    }

    @Test
    public void nonExpendableItem_DoesNotRequireSynchronization() {
        SupplyItem item = builder.withStatus(new ItemStatus(false, true, true, false)).build();
        assertSynchronizationNotRequired(item);

        item = builder.withStatus(new ItemStatus(false, false, true, false)).build();
        assertSynchronizationNotRequired(item);
    }

    @Test
    public void expendableItemsOrderedBySupply_RequireSynchronization() {
        SupplyItem item = builder.withStatus(new ItemStatus(true, true, true, false)).build();
        assertSynchronizationRequired(item);
    }

    @Test
    public void cannotSetItemRestrictionToNull() {
        SupplyItem item = builder.build();
        item.setRestriction(null);
        assertFalse(item.isRestricted());
    }

    @Test
    public void givenEmptyRestriction_notRestricted() {
        SupplyItem item = builder.build();
        item.setRestriction(new ItemRestriction(null));
        assertFalse(item.isRestricted());
    }

    @Test
    public void givenRestrictedLocations_itemIsRestricted() {
        SupplyItem item = builder.build();
        item.setRestriction(new ItemRestriction(ImmutableSet.of(new LocationId("A42FB-W"))));
        assertTrue(item.isRestricted());
    }

    private void assertSynchronizationNotRequired(SupplyItem item) {
        assertThat(item.requiresSynchronization(), is(false));
    }

    private void assertSynchronizationRequired(SupplyItem item) {
        assertThat(item.requiresSynchronization(), is(true));
    }

}
