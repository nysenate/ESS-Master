package gov.nysenate.ess.supply.allowance.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.allowance.service.LocationAllowanceService;
import gov.nysenate.ess.supply.allowance.view.LocationAllowanceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/location/allowance")
public class LocationAllowanceRestCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(LocationAllowanceRestCtrl.class);

    @Autowired private LocationAllowanceService locationAllowanceService;

    @RequestMapping("/{locId}")
    public BaseResponse getLocationAllowance(@PathVariable String locId) {
        LocationId locationId = new LocationId(locId);
        LocationAllowanceView locationAllowanceView = new LocationAllowanceView(locationAllowanceService.getLocationAllowance(locationId));
        return new ViewObjectResponse<>(locationAllowanceView);
    }
}
