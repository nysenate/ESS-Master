package gov.nysenate.ess.travel.provider.gsa.meal;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.travel.provider.senate.SenateMie;

/**
 * This view is used for deserializing the GSA mie json response from the mie api endpoint.
 */
public class GsaMieApiView {

    private double total;
    private double breakfast;
    private double lunch;
    private double dinner;
    private double incidental;
    @JsonProperty("FirstLastDay ") // Note the space at the end.
    private double FirstLastDay;

    public GsaMieApiView() {
    }

    /**
     * Convert to a {@link SenateMie}
     * @param fiscalYear The fiscal year of this rate.
     * @return
     */
    public SenateMie toGsaMie(int fiscalYear) {
//        return new SenateMie(0, fiscalYear, new Dollars(total), new Dollars(breakfast), new Dollars(lunch),
//                new Dollars(dinner), new Dollars(incidental), new Dollars(FirstLastDay));
        return null;
    }

    public double getTotal() {
        return total;
    }

    public double getBreakfast() {
        return breakfast;
    }

    public double getLunch() {
        return lunch;
    }

    public double getDinner() {
        return dinner;
    }

    public double getIncidental() {
        return incidental;
    }

    public double getFirstLastDay() {
        return FirstLastDay;
    }
}
