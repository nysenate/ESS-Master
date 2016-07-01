package gov.nysenate.ess.seta.service.attendance.validation;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.seta.model.attendance.TimeEntry;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.model.attendance.TimeRecordScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Checks time records to make sure that no time record contains time entry that exceeds the employee's yearly allowance
 */
@Service
public class HourIncrementTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(HourIncrementTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState) {
        // If the saved record contains entries where the employee was a temporary employee
        return record.getScope() == TimeRecordScope.EMPLOYEE &&
                record.getTimeEntries().stream()
                        .anyMatch(entry -> entry.getPayType() != PayType.TE);
    }

    /**
     *  checkTimeRecord check hourly increments for all of the daily records
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException
     */

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState) throws TimeRecordErrorException {
        ImmutableList<TimeEntry> entries =  record.getTimeEntries();

        for (TimeEntry entry : entries) {
            checkHourIncrement(entry);
        }

    }

    /**
     * checkHourIncrement:  determine what fields need to be checked based on Payrype Type being TE or RA/SA.
     *
     * @param entry
     * @throws TimeRecordErrorException
     */

    private void checkHourIncrement(TimeEntry entry)  throws TimeRecordErrorException {
        if (entry.getPayType().equals("TE")) {
            checkTeHourIncrements(entry);
        }
        else {
            checkRaSaHourIncrements(entry);
        }
    }

    /**
     * checkTeHourIncrements:  Check Temporary fields hourly increments
     *
     * @param entry
     * @throws TimeRecordErrorException
     */

    private void checkTeHourIncrements(TimeEntry entry) throws TimeRecordErrorException {
        BigDecimal divisor = new BigDecimal(.25);

        if (entry.getWorkHours().orElse(BigDecimal.ZERO).remainder(divisor).equals(BigDecimal.ZERO)) {
            return;
        }
        throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_HORULY_INCREMENT,
                new InvalidParameterView("workTime", "decimal",
                        "worktime = " + entry.getWorkHours().toString(),  entry.getWorkHours().toString()));
    }

    /**
     * checkRaSaHourIncrements: Check non-temporary fields hourly increments
     *
     * @param entry
     * @throws TimeRecordErrorException
     */


    private void checkRaSaHourIncrements(TimeEntry entry) throws TimeRecordErrorException {
        BigDecimal divisor = new BigDecimal(.50);
        BigDecimal workTime = entry.getWorkHours().orElse(BigDecimal.ZERO);
        BigDecimal holidayTime = entry.getHolidayHours().orElse(BigDecimal.ZERO);
        BigDecimal travelTime = entry.getTravelHours().orElse(BigDecimal.ZERO);
        BigDecimal personalTime = entry.getPersonalHours().orElse(BigDecimal.ZERO);
        BigDecimal vacationTime = entry.getVacationHours().orElse(BigDecimal.ZERO);
        BigDecimal sickEmpTime = entry.getSickEmpHours().orElse(BigDecimal.ZERO);
        BigDecimal sickFamTime = entry.getSickFamHours().orElse(BigDecimal.ZERO);
        BigDecimal miscTime = entry.getMiscHours().orElse(BigDecimal.ZERO);

        if (!workTime.remainder(divisor).equals(BigDecimal.ZERO)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_HORULY_INCREMENT,
                    new InvalidParameterView("workTime", "decimal",
                            "worktime = " + workTime.toString(),  workTime.toString()));

        } else if (!holidayTime.remainder(divisor).equals(BigDecimal.ZERO)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_HORULY_INCREMENT,
                    new InvalidParameterView("holidayTime", "decimal",
                            "holidayTime = " + holidayTime.toString(), holidayTime.toString()));
        } else if (!travelTime.remainder(divisor).equals(BigDecimal.ZERO)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_HORULY_INCREMENT,
                    new InvalidParameterView("travelTime", "decimal",
                            "travelTime = " + travelTime.toString(), travelTime.toString()));
        } else if (!personalTime.remainder(divisor).equals(BigDecimal.ZERO)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_HORULY_INCREMENT,
                    new InvalidParameterView("personalTime", "decimal",
                            "personalTime = " + personalTime.toString(), personalTime.toString()));
        } else if (!vacationTime.remainder(divisor).equals(BigDecimal.ZERO)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_HORULY_INCREMENT,
                    new InvalidParameterView("vacationTime", "decimal",
                            "vacationTime = " + vacationTime.toString(), vacationTime.toString()));
        } else if (!sickEmpTime.remainder(divisor).equals(BigDecimal.ZERO)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_HORULY_INCREMENT,
                    new InvalidParameterView("sickEmpTime", "decimal",
                            "sickEmpTime = " + sickEmpTime.toString(), sickEmpTime.toString()));
        } else if (!sickFamTime.remainder(divisor).equals(BigDecimal.ZERO)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_HORULY_INCREMENT,
                    new InvalidParameterView("sickFamTime", "decimal",
                            "sickFamTime = " + sickFamTime.toString(), sickFamTime.toString()));
        } else if (!miscTime.remainder(divisor).equals(BigDecimal.ZERO)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_HORULY_INCREMENT,
                    new InvalidParameterView("miscTime", "decimal",
                            "miscTime = " + miscTime.toString(), miscTime.toString()));
        }
    }

}
