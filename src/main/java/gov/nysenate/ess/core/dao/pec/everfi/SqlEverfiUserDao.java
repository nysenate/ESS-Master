package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiUserIDs;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import static gov.nysenate.ess.core.dao.pec.everfi.SqlEverfiUserQuery.*;


@Repository
public class SqlEverfiUserDao extends SqlBaseDao implements EverfiUserDao {

    public EverfiUserIDs getEverfiUserIDsWithEmpID(int empID) {
        return localNamedJdbc.queryForObject(
                SELECT_EMP_BY_EMP_ID.getSql(schemaMap()),
                new MapSqlParameterSource("empID", empID),
                everfiUserIDsRowMapper
        );
    }

    public EverfiUserIDs getEverfiUserIDsWithEverfiUUID(String everfiUUID) {
        return localNamedJdbc.queryForObject(
                SELECT_EMP_BY_EVERFI_ID.getSql(schemaMap()),
                new MapSqlParameterSource("everfi_UUID", everfiUUID),
                everfiUserIDsRowMapper
        );
    }

    public int insertEverfiUserIDs(String everfiUUID, Integer empID) throws DuplicateKeyException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("everfi_UUID", everfiUUID);
        params.addValue("emp_id", empID);
        return localNamedJdbc.update(INSERT_EVERFI_USER_ID.getSql(schemaMap()), params);
    }

    private static final RowMapper<EverfiUserIDs> everfiUserIDsRowMapper = (rs, rowNum) ->
            new EverfiUserIDs(
                    rs.getInt("emp_id"),
                    rs.getString("everfi_uuid")
            );

}
