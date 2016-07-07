package gov.nysenate.ess.supply.requisition.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.core.util.*;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SqlRequisitionDao extends SqlBaseDao implements RequisitionDao {

    @Autowired private SqlLineItemDao lineItemDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private LocationService locationService;

    @Override
    @Transactional(value = "localTxManager")
    public Requisition saveRequisition(Requisition requisition) {
        requisition = insertRequisitionContent(requisition);
        saveRequisitionInfo(requisition);
        lineItemDao.insertRequisitionLineItems(requisition);
        return requisition;
    }

    /**
     * Inserts a new requisition revision into the requisition_content table.
     * @return Requisition with its revisionId set.
     */
    private Requisition insertRequisitionContent(Requisition requisition) {
        MapSqlParameterSource params = requisitionParams(requisition);
        String sql = SqlRequisitionQuery.INSERT_REQUISITION_CONTENT.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return requisition.setRevisionId((Integer) keyHolder.getKeys().get("revision_id"));
    }

    /**
     * Saves Requisition global information to the requisition table.
     * Updates the row if it exists, otherwise inserts a new row.
     */
    private void saveRequisitionInfo(Requisition requisition) {
        if (!updateRequisition(requisition)) {
            insertRequisition(requisition);
        }
    }

    private boolean updateRequisition(Requisition requisition) {
        MapSqlParameterSource params = requisitionParams(requisition);
        String sql = SqlRequisitionQuery.UPDATE_REQUISITION.getSql(schemaMap());
        return localNamedJdbc.update(sql, params) == 1;
    }

    private void insertRequisition(Requisition requisition) {
        MapSqlParameterSource params = requisitionParams(requisition);
        String sql = SqlRequisitionQuery.INSERT_REQUISITION.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    @Override
    public synchronized Requisition getRequisitionById(int requisitionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("requisitionId", requisitionId);
        String sql = SqlRequisitionQuery.GET_REQUISITION_BY_ID.getSql(schemaMap());
        RequisitionRowMapper rowMapper = new RequisitionRowMapper(employeeInfoService, locationService, lineItemDao);
        return localNamedJdbc.queryForObject(sql, params, rowMapper);
    }

    @Override
    public synchronized PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("destination", formatSearchString(destination))
                .addValue("customerId", formatSearchString(customerId))
                .addValue("statuses", extractEnumSetParams(statuses))
                .addValue("fromDate", toDate(DateUtils.startOfDateTimeRange(dateRange)))
                .addValue("toDate", toDate(DateUtils.endOfDateTimeRange(dateRange)));
        ensureValidColumn(dateField);
        String sql = generateSearchQuery(SqlRequisitionQuery.SEARCH_REQUISITIONS_PARTIAL, dateField, new OrderBy(dateField, SortOrder.DESC), limitOffset);
        PaginatedRowHandler<Requisition> paginatedRowHandler = new PaginatedRowHandler<>(limitOffset, "total_rows", new RequisitionRowMapper(employeeInfoService, locationService, lineItemDao));
        localNamedJdbc.query(sql, params, paginatedRowHandler);
        return paginatedRowHandler.getList();
    }

    /** Protect against sql injection attacks by validating that dateFiled
     * matches one of the requisition date time fields. */
    private void ensureValidColumn(String dateField) {
        boolean matches = dateField.matches("ordered_date_time|processed_date_time|completed_date_time|" +
                                            "approved_date_time|rejected_date_time|modified_date_time");
        if (!matches) {
            throw new RuntimeException("Given datefield: " + dateField + "is not a valid requisition date field.");
        }
    }

    /** Dynamically generates query date range filter using the supplied {@code dateField}.
     * Then completes the query by adding Order by and Limit Offset information. */
    private String generateSearchQuery(SqlRequisitionQuery baseSearchQuery, String dateField, OrderBy orderBy, LimitOffset limoff) {
        String sql = baseSearchQuery.getSql(schemaMap()) + dateField + " BETWEEN :fromDate AND :toDate";
        sql = SqlQueryUtils.withOrderByClause(sql, orderBy);
        return SqlQueryUtils.withLimitOffsetClause(sql, limoff, baseSearchQuery.getVendor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized PaginatedList<Requisition> searchOrderHistory(String destinationId, int customerId, EnumSet<RequisitionStatus> statuses,
                                                         Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("destination", formatSearchString(destinationId))
                .addValue("customerId", customerId)
                .addValue("statuses", extractEnumSetParams(statuses))
                .addValue("fromDate", toDate(DateUtils.startOfDateTimeRange(dateRange)))
                .addValue("toDate", toDate(DateUtils.endOfDateTimeRange(dateRange)));
        ensureValidColumn(dateField);
        String sql = generateSearchQuery(SqlRequisitionQuery.ORDER_HISTORY_PARTIAL, dateField, new OrderBy(dateField, SortOrder.DESC), limitOffset);
        PaginatedRowHandler<Requisition> paginatedRowHandler = new PaginatedRowHandler<>(limitOffset, "total_rows", new RequisitionRowMapper(employeeInfoService, locationService, lineItemDao));
        localNamedJdbc.query(sql, params, paginatedRowHandler);
        return paginatedRowHandler.getList();
    }

    private MapSqlParameterSource requisitionParams(Requisition requisition) {
        return new MapSqlParameterSource()
                .addValue("requisitionId", requisition.getRequisitionId())
                .addValue("revisionId", requisition.getRevisionId())
                .addValue("customerId", requisition.getCustomer().getEmployeeId())
                .addValue("destination", requisition.getDestination().getLocId().toString())
                .addValue("status", requisition.getStatus().toString())
                .addValue("issuerId", requisition.getIssuer().map(Employee::getEmployeeId).orElse(null))
                .addValue("note", requisition.getNote().orElse(null))
                .addValue("modifiedBy", requisition.getModifiedBy().getEmployeeId())
                .addValue("modifiedDateTime", toDate(requisition.getModifiedDateTime()))
                .addValue("orderedDateTime", toDate(requisition.getOrderedDateTime()))
                .addValue("processedDateTime", requisition.getProcessedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("completedDateTime", requisition.getCompletedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("approvedDateTime", requisition.getApprovedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("rejectedDateTime", requisition.getRejectedDateTime().map(SqlBaseDao::toDate).orElse(null))
                .addValue("savedInSfms", requisition.getSavedInSfms());
    }

    private String formatSearchString(String param) {
        return param != null && param.equals("all") ? "%" : param;
    }

    /** Convert an EnumSet into a Set containing each enum's name. */
    private Set<String> extractEnumSetParams(EnumSet<RequisitionStatus> statuses) {
        return statuses.stream().map(Enum::name).collect(Collectors.toSet());
    }

    private enum SqlRequisitionQuery implements BasicSqlQuery {
        INSERT_REQUISITION(
                "INSERT INTO ${supplySchema}.requisition(current_revision_id, ordered_date_time, \n" +
                "processed_date_time, completed_date_time, approved_date_time, rejected_date_time, saved_in_sfms) \n" +
                "VALUES (:revisionId, :orderedDateTime, :processedDateTime, :completedDateTime, \n" +
                ":approvedDateTime, :rejectedDateTime, :savedInSfms)"
        ),

        UPDATE_REQUISITION(
                "UPDATE ${supplySchema}.requisition SET current_revision_id = :revisionId, ordered_date_time = :orderedDateTime, \n" +
                "processed_date_time = :processedDateTime, completed_date_time = :completedDateTime, \n" +
                "approved_date_time = :approvedDateTime, rejected_date_time = :rejectedDateTime, \n" +
                "saved_in_sfms = :savedInSfms \n" +
                "WHERE requisition_id = :requisitionId"
        ),

        /** Never insert the revision id, let it auto increment. */
        INSERT_REQUISITION_CONTENT(
                "INSERT INTO ${supplySchema}.requisition_content(requisition_id, destination, status, \n" +
                "issuing_emp_id, note, customer_id, modified_by_id, modified_date_time) \n" +
                "VALUES (:requisitionId, :destination, :status::${supplySchema}.requisition_status, \n" +
                ":issuerId, :note, :customerId, :modifiedBy, :modifiedDateTime)"
        ),

        GET_REQUISITION_BY_ID(
                "SELECT * from ${supplySchema}.requisition r INNER JOIN ${supplySchema}.requisition_content c \n" +
                "ON r.current_revision_id = c.revision_id \n" +
                "WHERE r.requisition_id = :requisitionId"
        ),

        /** Must use {@link #generateSearchQuery(SqlRequisitionQuery, String, OrderBy, LimitOffset) generateSearchQuery}
         * to complete this query. */
        SEARCH_REQUISITIONS_PARTIAL(
                "SELECT *, count(*) OVER() as total_rows \n" +
                "FROM ${supplySchema}.requisition as r \n" +
                "INNER JOIN ${supplySchema}.requisition_content as c ON r.current_revision_id = c.revision_id \n" +
                "WHERE c.destination LIKE :destination AND Coalesce(c.customer_id::text, '') LIKE :customerId \n" +
                "AND c.status::text IN (:statuses) AND r."
        ),

        /** Must use {@link #generateSearchQuery(SqlRequisitionQuery, String, OrderBy, LimitOffset) generateSearchQuery}
         * to complete this query. */
        ORDER_HISTORY_PARTIAL(
                "SELECT *, count(*) OVER() as total_rows \n" +
                "FROM ${supplySchema}.requisition as r \n" +
                "INNER JOIN ${supplySchema}.requisition_content as c ON r.current_revision_id = c.revision_id \n" +
                "WHERE (c.destination = :destination OR c.customer_id = :customerId) \n" +
                "AND c.status::text IN (:statuses) AND r."
        )
        ;

        private String sql;

        SqlRequisitionQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class RequisitionRowMapper extends BaseRowMapper<Requisition> {

        private EmployeeInfoService employeeInfoService;
        private LocationService locationService;
        private SqlLineItemDao lineItemDao;

        protected RequisitionRowMapper(EmployeeInfoService employeeInfoService, LocationService locationService,
                                    SqlLineItemDao lineItemDao) {
            this.employeeInfoService = employeeInfoService;
            this.locationService = locationService;
            this.lineItemDao = lineItemDao;
        }

        @Override
        public Requisition mapRow(ResultSet rs, int i) throws SQLException {
            return new Requisition.Builder()
                    .withRequisitionId(rs.getInt("requisition_id"))
                    .withRevisionId(rs.getInt("current_revision_id"))
                    .withCustomer(employeeInfoService.getEmployee(rs.getInt("customer_id")))
                    .withDestination(locationService.getLocation(LocationId.ofString(rs.getString("destination"))))
                    .withLineItems(lineItemDao.getLineItems(rs.getInt("current_revision_id")))
                    .withStatus(RequisitionStatus.valueOf(rs.getString("status")))
                    .withIssuer(employeeInfoService.getEmployee(rs.getInt("issuing_emp_id")))
                    .withNote(rs.getString("note"))
                    .withModifiedBy(employeeInfoService.getEmployee(rs.getInt("modified_by_id")))
                    .withModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"))
                    .withOrderedDateTime(getLocalDateTimeFromRs(rs, "ordered_date_time"))
                    .withProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"))
                    .withCompletedDateTime(getLocalDateTimeFromRs(rs, "completed_date_time"))
                    .withApprovedDateTime(getLocalDateTimeFromRs(rs, "approved_date_time"))
                    .withRejectedDateTime(getLocalDateTimeFromRs(rs, "rejected_date_time"))
                    .withSavedInSfms(rs.getBoolean("saved_in_sfms"))
                    .build();
        }
    }
}
