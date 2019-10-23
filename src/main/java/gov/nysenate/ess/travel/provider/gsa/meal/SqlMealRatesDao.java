package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class SqlMealRatesDao extends SqlBaseDao {

    /**
     * Get the effective Meal Rates for a given date.
     * @param date
     * @return
     */
    public MealRates getMealRates(LocalDate date) {
        MapSqlParameterSource params =  new MapSqlParameterSource()
                .addValue("date", toDate(date));
        String sql = SqlMealRatesQuery.GET_MEAL_RATES.getSql(schemaMap());
        MealRatesHandler handler = new MealRatesHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.results();
    }

    // TODO This needs some fixes... end date no longer null...
    public synchronized void insertMealRates(MealRates mealRates, LocalDate date) {
        updateCurrentRatesEndDate(date);
        Integer id = insertMealRate(date);
        insertMealTiers(mealRates, id);
    }

    private void updateCurrentRatesEndDate(LocalDate endDate) {
        MapSqlParameterSource params =  new MapSqlParameterSource()
                .addValue("endDate", toDate(endDate));
        String sql = SqlMealRatesQuery.UPDATE_MEAL_RATE_END_DATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private Integer insertMealRate(LocalDate startDate) {
        MapSqlParameterSource params =  new MapSqlParameterSource()
                .addValue("startDate", toDate(startDate));
        String sql = SqlMealRatesQuery.INSERT_MEAL_RATE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("id");
    }

    private void insertMealTiers(MealRates mealRates, Integer id) {
        List<SqlParameterSource> paramList = createBatchParams(mealRates, id);
        String sql = SqlMealRatesQuery.INSERT_MEAL_TIER.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private List<SqlParameterSource> createBatchParams(MealRates mealRates, Integer id) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (MealTier tier: mealRates.getTiers()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("tier", tier.getTier())
                    .addValue("total", tier.getTotal().toString())
                    .addValue("incidental", tier.getIncidental().toString());
            paramList.add(params);
        }
        return paramList;
    }

    private enum SqlMealRatesQuery implements BasicSqlQuery {
        INSERT_MEAL_RATE(
                "INSERT INTO ${travelSchema}.meal_rate(id, start_date) \n" +
                        "VALUES (:id, :startDate)"
        ),
        INSERT_MEAL_TIER(
                "INSERT INTO ${travelSchema}.meal_tier(id, tier, total, incidental) " +
                        "VALUES (:id, :tier, :total, :incidental)"
        ),
        UPDATE_MEAL_RATE_END_DATE(
                "UPDATE ${travelSchema}.meal_rate " +
                        "SET end_date = :endDate " +
                        "WHERE end_date IS NULL"
        ),
        GET_MEAL_RATES(
                "SELECT mr.id, mr.start_date, mr.end_date, \n" +
                        "mt.id as meal_tier_id, mt.tier, mt.total, mt.incidental \n" +
                        "FROM ${travelSchema}.meal_tier mt \n" +
                        "INNER JOIN ${travelSchema}.meal_rate mr on mr.id = mt.meal_rate_id " +
                        "WHERE mr.start_date <= :date " +
                        "AND (mr.end_date IS NULL OR mr.end_date >= :date)"
        );

        private String sql;

        SqlMealRatesQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class MealRatesHandler extends BaseHandler {

        private UUID mealRateId;
        private LocalDate startDate;
        private LocalDate endDate;
        private Set<MealTier> tiers;

        public MealRatesHandler() {
            this.tiers = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (mealRateId == null) {
                mealRateId = UUID.fromString(rs.getString("id"));
                startDate = getLocalDateFromRs(rs, "start_date");
                endDate = getLocalDateFromRs(rs, "end_date");
            }
            MealTier tier = new MealTier(UUID.fromString(rs.getString("meal_tier_id")), rs.getString("tier"), rs.getString("total"), rs.getString("incidental"));
            tiers.add(tier);
        }

        public MealRates results() {
            return new MealRates(mealRateId, startDate, endDate, tiers);
        }
    }
}
