package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.supply.order.Order;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.TreeMap;

@Repository
public class InMemoryOrder implements OrderDao {

    private Map<Integer, Order> orders = new TreeMap<>();

    @Override
    public int getUniqueId() {
        return orders.size() + 1;
    }

    @Override
    public void saveOrder(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Order getOrderById(int orderId) {
        return orders.get(orderId);
    }
}
