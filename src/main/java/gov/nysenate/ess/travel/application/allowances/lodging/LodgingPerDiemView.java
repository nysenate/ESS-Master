package gov.nysenate.ess.travel.application.allowances.lodging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LodgingPerDiemView implements ViewObject {

    private String date;
    private AddressView address;
    private String rate;
    @JsonProperty("isReimbursementRequested")
    private boolean isReimbursementRequested;
    private String requestedPerDiem;
    private String maximumPerDiem;

    public LodgingPerDiemView() {
    }

    public LodgingPerDiemView(LodgingPerDiem lpd) {
        this.date = lpd.date().format(DateTimeFormatter.ISO_DATE);
        this.address = new AddressView(lpd.address());
        this.rate = lpd.rate().toString();
        this.isReimbursementRequested = lpd.isReimbursementRequested();
        this.requestedPerDiem = lpd.requestedPerDiem().toString();
        this.maximumPerDiem = lpd.maximumPerDiem().toString();
    }

    @JsonIgnore
    public LocalDate date() {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }

    @JsonIgnore
    public BigDecimal rate() {
        return new BigDecimal(rate);
    }

    public String getDate() {
        return date;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getRate() {
        return rate;
    }

    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    public String getRequestedPerDiem() {
        return requestedPerDiem;
    }

    public String getMaximumPerDiem() {
        return maximumPerDiem;
    }

    @Override
    public String getViewType() {
        return "lodging-per-diem";
    }
}
