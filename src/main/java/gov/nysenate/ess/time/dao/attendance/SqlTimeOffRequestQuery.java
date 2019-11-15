package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlTimeOffRequestQuery implements BasicSqlQuery {

    ADD_TIME_OFF_REQUEST(
            "INSERT INTO time.time_off_request (employee_id, supervisor_id, status, update_timestamp, start_date, end_date) " +
            "VALUES( :employeeId, :supervisorId, :status::\"time\".time_off_status_type, :updateTimestamp, :startDate, :endDate)"
    ),
    SELECT_TIME_OFF_REQUEST(
            "SELECT *\n" +
            "FROM time.time_off_request r\n"
    ),
    SELECT_COMMENTS_BY_REQUEST_ID(
            "SELECT *\n" +
            "FROM time.time_off_request_comment c\n" +
            "WHERE c.request_id = :requestId\n"
    ),
    SELECT_DAYS_BY_REQUEST_ID(
            "SELECT *\n" +
            "FROM time.time_off_request_day d\n" +
            "WHERE d.request_id = :requestId\n"
    ),
    SELECT_TIME_OFF_REQUEST_BY_REQUEST_ID(
        SELECT_TIME_OFF_REQUEST.getSql() + "WHERE r.request_id = :requestId"
    ),
    SELECT_TIME_OFF_REQUEST_IDS_BY_EMPLOYEE_ID(
            "SELECT request_id\n" +
            "FROM time.time_off_request r\n" +
            "WHERE r.employee_id = :employeeId"
    ),
    SELECT_TIME_OFF_REQUEST_IDS_BY_SUPERVISOR_ID(
            "SELECT request_id\n" +
            "FROM time.time_off_request r\n" +
            "WHERE r.supervisor_id = :supervisorId"
    ),
    SELECT_TIME_OFF_REQUESTS_IDS_BY_EMP_SUP_YEAR(
            "SELECT request_id\n" +
            "FROM time.time_off_request r\n" +
            "WHERE r.supervisor_id = :supervisorId " +
            "and r.employee_id = :employeeId and (EXTRACT(YEAR FROM r.start_date) = " +
                    ":year or EXTRACT(YEAR FROM r.end_date) = :year)\n"
    ),
    ADD_COMMENT_TO_TIME_OFF_REQUEST(
            "INSERT INTO time.time_off_request_comment" +
                    "(comment, author_id, time_stamp, request_id)\n" +
                    "VALUES(:text, :authorId, :timestamp, :requestId) "
    ),
    ADD_DAY_TO_TIME_OFF_REQUEST(
            "INSERT INTO time.time_off_request_day" +
                    "(request_id, request_date, work_hours, holiday_hours, vacation_hours, personal_hours, " +
                    "sick_emp_hours, sick_fam_hours, misc_hours, misc_type) \n" +
                    "VALUES(:requestId, :date, :workHours, :holidayHours, :vacationHours, :personalHours, " +
                    ":sickEmpHours, :sickFamHours, :miscHours, :miscType::\"time\".time_off_misc_type)"
    ),
    UPDATE_REQUEST(
            "UPDATE time.time_off_request\n" +
            "SET status = CAST(:status AS \"time\".time_off_status_type), update_timestamp = :updateTimestamp, " +
                    "start_date = :startDate, end_date = :endDate\n" +
            "WHERE request_id = :requestId\n"
    ),
    REMOVE_ALL_COMMENTS_FOR_REQUEST(
            "DELETE FROM time.time_off_request_comment\n" +
                    "WHERE request_id = :requestId\n"
    ),
    REMOVE_ALL_DAYS_FOR_REQUEST(
            "DELETE FROM time.time_off_request_day\n" +
                    "WHERE request_id = :requestId\n"
    ),
    ;

    private String sql;

    SqlTimeOffRequestQuery(String sql) { this.sql = sql; }

    @Override
    public String getSql() { return sql; }

    @Override
    public DbVendor getVendor() { return DbVendor.ORACLE_10g; }
}
