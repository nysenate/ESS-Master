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
import gov.nysenate.ess.supply.authorization.responsibilityhead.TempResponsibilityHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        Set<Location> locations;
        // If a supply employee, can deliver to any location.
        if (getSubject().isPermitted(SupplyPermission.SUPPLY_EMPLOYEE.getPermission())) {
            locations = workLocationsIn(locationDao.getLocations());
        } else {
            Employee employee = employeeService.getEmployee(empId);

            // Locations for all temp rch's and employees rch.
            List<ResponsibilityHead> rchs = trchService.tempRchForEmp(employee);
            rchs.add(employee.getRespCenter().getHead());
            locations = new HashSet<>(locationDao.getLocationsByResponsibilityHead(rchs));

            // Add the employees work location
            locations.add(employee.getWorkLocation());

            // Filter out any non work type locations.
            locations = workLocationsIn(locations);
        }
        return ListViewResponse.of(locations.stream()
                .map(LocationView::new)
                .collect(Collectors.toList()));
    }

    private Set<Location> workLocationsIn(Collection<Location> locations) {
        return locations
                .stream()
                .filter(loc -> loc.getLocId().getType() == LocationType.WORK)
                .collect(Collectors.toSet());
    }
}
