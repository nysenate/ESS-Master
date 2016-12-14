package gov.nysenate.ess.supply.unit;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.supply.item.model.*;
import gov.nysenate.ess.supply.unit.fixtures.SupplyItemFixture;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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

    private void assertSynchronizationNotRequired(SupplyItem item) {
        assertThat(item.requiresSynchronization(), is(false));
    }

    private void assertSynchronizationRequired(SupplyItem item) {
        assertThat(item.requiresSynchronization(), is(true));
    }

}
