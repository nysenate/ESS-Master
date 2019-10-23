package gov.nysenate.ess.travel.application.unsubmitted;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationService;
import gov.nysenate.ess.travel.application.TravelApplicationView;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverridesView;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.application.route.RouteService;
import gov.nysenate.ess.travel.application.route.RouteView;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionBuilder;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/unsubmitted")
public class UnsubmittedAppCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(UnsubmittedAppCtrl.class);
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private UnsubmittedAppDao unsubmittedAppDao;
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private RouteService routeService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getUnsubmittedApps(@RequestParam int userId, @RequestParam int travelerId) throws IOException {
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(travelerId)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission());

        TravelApplicationView appView;
        Optional<TravelApplicationView> viewOpt = unsubmittedAppDao.find(userId, travelerId);
        if (viewOpt.isPresent()) {
            appView = viewOpt.get();
        } else {
            appView = new TravelApplicationView(new TravelApplication(employeeInfoService.getEmployee(travelerId)));
            unsubmittedAppDao.save(userId, appView);
        }
        return new ViewObjectResponse<>(appView);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public void deleteUnsubmittedApp(@RequestParam int userId, @RequestParam int travelerId) throws IOException {
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(travelerId)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.POST)
                .buildPermission());
        unsubmittedAppDao.delete(userId, travelerId);
    }

    private TravelApplicationView findApp(int userId, int travelerId) throws IOException {
        return unsubmittedAppDao.find(userId, travelerId)
                .orElseThrow(() -> new InvalidRequestParamEx(userId + " & " + travelerId,
                        "userId & travelerId",
                        "int & int",
                        "No Unsubmitted travel app found with provided userId and travelerId"));
    }

    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public BaseResponse patchUnsubmittedApp(@RequestParam int userId,
                                            @RequestParam int travelerId,
                                            @RequestBody Map<String, String> patches) throws IOException {
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(travelerId)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.POST)
                .buildPermission());

        TravelApplicationView view = findApp(userId, travelerId);
        TravelApplication app = view.toTravelApplication();
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());

        // Perform all updates specified in the patch.
        // FIXME changes should be on a new amendment, not active...
        for (Map.Entry<String, String> patch : patches.entrySet()) {
            switch (patch.getKey()) {
                case "purposeOfTravel":
                    app.activeAmendment().setPurposeOfTravel(patch.getValue());
                    break;
                case "route":
                    RouteView routeView = OutputUtils.jsonToObject(patch.getValue(), RouteView.class);
                    Route fullRoute = routeService.createRoute(routeView.toRoute());
                    app.activeAmendment().setRoute(fullRoute);
                    break;
                case "allowances":
                    AllowancesView allowancesView = OutputUtils.jsonToObject(patch.getValue(), AllowancesView.class);
                    app.activeAmendment().setAllowances(allowancesView.toAllowances());
                    break;
                case "mealPerDiems":
                    MealPerDiemsView mealPerDiemsView = OutputUtils.jsonToObject(patch.getValue(), MealPerDiemsView.class);
                    travelApplicationService.updateMealPerDiems(app, mealPerDiemsView.toMealPerDiems());
                    break;
                case "lodgingPerDiems":
                    LodgingPerDiemsView lodgingPerDiemsView = OutputUtils.jsonToObject(patch.getValue(), LodgingPerDiemsView.class);
                    travelApplicationService.updateLodgingPerDiems(app, lodgingPerDiemsView.toLodgingPerDiems());
                    break;
                case "mileagePerDiems":
                    MileagePerDiemsView mileagePerDiemView = OutputUtils.jsonToObject(patch.getValue(), MileagePerDiemsView.class);
                    travelApplicationService.updateMileagePerDiems(app, mileagePerDiemView.toMileagePerDiems());
                    break;
                case "perDiemOverrides":
                    PerDiemOverridesView overridesView = OutputUtils.jsonToObject(patch.getValue(), PerDiemOverridesView.class);
                    app.activeAmendment().setPerDiemOverrides(overridesView.toPerDiemOverrides());
                    break;
                default:
                    logger.info("Call to travel application patch API did not contain a valid patch key. Patches were: " + patches.toString());
            }
        }

        TravelApplicationView appView = new TravelApplicationView(app);
        // Save after all changes are applied.
        unsubmittedAppDao.save(user.getEmployeeId(), appView);

        return new ViewObjectResponse<>(appView);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse submitApp(@RequestParam int userId,
                                  @RequestParam int travelerId) throws IOException {
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(travelerId)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.POST)
                .buildPermission());

        TravelApplicationView view = findApp(userId, travelerId);
        TravelApplication app = view.toTravelApplication();
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());

        app = travelApplicationService.submitTravelApplication(app, user);
        unsubmittedAppDao.delete(userId, travelerId);

        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }
}
