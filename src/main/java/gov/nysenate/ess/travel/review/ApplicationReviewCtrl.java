package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionBuilder;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionObject;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/review")
public class ApplicationReviewCtrl extends BaseRestApiCtrl {

    @Autowired private ApplicationReviewService appReviewService;
    @Autowired private EmployeeInfoService employeeInfoService;

    /**
     * Get app reviews which need review by the logged in user.
     * <p>
     * Don't implement strict permission checking for this endpoint because it is
     * used in the badge service for all users. Instead of checking permissions,
     * this method will return an empty list if its not applicable to the user.
     */
    @RequestMapping(value = "/pending", method = RequestMethod.GET)
    public BaseResponse getPendingReviews(@RequestParam(required = false) List<String> roles) {
        List<TravelRole> roleList = roles == null || roles.isEmpty()
                ? new ArrayList<>()
                : roles.stream().map(TravelRole::of).collect(Collectors.toList());
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());

        List<ApplicationReview> pendingReviews = new ArrayList<>();
        for (TravelRole r : roleList) {
            pendingReviews.addAll(appReviewService.pendingAppReviewsForEmpWithRole(employee, r));
        }

        return ListViewResponse.of(pendingReviews.stream()
                .map(ApplicationReviewView::new)
                .collect(Collectors.toList()));
    }

    /**
     * @return A list of ApplicationReviews where the users role has performed an action.
     */
    @RequestMapping(value = "/history")
    public BaseResponse reviewHistory() {
        TravelRole role = getSubjectRole();
        List<ApplicationReview> reviewHistory = appReviewService.appReviewHistoryForEmp(role);
        return ListViewResponse.of(reviewHistory.stream()
                .map(ApplicationReviewView::new)
                .collect(Collectors.toList()));
    }

    @RequestMapping(value = "/{appReviewId}/approve", method = RequestMethod.POST)
    public BaseResponse approveApplication(@PathVariable int appReviewId, @RequestBody ActionBodyView body) {
        TravelRole role = getSubjectRole();
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());
        ApplicationReview appReview = appReviewService.getApplicationReview(appReviewId);

        checkPermission(new TravelPermissionBuilder()
                .forEmpId(appReview.application().getTraveler().getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forAction(RequestMethod.POST)
                .buildPermission());

        appReviewService.approveApplication(appReview, employee, body.getNotes(), role, body.isDiscussionRequested());
        return new ViewObjectResponse<>(new ApplicationReviewView(appReview));
    }

    @RequestMapping(value = "/{appReviewId}/disapprove", method = RequestMethod.POST)
    public BaseResponse disapproveApplication(@PathVariable int appReviewId, @RequestBody ActionBodyView body) {
        if (body.getNotes() == null || body.getNotes().trim().isEmpty()) {
            throw new InvalidRequestParamEx(body.getNotes(), "notes", "String", "not null & not empty");
        }
        TravelRole role = getSubjectRole();
        Employee employee = employeeInfoService.getEmployee(getSubjectEmployeeId());
        ApplicationReview appReview = appReviewService.getApplicationReview(appReviewId);

        checkPermission(new TravelPermissionBuilder()
                .forEmpId(appReview.application().getTraveler().getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forAction(RequestMethod.POST)
                .buildPermission());

        appReviewService.disapproveApplication(appReview, employee, body.getNotes(), role);
        return new ViewObjectResponse<>(new ApplicationReviewView(appReview));
    }

    /**
     * @return The TravelRole assigned to the user.
     */
    private TravelRole getSubjectRole() {
        TravelRole role = TravelRole.NONE;
        if (getSubject().hasRole(TravelRole.SUPERVISOR.name())) {
            role = TravelRole.SUPERVISOR;
        }
        if (getSubject().hasRole(TravelRole.DEPUTY_EXECUTIVE_ASSISTANT.name())) {
            role = TravelRole.DEPUTY_EXECUTIVE_ASSISTANT;
        }
        if (getSubject().hasRole(TravelRole.SECRETARY_OF_THE_SENATE.name())) {
            role = TravelRole.SECRETARY_OF_THE_SENATE;
        }
        if (getSubject().hasRole(TravelRole.MAJORITY_LEADER.name())) {
            role = TravelRole.MAJORITY_LEADER;
        }
        return role;
    }
}
