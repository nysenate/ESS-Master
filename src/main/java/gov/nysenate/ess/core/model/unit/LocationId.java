package gov.nysenate.ess.core.model.unit;

/**
 * The location code together with the location type uniquely identify a location.
 */
public final class LocationId {

    private final String code;
    private final LocationType type;

    public LocationId(String code, LocationType type) {
        this.code = code;
        this.type = type;
    }

    public LocationId(String locCode, char locType) {
        this.code = locCode;
        this.type = LocationType.valueOfCode(locType);
    }

    public LocationId(String locationId) {
        if (locationId == null || !locationId.contains("-")) {
            this.code = null;
            this.type = null;
        }
        else {
            String[] parts = locationId.split("-");
            this.code = parts[0];
            this.type = LocationType.valueOfCode(parts[1].charAt(0));
        }
    }

    /** Creates a location Id from its toString() output. */
    public static LocationId ofString(String locString) {
        return new LocationId(locString);
    }

    public String getCode() {
        return code;
    }

    public LocationType getType() {
        return type;
    }

    public String getTypeAsString() {
        return String.valueOf(type.getCode());
    }

    /**
     * Was this locationId constructed with valid syntax.
     * ie. locCode and locType not null, locType is a valid type,
     * not missing '-' if constructing from string.
     *
     * May want to replace with NullObjectPattern.
     */
    public boolean isSyntacticallyValid() {
        if (code == null || type == null || code.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return code + '-' + type.getCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationId that = (LocationId) o;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
