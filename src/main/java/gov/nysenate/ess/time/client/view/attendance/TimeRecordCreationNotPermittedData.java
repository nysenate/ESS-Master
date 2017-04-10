package gov.nysenate.ess.time.client.view.attendance;

import gov.nysenate.ess.core.client.view.PayPeriodView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordCreationNotPermittedEx;

public class TimeRecordCreationNotPermittedData implements ViewObject {

    private int empId;
    private PayPeriodView payPeriod;
    private String reason;

    public TimeRecordCreationNotPermittedData(TimeRecordCreationNotPermittedEx ex) {
        this.empId = ex.getEmpId();
        this.payPeriod = new PayPeriodView(ex.getPayPeriod());
        this.reason = ex.getReason();
    }

    @Override
    public String getViewType() {
        return "time-record-creation-not-permitted";
    }

    public int getEmpId() {
        return empId;
    }

    public PayPeriodView getPayPeriod() {
        return payPeriod;
    }

    public String getReason() {
        return reason;
    }
}
