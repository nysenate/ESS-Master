package gov.nysenate.ess.time.service.attendance.validation;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.time.dao.attendance.AttendanceDao;
import gov.nysenate.ess.time.model.attendance.AttendanceRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordStatus;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 */
@Service
public class EssTimeRecordCreationValidator implements TimeRecordCreationValidator {

    @Autowired private EmpTransactionService transactionService;
    @Autowired private TimeRecordService timeRecordService;
    @Autowired private AttendanceDao attendanceDao;
    @Autowired private PayPeriodService periodService;

    /** {@inheritDoc} */
    @Override
    public void validateRecordCreation(int empId, PayPeriod period) throws TimeRecordCreationNotPermittedEx {
        checkPeriodEligibility(empId, period);
        checkPersonnelStatus(empId, period);
        checkRecordStatus(empId, period);
        checkForExistingRecord(empId, period);
    }

    /* --- Internal Methods --- */

    /**
     * Ensure that the given period is eligible for record creation
     * The period is eligible so long as it ends no more than 1 period past the current period
     */
    private void checkPeriodEligibility(int empId, PayPeriod period) throws TimeRecordCreationNotPermittedEx {
        PayPeriod currentPeriod = periodService.getPayPeriod(PayPeriodType.AF, LocalDate.now());
        PayPeriod nextPeriod = periodService.getPayPeriod(PayPeriodType.AF, currentPeriod.getEndDate().plusDays(1));

        if (nextPeriod.compareTo(period) == -1) {
            throw new TimeRecordCreationNotPermittedEx(empId, period);
        }
    }

    /**
     * Ensure that the employee is employed fo the pay period and that time entry is required
     */
    private void checkPersonnelStatus(int empId, PayPeriod period) throws TimeRecordCreationNotPermittedEx {
        TransactionHistory transHistory = transactionService.getTransHistory(empId);

        // Get range set of employed dates
        RangeSet<LocalDate> empStatusRangeSet =
                RangeUtils.getEffectiveRanges(
                        RangeUtils.toRangeMap(transHistory.getEffectiveEmpStatus(Range.all())),
                        true
                );

        // Get range set of dates where time entry is required
        RangeMap<LocalDate, Boolean> perStatRangeMap = transHistory.getEffectivePersonnelStatus(Range.all())
                .entrySet().stream()
                .map(entry -> Pair.of(entry.getKey(),
                        entry.getValue().isEmployed() && entry.getValue().isTimeEntryRequired()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Pair::getKey, Pair::getValue, (a, b) -> b, TreeMap::new),
                        RangeUtils::toRangeMap
                ));
        RangeSet<LocalDate> perStatRangeSet = RangeUtils.getEffectiveRanges(perStatRangeMap, true);

        // Get intersection of effective emp status and per status dates
        RangeSet<LocalDate> fullPerStatRangeSet = RangeUtils.intersection(empStatusRangeSet, perStatRangeSet);

        if (!RangeUtils.intersects(fullPerStatRangeSet, period.getDateRange())) {
            throw new TimeRecordCreationNotPermittedEx(empId, period);
        }
    }

    /**
     * Ensure that all active time records are submitted for the pay period
     */
    private void checkRecordStatus(int empId, PayPeriod period) throws TimeRecordCreationNotPermittedEx {
        timeRecordService.getActiveTimeRecords(empId).stream()
                .map(TimeRecord::getRecordStatus)
                .filter(TimeRecordStatus::isUnlockedForEmployee)
                .findAny()
                .ifPresent(timeRecordStatus -> {
                    throw new TimeRecordCreationNotPermittedEx(empId, period);});
    }

    /**
     * Ensure that the given pay period is not already covered by existing records
     */
    private void checkForExistingRecord(int empId, PayPeriod period) throws TimeRecordCreationNotPermittedEx {
        List<AttendanceRecord> attendanceRecords = attendanceDao.getAttendanceRecords(empId, period.getDateRange());

        List<TimeRecord> timeRecords = timeRecordService.getTimeRecords(
                Collections.singleton(empId),
                Collections.singleton(period),
                TimeRecordStatus.getAll());

        RangeSet<LocalDate> recordRanges = TreeRangeSet.create();

        attendanceRecords.stream()
                .map(AttendanceRecord::getDateRange)
                .forEach(recordRanges::add);
        timeRecords.stream()
                .map(TimeRecord::getDateRange)
                .forEach(recordRanges::add);

        if (recordRanges.encloses(period.getDateRange())) {
            throw new TimeRecordCreationNotPermittedEx(empId, period);
        }
    }
}
