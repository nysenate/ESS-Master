package gov.nysenate.ess.supply.unit;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.item.OrderableItems;
import gov.nysenate.ess.supply.item.model.ItemRestriction;
import gov.nysenate.ess.supply.item.model.ItemStatus;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.unit.fixtures.SupplyItemFixture;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@org.junit.experimental.categories.Category(UnitTest.class)
public class OrderableItemsTest {


    @Test
    public void givenNullItemsList_returnEmptyList() {
        assertTrue(OrderableItems.forItems(null).isEmpty());
        assertTrue(OrderableItems.forItemsAndLoc(null, new LocationId("A42FB-W")).isEmpty());
    }

    /**
     * --- forItems tests ---
     */

    @Test
    public void givenEmptyList_returnEmptyList() {
        assertTrue(OrderableItems.forItems(new ArrayList<>()).isEmpty());
    }

    @Test
    public void nonExpendableItems_notOrderable() {
        ImmutableSet<SupplyItem> items = ImmutableSet.of(SupplyItemFixture.getDefaultBuilder().withStatus(new ItemStatus(false, true, true, false)).build());
        assertTrue(OrderableItems.forItems(items).isEmpty());
    }

    @Test
    public void visibleItems_areOrderable() {
        ImmutableSet<SupplyItem> items = ImmutableSet.of(SupplyItemFixture.getDefaultBuilder().build());
        assertEquals(items, OrderableItems.forItems(items));
    }

    @Test
    public void hiddenItems_notOrderable() {
        ImmutableSet<SupplyItem> items = ImmutableSet.of(SupplyItemFixture.getDefaultBuilder().withStatus(new ItemStatus(true, true, false, false)).build());
        assertTrue(OrderableItems.forItems(items).isEmpty());
    }

    /**
     * --- forItemsAndLoc Tests ---
     */

    @Test
    public void givenNullItemsAndLoc_returnEmptyList() {
        assertTrue(OrderableItems.forItemsAndLoc(null, null).isEmpty());
    }

    @Test
    public void givenNullLocationAndHiddenItems_returnEmptyList() {
        ImmutableSet<SupplyItem> items = ImmutableSet.of(SupplyItemFixture.getDefaultBuilder().withStatus(new ItemStatus(true, true, false, false)).build());
        assertTrue(OrderableItems.forItemsAndLoc(items, null).isEmpty());
    }

    @Test
    public void givenNullLocationAndRestrictedItems_returnList() {
        SupplyItem item = SupplyItemFixture.getDefaultBuilder().build();
        item.setRestriction(new ItemRestriction(ImmutableSet.of(new LocationId("A42FB-W"))));
        ImmutableSet<SupplyItem> items = ImmutableSet.of(item);
        assertFalse(OrderableItems.forItemsAndLoc(items, null).isEmpty());
    }

    @Test
    public void filterOutRestrictedItems() {
        SupplyItem item = SupplyItemFixture.getDefaultBuilder().build();
        item.setRestriction(new ItemRestriction(ImmutableSet.of(new LocationId("A42FB-W"))));
        ImmutableSet<SupplyItem> items = ImmutableSet.of(item);
        assertTrue(OrderableItems.forItemsAndLoc(items, new LocationId("D5001-W")).isEmpty());
    }

    @Test
    public void restrictedItemCanBeOrderedFromAllowedLocation() {
        SupplyItem item = SupplyItemFixture.getDefaultBuilder().build();
        item.setRestriction(new ItemRestriction(ImmutableSet.of(new LocationId("A42FB-W"))));
        ImmutableSet<SupplyItem> items = ImmutableSet.of(item);
        assertFalse(OrderableItems.forItemsAndLoc(items, new LocationId("A42FB-W")).isEmpty());
    }
}
