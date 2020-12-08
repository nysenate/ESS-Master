package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.authorization.role.TravelRoleFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRoles;
import gov.nysenate.ess.travel.notifications.email.TravelEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationReviewService {

    @Autowired private ApplicationReviewDao appReviewDao;
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private SupervisorInfoService supervisorInfoService;
    @Autowired private TravelRoleFactory travelRoleFactory;
    @Autowired private TravelEmailService emailService;

    public void approveApplication(ApplicationReview applicationReview, Employee approver, String notes,
                                   TravelRole approverRole) {
        Action approvalAction = new Action(0, approver, approverRole,
                ActionType.APPROVE, notes, LocalDateTime.now());
        applicationReview.addAction(approvalAction);
        saveApplicationReview(applicationReview);

        if (applicationReview.nextReviewerRole() == TravelRole.NONE) {
            // If no one else needs to review, the application is completely approved.
            applicationReview.application().approve();
            travelApplicationService.saveApplication(applicationReview.application());
            emailService.sendApprovalEmails(applicationReview.application());
        }
    }

    public void disapproveApplication(ApplicationReview applicationReview, Employee disapprover,
                                      String reason, TravelRole disapproverRole) {
        Action disapproveAction = new Action(0, disapprover, disapproverRole, ActionType.DISAPPROVE,
                reason, LocalDateTime.now());
        applicationReview.addAction(disapproveAction);
        saveApplicationReview(applicationReview);

        applicationReview.application().disapprove(reason);
        travelApplicationService.saveApplication(applicationReview.application());
        emailService.sendDisapprovalEmails(applicationReview.application(), disapprover, reason);
    }

    public ApplicationReview createApplicationReview(TravelApplication app) {
        TravelRoles roles = travelRoleFactory.travelRolesForEmp(app.getTraveler());
        ApplicationReview appReview = new ApplicationReview(app, roles.apex());
        return appReview;
    }

    public ApplicationReview getApplicationReview(int appReviewId) {
        return appReviewDao.selectAppReviewById(appReviewId);
    }

    public void saveApplicationReview(ApplicationReview appReview) {
        appReviewDao.saveApplicationReview(appReview);
    }

    public ApplicationReview updateIsShared(ApplicationReview review, boolean isShared) {
        review.setShared(isShared);
        saveApplicationReview(review);
        return review;
    }

    public List<ApplicationReview> pendingAppReviewsForEmpWithRole(Employee employee, TravelRole role) {
        if (role == TravelRole.NONE) {
            return new ArrayList<>();
        }

        List<ApplicationReview> pendingReviews = appReviewDao.pendingReviewsByRole(role);
        if (role == TravelRole.SUPERVISOR) {
            pendingReviews = pendingReviews.stream()
                    .filter(a -> isSupervisor(employee, a))
                    .collect(Collectors.toList());
        }

        return pendingReviews;
    }

    /**
     * Returns shared app reviews that have not yet been approved by all reviewers.
     * @return
     */
    public List<ApplicationReview> pendingSharedAppReviews() {
        return appReviewDao.pendingSharedReviews();
    }

    public List<ApplicationReview> appReviewHistoryForRole(TravelRole role) {
        // TODO if SUPERVISOR need to filter out employees who are not theirs
        // TODO wait on implementing this until Dept Heads are added.
        return appReviewDao.reviewHistoryForRole(role);
    }

    private boolean isSupervisor(Employee employee, ApplicationReview applicationReview) {
        return supervisorInfoService.getSupervisorIdForEmp(applicationReview.application().getTraveler().getEmployeeId(), LocalDate.now()) == employee.getEmployeeId();
    }
}
