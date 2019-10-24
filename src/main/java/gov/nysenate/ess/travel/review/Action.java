package gov.nysenate.ess.travel.review;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.time.LocalDateTime;

public class Action {

    protected int actionId;
    protected final Employee user;
    protected final TravelRole role;
    protected final ActionType type;
    protected final String notes;
    protected final LocalDateTime dateTime;
    protected final boolean isDiscussionRequested;

    public Action(int actionId, Employee user, TravelRole role, ActionType type,
                  String notes, LocalDateTime dateTime, boolean isDiscussionRequested) {
        // Cannot request discussion when disapproving.
        Preconditions.checkArgument(!(type == ActionType.DISAPPROVE && isDiscussionRequested));
        this.actionId = actionId;
        this.user = user;
        this.role = role;
        this.type = type;
        this.notes = notes;
        this.dateTime = dateTime;
        this.isDiscussionRequested = isDiscussionRequested;
    }

    /**
     * The user who performed this action.
     */
    public Employee user() {
        return user;
    }

    /**
     * The role of the user who performed this action.
     */
    public TravelRole role() {
        return role;
    }

    /**
     * Was this action an approval.
     */
    public boolean isApproval() {
        return type == ActionType.APPROVE;
    }

    /**
     * Was this action a disapproval
     */
    public boolean isDisapproval() {
        return type == ActionType.DISAPPROVE;
    }

    /**
     * Any notes left by the user.
     */
    public String notes() {
        return notes;
    }

    /**
     * The datetime this action was performed.
     */
    public LocalDateTime dateTime() {
        return dateTime;
    }
}
