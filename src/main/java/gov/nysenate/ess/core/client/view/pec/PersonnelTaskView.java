package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;

import java.time.LocalDateTime;
import java.util.Optional;

public abstract class PersonnelTaskView implements ViewObject {

    private final int taskId;
    private final PersonnelTaskType taskType;
    private final String title;
    private final LocalDateTime effectiveDateTime;
    private final LocalDateTime endDateTime;
    private final boolean active;

    public PersonnelTaskView(PersonnelTask task) {
        this.taskId = task.getTaskId();
        this.taskType = task.getTaskType();
        this.title = task.getTitle();
        this.effectiveDateTime = task.getEffectiveDateTime();
        this.endDateTime = task.getEndDateTime();
        this.active = task.isActive();
    }

    @Override
    public String getViewType() {
        String typeString = Optional.ofNullable(taskType)
                .map(Enum::name)
                .orElse(null);
        return "personnel-task-" + typeString;
    }

    public int getTaskId() {
        return taskId;
    }

    public PersonnelTaskType getTaskType() {
        return taskType;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public boolean isActive() {
        return active;
    }
}
