package gov.nysenate.ess.supply.requisition.dao;

import gov.nysenate.ess.supply.requisition.Requisition;

public interface RequisitionDao {

    void insertRequisition(Requisition requisition);

    void saveRequisition(Requisition requisition);

    Requisition getRequisition(int requisitionId);
}
