package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.SimpleTravelApplicationView;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationApprovalView implements ViewObject {

    private SimpleTravelApplicationView travelApplication;
    private List<ActionView> actions;

    public ApplicationApprovalView() {
    }

    public ApplicationApprovalView(ApplicationApproval appApproval) {
        travelApplication = new SimpleTravelApplicationView(appApproval.application());
        actions = appApproval.actions().stream()
                .map(ActionView::new)
                .collect(Collectors.toList());
    }

    public SimpleTravelApplicationView getTravelApplication() {
        return travelApplication;
    }

    public List<ActionView> getActions() {
        return actions;
    }

    @Override
    public String getViewType() {
        return "application-approval";
    }
}
