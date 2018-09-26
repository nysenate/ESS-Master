package gov.nysenate.ess.travel.provider.gsa.client;

import java.util.Objects;

public class GsaResponseId {

    private final int fiscalYear;
    private final String zipcode;

    public GsaResponseId(int fiscalYear, String zipcode) {
        this.fiscalYear = fiscalYear;
        this.zipcode = zipcode;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public String getZipcode() {
        return zipcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GsaResponseId that = (GsaResponseId) o;
        return fiscalYear == that.fiscalYear &&
                Objects.equals(zipcode, that.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fiscalYear, zipcode);
    }
}
