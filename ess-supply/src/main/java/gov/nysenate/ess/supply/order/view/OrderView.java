package gov.nysenate.ess.supply.order.view;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class OrderView implements ViewObject {

    protected int id;
    protected EmployeeView customer;
    protected LocalDateTime orderDateTime;
    protected LocationView location;
    protected EmployeeView issuingEmployee;
    protected LocalDateTime processedDateTime;
    protected LocalDateTime completedDateTime;
    protected String status;
    protected LineItemView[] items;

    public OrderView() {

    }

    public OrderView(Order order) {
        this.id = order.getId();
        this.customer = new EmployeeView(order.getCustomer());
        this.orderDateTime = order.getOrderDateTime();
        this.location = new LocationView(order.getLocation());
        this.issuingEmployee = new EmployeeView(order.getIssuingEmployee());
        this.processedDateTime = order.getProcessedDateTime();
        this.completedDateTime = order.getCompletedDateTime();
        this.status = order.getStatus().toString();
        this.items = order.getItems().stream().map(LineItemView::new).collect(Collectors.toList()).toArray(new LineItemView[order.getItems().size()]);
    }

    public Order toOrder() {
        Order.Builder builder = new Order.Builder(id, customer.toEmployee(), orderDateTime, location.toLocation(), OrderStatus.valueOf(status))
                .processedDateTime(processedDateTime)
                .completedDateTime(completedDateTime)
                .items(Arrays.asList(items).stream().map(LineItemView::toLineItem).collect(Collectors.toSet()));

        // Cant construct issuing employee if there is not one.
        if (issuingEmployee.getEmployeeId() != 0) {
            builder.issuingEmployee(issuingEmployee.toEmployee());
        }
        return builder.build();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmployeeView getCustomer() {
        return customer;
    }

    public void setCustomer(EmployeeView customer) {
        this.customer = customer;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public LocationView getLocation() {
        return location;
    }

    public void setLocation(LocationView location) {
        this.location = location;
    }

    public EmployeeView getIssuingEmployee() {
        return issuingEmployee;
    }

    public void setIssuingEmployee(EmployeeView issuingEmployee) {
        this.issuingEmployee = issuingEmployee;
    }

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(LocalDateTime processedDateTime) {
        this.processedDateTime = processedDateTime;
    }

    public LocalDateTime getCompletedDateTime() {
        return completedDateTime;
    }

    public void setCompletedDateTime(LocalDateTime completedDateTime) {
        this.completedDateTime = completedDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LineItemView[] getItems() {
        return items;
    }

    public void setItems(LineItemView[] items) {
        this.items = items;
    }

    @Override
    public String getViewType() {
        return null;
    }
}