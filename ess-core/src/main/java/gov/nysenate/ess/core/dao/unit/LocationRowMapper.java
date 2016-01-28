package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.personnel.mapper.RespHeadRowMapper;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationRowMapper extends BaseRowMapper<Location>
{
    private String pfx;

    private RespHeadRowMapper respHeadRowMapper;

    public LocationRowMapper(String locPfx, String rctrhdPfx) {
        this.pfx = locPfx;
        this.respHeadRowMapper = new RespHeadRowMapper(rctrhdPfx);
    }

    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getString(pfx + "CDLOCAT") != null) {
            Location loc = new Location();
            Address addr = new Address();
            loc.setCode(rs.getString(pfx + "CDLOCAT"));
            loc.setType(LocationType.valueOfCode(rs.getString(pfx + "CDLOCTYPE").charAt(0)));
            addr.setAddr1(rs.getString(pfx + "FFADSTREET1"));
            addr.setAddr2(rs.getString(pfx + "FFADSTREET2"));
            addr.setCity(rs.getString(pfx + "FFADCITY"));
            addr.setState(rs.getString(pfx + "ADSTATE"));
            addr.setZip5(rs.getString(pfx + "ADZIPCODE"));
            loc.setAddress(addr);
            loc.setResponsibilityHead(respHeadRowMapper.mapRow(rs, rowNum));
            return loc;
        }
        return null;
    }
}
