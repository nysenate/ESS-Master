package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.application.route.destination.DestinationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class SqlRouteDao extends SqlBaseDao implements RouteDao {

    @Autowired private DestinationDao destinationDao;

    @Override
    @Transactional(value = "localTxManager")
    public void saveRoute(Route route, int appVersionId, int previousAppVersionId) {
        Route previousRoute = selectRoute(previousAppVersionId);
        if (previousRoute.equals(route)) {
            // If Route did not change, just update the join table with the new versionId.
            for (Leg leg : previousRoute.getAllLegs()) {
                // Use the previous route because it has the correct Leg.id's
                joinLegWithAppVersion(leg, appVersionId);
            }
        }
        else {
            // FIXME leg.to and nextLeg.from should reference same db row.
            List<Destination> destinations = route.getAllLegs().stream()
                    .flatMap(leg -> Stream.of(leg.getFrom(), leg.getTo()))
                    .collect(Collectors.toList());
            destinationDao.insertDestinations(destinations);

            int sequenceNo = 0;
            for (Leg leg : route.getOutboundLegs()) {
                insertLeg(leg, true, sequenceNo);
                joinLegWithAppVersion(leg, appVersionId);
                sequenceNo++;
            }
            for (Leg leg : route.getReturnLegs()) {
                insertLeg(leg, false, sequenceNo);
                joinLegWithAppVersion(leg, appVersionId);
                sequenceNo++;
            }
        }
    }

    /**
     * Insert into the table which joins app_version and app_leg
     */
    private void joinLegWithAppVersion(Leg leg, int appVersionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appVersionId", appVersionId)
                .addValue("legId", leg.getId());
        String sql = SqlRouteQuery.INSERT_JOIN_TABLE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertLeg(Leg leg, boolean isOutbound, int sequenceNo) {
        MapSqlParameterSource params = legParams(leg, isOutbound, sequenceNo);
        String sql = SqlRouteQuery.INSERT_ROUTE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        leg.setId((Integer) keyHolder.getKeys().get("leg_id"));
    }

    private MapSqlParameterSource legParams(Leg leg, boolean isOutbound, int sequenceNo) {
        return new MapSqlParameterSource()
                .addValue("fromDestinationId", leg.getFrom().getId())
                .addValue("toDestinationId", leg.getTo().getId())
                .addValue("travelDate", toDate(leg.getTravelDate()))
                .addValue("methodOfTravel", leg.getModeOfTransportation().getMethodOfTravel().name())
                .addValue("methodOfTravelDescription", leg.getModeOfTransportation().getDescription())
                .addValue("miles", String.valueOf(leg.getMiles()))
                .addValue("mileageRate", leg.getMileageRate().toString())
                .addValue("isOutbound", isOutbound)
                .addValue("sequenceNo", sequenceNo);
    }

    @Override
    public Route selectRoute(int appVersionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("appVersionId", appVersionId);
        String sql = SqlRouteQuery.SELECT_LEGS_FOR_VERSION.getSql(schemaMap());
        List<Integer> appVersionLegIds = localNamedJdbc.queryForList(sql, params, Integer.class);

        if (appVersionLegIds.isEmpty()) {
            return Route.EMPTY_ROUTE;
        }

        MapSqlParameterSource routeParams = new MapSqlParameterSource("legIds", appVersionLegIds);
        String routeSql = SqlRouteQuery.SELECT_ROUTE.getSql(schemaMap());
        RouteHandler handler = new RouteHandler(destinationDao);
        localNamedJdbc.query(routeSql, routeParams, handler);
        return handler.getRoute();
    }

    private enum SqlRouteQuery implements BasicSqlQuery {
        INSERT_ROUTE(
                "INSERT INTO ${travelSchema}.app_leg(from_destination_id, to_destination_id, travel_date," +
                        " method_of_travel, method_of_travel_description, miles, mileage_rate, is_outbound, sequence_no) \n" +
                        " VALUES(:fromDestinationId, :toDestinationId, :travelDate," +
                        " :methodOfTravel, :methodOfTravelDescription, :miles, :mileageRate, :isOutbound, :sequenceNo)"
        ),
        INSERT_JOIN_TABLE(
                "INSERT INTO ${travelSchema}.app_version_leg(app_version_id, leg_id)\n" +
                        " VALUES(:appVersionId, :legId)"
        ),
        SELECT_LEGS_FOR_VERSION(
                "SELECT leg_id\n" +
                        " FROM ${travelSchema}.app_version_leg\n" +
                        " WHERE app_version_id = :appVersionId"
        ),
        SELECT_ROUTE(
                "SELECT leg_id, from_destination_id, to_destination_id, travel_date, method_of_travel," +
                        " method_of_travel_description, miles, mileage_rate, is_outbound\n" +
                        " FROM ${travelSchema}.app_leg\n" +
                        " WHERE leg_id IN (:legIds)\n" +
                        " ORDER BY sequence_no ASC"
        );

        private String sql;

        SqlRouteQuery(String sql) {
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

    private class RouteHandler extends BaseHandler {

        private LegMapper legMapper;
        private List<Leg> outboundLegs = new ArrayList<>();
        private List<Leg> returnLegs = new ArrayList<>();

        public RouteHandler(DestinationDao destinationDao) {
            this.legMapper = new LegMapper(destinationDao);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Leg leg = legMapper.mapRow(rs, rs.getRow());
            if (isOutboundLeg(rs)) {
                outboundLegs.add(leg);
            } else {
                returnLegs.add(leg);
            }
        }

        private boolean isOutboundLeg(ResultSet rs) throws SQLException {
            return rs.getBoolean("is_outbound");
        }

        Route getRoute() {
            return new Route(outboundLegs, returnLegs);
        }
    }

    private class LegMapper extends BaseRowMapper<Leg> {

        private DestinationDao destinationDao;

        LegMapper(DestinationDao destinationDao) {
            this.destinationDao = destinationDao;
        }

        @Override
        public Leg mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Leg(
                    rs.getInt("leg_id"),
                    destinationDao.selectDestination(rs.getInt("from_destination_id")),
                    destinationDao.selectDestination(rs.getInt("to_destination_id")),
                    new ModeOfTransportation(MethodOfTravel.valueOf(rs.getString("method_of_travel")), rs.getString("method_of_travel_description")),
                    getLocalDate(rs, "travel_date"),
                    Double.valueOf(rs.getString("miles")),
                    new BigDecimal(rs.getString("mileage_rate"))
            );
        }
    }
}
