package gov.nysenate.ess.supply.requisition.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;

import java.time.LocalDateTime;
import java.util.EnumSet;

public interface RequisitionDao {

    Requisition saveRequisition(Requisition requisition);

    Requisition getRequisitionById(int requisitionId);

    PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset);

    /**
     * Searches the order history for a employee.
     * An employees order history includes all orders for his work location plus all orders they have made themselves
     * to any other locations.
     * This is similar to the searchRequisitions method except it requires a destinationId and customerId and
     * should only be used to get an employees order history.
     */
    PaginatedList<Requisition> searchOrderHistory(String destinationId, int customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset);
}
