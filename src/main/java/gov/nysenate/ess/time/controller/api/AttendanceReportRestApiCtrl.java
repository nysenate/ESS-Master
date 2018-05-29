package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.auth.TimePermissionObject;
import gov.nysenate.ess.time.service.attendance.AttendanceReportUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

import static gov.nysenate.ess.core.model.period.PayPeriodType.AF;
import static gov.nysenate.ess.time.model.auth.TimePermissionObject.*;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/attendance/report")
public class AttendanceReportRestApiCtrl extends BaseRestApiCtrl {

    private static final ImmutableSet<TimePermissionObject> attendanceReportPermissions =
            ImmutableSet.of(ACCRUAL, ALLOWANCE, ATTENDANCE_RECORDS, TIME_RECORDS);

    private final AttendanceReportUrlService reportUrlService;
    private final PayPeriodService payPeriodService;
    private final EmployeeInfoService empInfoService;

    @Autowired
    public AttendanceReportRestApiCtrl(AttendanceReportUrlService reportUrlService, PayPeriodService payPeriodService, EmployeeInfoService empInfoService) {
        this.reportUrlService = reportUrlService;
        this.payPeriodService = payPeriodService;
        this.empInfoService = empInfoService;
    }

    /**
     * Get Attendance Report API
     * -------------------------
     *
     * Generates a pdf attendance report for the given employee on the pay period of the given date.
     * (GET) /api/v1/attendance/report
     *
     * Request Parameters: empId - int - required - The employee for the report
     *                     date - Date - required - Determines pay period of the report
     */
    @GetMapping(produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getAttendanceReport(
            @RequestParam int empId,
            @RequestParam String date) throws IOException {
        LocalDate parsedDate = parseISODate(date, "date");
        attendanceReportPermissions.stream()
                .map(po -> new EssTimePermission(empId, po, GET, parsedDate))
                .forEach(this::checkPermission);
        PayPeriod payPeriod = payPeriodService.getPayPeriod(AF, parsedDate);
        Employee employee = empInfoService.getEmployee(empId);

        UriComponents attendanceReportUri = reportUrlService.getAttendanceReportUri(empId, payPeriod);
        URL url = attendanceReportUri.toUri().toURL();

        String uid = Optional.ofNullable(employee.getUid()).orElse(Integer.toString(empId));
        String filename = uid + "_" + payPeriod.getEndDate() + ".pdf";

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                .header("Content-Type", APPLICATION_PDF_VALUE + "; name=\"" + filename + "\"")
                .body(new InputStreamResource(url.openStream())) ;
    }

}
