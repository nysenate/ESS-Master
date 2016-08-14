package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.service.attendance.TimeRecordNotFoundEx;

import java.time.LocalDate;

public class TimeRecordNotFoundData implements ViewObject {

    private Integer empId;
    private LocalDate beginDate;

    public TimeRecordNotFoundData(Integer empId, LocalDate beginDate) {
        this.empId = empId;
        this.beginDate = beginDate;
    }

    public TimeRecordNotFoundData(TimeRecordNotFoundEx ex) {
        this(ex.getEmpId(), ex.getBeginDate());
    }

    @Override
    public String getViewType() {
        return "time-record-not-found-data";
    }

    public Integer getEmpId() {
        return empId;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }
}
