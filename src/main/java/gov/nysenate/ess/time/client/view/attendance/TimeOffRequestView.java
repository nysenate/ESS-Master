package gov.nysenate.ess.time.client.view.attendance;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.model.attendance.TimeOffStatus;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class TimeOffRequestView implements ViewObject {

    protected List<TimeOffRequestDayView> days;
    protected List<TimeOffRequestCommentView> comments;
    protected Integer requestId;
    protected int employeeId;
    protected int supervisorId;
    protected String status;
    protected LocalDateTime timestamp;
    protected LocalDate startDate;
    protected LocalDate endDate;

    public TimeOffRequestView() {}

    public TimeOffRequestView(TimeOffRequest request) {
        this.days = request.getDays()
                           .stream()
                           .map(TimeOffRequestDayView::new)
                           .collect(Collectors.toList());
        this.comments = request.getComments()
                               .stream()
                               .map(TimeOffRequestCommentView::new)
                               .collect(Collectors.toList());
        this.status = request.getStatus().getName();
        this.employeeId = request.getEmployeeId();
        this.supervisorId = request.getSupervisorId();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.requestId = request.getRequestId();
        this.timestamp = request.getTimestamp();
    }


    public TimeOffRequest toTimeOffRequest() {
        TimeOffRequest request = new TimeOffRequest();
        request.setRequestId(requestId != null ? requestId : -1);
        request.setEmployeeId(employeeId);
        request.setSupervisorId(supervisorId);
        request.setStatus(TimeOffStatus.valueOf(status));
        request.setTimestamp(timestamp);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setDays(days.stream()
                        .map(TimeOffRequestDayView::toTimeOffRequestDay)
                        .collect(Collectors.toList()));
        request.setComments(comments.stream()
                        .map(TimeOffRequestCommentView::toTimeOffRequestComment)
                        .collect(Collectors.toList()));
        return request;
    }

    public List<TimeOffRequestDayView> getDays() {
        return days;
    }

    public List<TimeOffRequestCommentView> getComments() {
        return comments;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String getViewType() {
        return "time-off-request";
    }
}
