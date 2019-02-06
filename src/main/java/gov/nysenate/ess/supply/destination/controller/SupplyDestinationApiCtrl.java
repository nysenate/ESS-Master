package gov.nysenate.ess.supply.destination.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.dao.unit.LocationDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import gov.nysenate.ess.supply.authorization.responsibilityhead.TempResponsibilityHead;
import gov.nysenate.ess.supply.authorization.responsibilityhead.TempResponsibilityHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/destinations")
public class SupplyDestinationApiCtrl extends BaseRestApiCtrl {

    @Autowired private LocationDao locationDao;
    @Autowired private EmployeeInfoService employeeService;
    @Autowired private TempResponsibilityHeadService trchService;

    /**
     * This API is used to get the list of locations an employee is
     * allowed to select as a destination for their order.
     * <p>
     * Regular employees can only select destinations that
     * are part of their department.
     * <p>
     * Supply employees are able to create orders for employees,
     * and therefore can select any destination.
     *
     * @param empId The employee who's valid destinations should
     *              be returned.
     */
    @RequestMapping(value = "/{empId}")
    public BaseResponse getDestinationsForEmployee(@PathVariable int empId) {
        List<Location> locations;
        // If a supply employee, can deliver to any location.
        if (getSubject().isPermitted(SupplyPermission.SUPPLY_EMPLOYEE.getPermission())) {
            locations = workLocationsIn(locationDao.getLocations());
        } else {
            Employee employee = employeeService.getEmployee(empId);
            List<ResponsibilityHead> rchs =  trchService.tempRchForEmp(employee);
            rchs.add(employee.getRespCenter().getHead());
            locations = workLocationsIn(locationDao.getLocationsByResponsibilityHead(rchs));
        }
        return ListViewResponse.of(locations.stream()
                                            .map(LocationView::new)
                                            .collect(Collectors.toList()));
    }

    private List<Location> workLocationsIn(List<Location> locations) {
        return locations
                .stream()
                .filter(loc -> loc.getLocId().getType() == LocationType.WORK)
                .collect(Collectors.toList());
    }
}
