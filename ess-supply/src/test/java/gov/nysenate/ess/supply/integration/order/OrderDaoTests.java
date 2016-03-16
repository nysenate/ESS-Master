package gov.nysenate.ess.supply.integration.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderHistory;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@Transactional
@TransactionConfiguration(transactionManager = "remoteTxManager", defaultRollback = true)
public class OrderDaoTests extends SupplyTests {

    @Autowired private OrderDao orderDao;
    @Autowired private SupplyItemDao itemDao;

    private Order order;
    private OrderVersion firstVersion;
    private LocalDateTime insertedDateTime;

    @Before
    public void setup() {
        Employee customer = new Employee();
        customer.setEmployeeId(1);
        Location destination = new Location();
        destination.setCode("A42FB");
        destination.setType(LocationType.valueOfCode('W'));
        Set<LineItem> lineItems = new HashSet<>();
        lineItems.add(new LineItem(itemDao.getItemById(1), 3));
        lineItems.add(new LineItem(itemDao.getItemById(2), 3));

        firstVersion = new OrderVersion.Builder()
                .withCustomer(customer)
                .withDestination(destination)
                .withLineItems(lineItems)
                .withStatus(OrderStatus.APPROVED)
                .withModifiedBy(customer)
                .build();

        insertedDateTime = LocalDateTime.now();
        order = Order.of(1, OrderHistory.of(insertedDateTime, firstVersion));
    }

    @Test
    public void insertingNewVersionReturnsOrderId() {
        int orderId = orderDao.insertOrder(firstVersion, insertedDateTime);
        assertTrue(orderId > 0);
    }

    @Ignore
    @Test
    public void canGetOrderById() {
        int orderId = orderDao.insertOrder(firstVersion, insertedDateTime);
        Order actualOrder = orderDao.getOrderById(orderId);
        assertEquals(order.getHistory(), actualOrder.getHistory());
    }
}
