package gov.nysenate.ess.core.service.acknowledgment;

import gov.nysenate.ess.core.dao.acknowledgment.AckDocDao;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.pec.acknowledgment.Acknowledgment;
import gov.nysenate.ess.core.model.pec.acknowledgment.EmpAckReport;
import gov.nysenate.ess.core.model.pec.acknowledgment.ReportAck;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AcknowledgmentReportService implements EssAcknowledgmentReportService{

    private static final Logger logger = LoggerFactory.getLogger(AcknowledgmentReportService.class);

    private static final Comparator<EmpAckReport> respCenterComparator = Comparator.comparing(
            (report) -> Optional.ofNullable(report.getEmployee())
                    .map(Employee::getRespCenter)
                    .map(ResponsibilityCenter::getHead)
                    .map(ResponsibilityHead::getShortName)
                    .orElse("")
    );

    private final EmployeeInfoService employeeInfoService;
    private final AckDocDao sqlAckDocDao;

    @Autowired
    public AcknowledgmentReportService(EmployeeInfoService employeeInfoService, AckDocDao sqlAckDocDao) {
        this.employeeInfoService = employeeInfoService;
        this.sqlAckDocDao = sqlAckDocDao;
    }

    /** {@inheritDoc} */
    @Override
    public EmpAckReport getAllAcksFromEmployee(int empId) {
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);

        EmpAckReport finalEmpAckReport = new EmpAckReport();

        List<Acknowledgment> allEmpAcks = sqlAckDocDao.getAllAcknowledgmentsForEmp(empId);
        List<AckDoc> allAckDocs = sqlAckDocDao.getAllAckDocs();
        List<ReportAck> reportAcks = new ArrayList<>();

        for (Employee emp : employees) {
            if (emp.getEmployeeId() == empId) {
                finalEmpAckReport.setEmployee(emp);
            }
        }

        for (AckDoc ackDoc: allAckDocs) {
            reportAcks.add(new ReportAck(ackDoc,null));
        }

        for (ReportAck reportAck: reportAcks) {
            for (Acknowledgment ack: allEmpAcks) {
                if (ack.getAckDocId().equals( reportAck.getAckDoc().getTaskId())) {
                    reportAck.setAck(ack);
                    reportAck.getAck().setTimestamp(reportAck.getAck().getTimestamp().withNano(0));
                }
            }
        }

        finalEmpAckReport.setAcks(reportAcks);

        return finalEmpAckReport;
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<EmpAckReport> getAllAcksForAckDocById(int ackDocId) {
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);
        ArrayList<EmpAckReport> completeAckReportList = new ArrayList<>();

        List<ReportAck> empsWhoHaveAckedSpecificDoc = sqlAckDocDao.getAllAcksForAckDocById(ackDocId);
        AckDoc reuqestedAckDoc = sqlAckDocDao.getAckDoc(ackDocId);


        for (Employee emp : employees) {
            EmpAckReport finalEmpAckReport = new EmpAckReport(emp);
            ReportAck reportAck = determineEmpAck(empsWhoHaveAckedSpecificDoc, emp.getEmployeeId());
            reportAck.setAckDoc(reuqestedAckDoc);
            finalEmpAckReport.getAcks().add(reportAck);
            completeAckReportList.add(finalEmpAckReport);
        }

        completeAckReportList.sort(respCenterComparator);

        return completeAckReportList;
    }

    /**
     * Finds the employee acknowledgment amongst every other employees acknowledgment
     * This removes the ack once its found to improve efficiency
     *
     * {@link EmpAckReport}
     * @param allReportAcks List of all EmpAckReports
     * @param empId The employee id whose EmpAckReport we want to find
     * @return the EmpAckReport of the empid that was passed in
     */
    private ReportAck determineEmpAck(List<ReportAck> allReportAcks, int empId) {
        ReportAck reportAck = new ReportAck();
        Iterator it = allReportAcks.iterator();
        while(it.hasNext()) {
            ReportAck AckReportFromAll = (ReportAck) it.next();
            if (AckReportFromAll.getAck().getEmpId() == empId) {
                reportAck = AckReportFromAll;
                it.remove();
            }
        }
        return reportAck;
    }
}
