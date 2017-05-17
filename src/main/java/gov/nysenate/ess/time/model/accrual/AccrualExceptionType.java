package gov.nysenate.ess.time.model.accrual;

public enum AccrualExceptionType
{
    NO_TYP_TRANSACTIONS_FOUND (
        "No transactions were found that indicate the employee's pay type."),

    NO_MIN_TRANSACTIONS_FOUND (
        "No transactions were found that indicate the minimum hours the employee needs to work. " +
        "The minimum hours are required to determine accrual rate."),

    NO_ACTIVE_ANNUAL_RECORD_FOUND (
        "No annual summaries exist for the date range specified. Cannot reliably determine " +
        "the accrual totals for the requested pay period without this information."),

    PERIOD_RECORD_NOT_FOUND (
            "A period accrual record for the requested period could not be retrieved or calculated." ),

    NO_FROM_DATE_FOUND (
            "Could not retrieve annual accrual end date or continuous service date for the employee" ),
    ;

    String message;

    AccrualExceptionType(String message) {
        this.message = message;
    }
}
