package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionStatus;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;

public interface RequisitionService {

    /**
     * Handles events for, and saves, a new requisition order.
     * @param requisition The new requisition
     * @return The saved requisition with its requisitionId set.
     */
    Requisition submitRequisition(Requisition requisition);

    Requisition saveRequisition(Requisition requisition);

    Requisition processRequisition(Requisition requisition);

    Requisition rejectRequisition(Requisition requisition);

    Optional<Requisition> getRequisitionById(int requisitionId);

    PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, String savedInSfms, LimitOffset limitOffset, String issuerID);
    /**
     * Search a users order history.
     * Order history consists of all of a users orders plus all other orders with destination equal to the users work location.
     *
     * @param destination String representing the location id of a destination. E.g. "A42FB-W"
     * @param customerId Users employee id.
     * @param statuses {@link RequisitionStatus statuses} to include in results.
     * @param dateRange Date range to search within.
     * @param dateField
     * @param limitOffset
     * @return
     */
    PaginatedList<Requisition> searchOrderHistory(String destination, int customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset);

    ImmutableList<Requisition> getRequisitionHistory(int requisitionId);

    /**
     * Marks a requisition as being saved in sfms.
     */
    void savedInSfms(int requisitionId, boolean succeed);
}
