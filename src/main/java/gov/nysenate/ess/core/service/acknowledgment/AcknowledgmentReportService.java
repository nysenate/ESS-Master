package gov.nysenate.ess.core.service.acknowledgment;

import gov.nysenate.ess.core.dao.acknowledgment.SqlAckDocDao;
import gov.nysenate.ess.core.model.acknowledgment.EmpAckReport;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AcknowledgmentReportService {

    @Autowired
    EmployeeInfoService employeeInfoService;

    @Autowired
    SqlAckDocDao sqlAckDocDao;

    private static final Logger logger = LoggerFactory.getLogger(AcknowledgmentReportService.class);

    public AcknowledgmentReportService() {}

    /*
    Returns a list of all acknowledgments for each employee
    Removes emp who have no acks
     */
    public ArrayList<EmpAckReport> getAllAcksFromEmployees() {
        ArrayList<EmpAckReport> completeAckReportList = getAllEmployeesAcks();
        removeEmpsWithNoAcks(completeAckReportList);
        return completeAckReportList;
    }

    /*
    This corresponds to the 2nd report, in which all acks from all employees are reported. W
    We only care about employees that have acked at least one document
     */
    private ArrayList<EmpAckReport> getAllEmployeesAcks() {
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);

        ArrayList<EmpAckReport> completeAckReportList = new ArrayList<>();

        List<EmpAckReport> completeAckReports = sqlAckDocDao.getAllAcksForEmpWithTimestampAndDocRef();

        for (Employee emp : employees) {
            EmpAckReport finalEmpAckReport = new EmpAckReport(emp.getEmployeeId(), emp.getFirstName(),emp.getLastName(),
                    emp.getEmail(),emp.getWorkLocation());

            ArrayList<EmpAckReport> empAckReports = determineEmpAcks(completeAckReports, emp.getEmployeeId());

            mergeAcksIntoFinalReport(finalEmpAckReport, empAckReports);

            completeAckReportList.add(finalEmpAckReport);
        }
        return completeAckReportList;
    }

    /*This corresponds to the 1st report, in which all acks on a single document are reported as well as those who have
    not acked this document
     */
    public ArrayList<EmpAckReport> getAllAcksForAckDocWithTitleAndYear(String title, int year) {
        Set<Employee> employees = employeeInfoService.getAllEmployees(true);
        ArrayList<EmpAckReport> completeAckReportList = new ArrayList<>();

        List<EmpAckReport> empsWhoHaveAckedSpecificDoc = sqlAckDocDao.getAllAcksForDocWithNameAndYear(title,year);

        for (Employee emp : employees) {
            EmpAckReport finalEmpAckReport = new EmpAckReport(emp.getEmployeeId(), emp.getFirstName(),emp.getLastName(),
                    emp.getEmail(),emp.getWorkLocation());

            EmpAckReport empAckReport = determineEmpAck(empsWhoHaveAckedSpecificDoc, emp.getEmployeeId());

            if (!empAckReport.getAckedTimeMap().isEmpty()) {
                finalEmpAckReport.getAckedTimeMap().putAll(empAckReport.getAckedTimeMap());
            }

            completeAckReportList.add(finalEmpAckReport);
        }

        return completeAckReportList;
    }

    /*
    Merge rows of ack reports for a specific employee into the final report for that employee
     */
    private void mergeAcksIntoFinalReport(EmpAckReport finalReport,List<EmpAckReport> ackedReports) {
        for (int i=0; i<ackedReports.size();i++) {
            finalReport.getAckedTimeMap().putAll(ackedReports.get(i).getAckedTimeMap());
        }
    }

    private ArrayList<EmpAckReport> determineEmpAcks(List<EmpAckReport> allAckReports, int empId) {
        ArrayList<EmpAckReport> empAckReports = new ArrayList<>();
        Iterator it = allAckReports.iterator();
        while(it.hasNext()) {
            EmpAckReport empAckReport = (EmpAckReport) it.next();
            if (empAckReport.getEmpId() == empId) {
                empAckReports.add(empAckReport);
                it.remove();
            }
        }
        return empAckReports;
    }

    private EmpAckReport determineEmpAck(List<EmpAckReport> allAckReports, int empId) {
        EmpAckReport empAckReport = new EmpAckReport();
        Iterator it = allAckReports.iterator();
        while(it.hasNext()) {
            EmpAckReport empAckReportFromAll = (EmpAckReport) it.next();
            if (empAckReportFromAll.getEmpId() == empId) {
                empAckReport = empAckReportFromAll;
                it.remove();
            }
        }
        return empAckReport;
    }

    /*
    For the 2nd report, We only care about employees with Acks.
    And so this removes all employees who have not acked anything at all
     */
    private void removeEmpsWithNoAcks(List<EmpAckReport> completeReports) {
        Iterator it = completeReports.iterator();
        while(it.hasNext()) {
            EmpAckReport empAckReport = (EmpAckReport) it.next();
            if (empAckReport.getAckedTimeMap().isEmpty()) {
                it.remove();
            }
        }
    }
}
