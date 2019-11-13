package gov.nysenate.ess.core.dao.pec;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Object that holds values to pass into a query for {@link PersonnelAssignedTask}s.
 *
 * Unset/null fields will be ignored in the query.
 */
public class PATQueryBuilder {

    private Integer empId;
    private Boolean active;
    private Set<PersonnelTaskId> taskIds = null;
    private PersonnelTaskType taskType;
    private Boolean completed;
    private LocalDateTime completedFrom;
    private LocalDateTime completedTo;

    @Override
    public String toString() {
        return new StringJoiner(", ", PATQueryBuilder.class.getSimpleName() + "[", "]")
                .add("empId=" + empId)
                .add("active=" + active)
                .add("taskType=" + taskType)
                .add("completed=" + completed)
                .add("completedFrom=" + completedFrom)
                .add("completedTo=" + completedTo)
                .add("taskIds=" + taskIds)
                .toString();
    }

    /* --- Builder-style setters --- */

    public PATQueryBuilder setEmpId(Integer empId) {
        this.empId = empId;
        return this;
    }

    public PATQueryBuilder setActive(Boolean active) {
        this.active = active;
        return this;
    }

    public PATQueryBuilder setTaskType(PersonnelTaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public PATQueryBuilder setCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public PATQueryBuilder setTaskIds(Collection<PersonnelTaskId> taskIds) {
        this.taskIds = Optional.ofNullable(taskIds)
                .map(ImmutableSet::copyOf)
                .orElse(null);
        return this;
    }

    public PATQueryBuilder setCompletedFrom(LocalDateTime completedFrom) {
        this.completedFrom = completedFrom;
        return this;
    }

    public PATQueryBuilder setCompletedTo(LocalDateTime completedTo) {
        this.completedTo = completedTo;
        return this;
    }

    /* --- Getters --- */

    public Integer getEmpId() {
        return empId;
    }

    public PersonnelTaskType getTaskType() {
        return taskType;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Set<PersonnelTaskId> getTaskIds() {
        return taskIds;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCompletedFrom() {
        return completedFrom;
    }

    public LocalDateTime getCompletedTo() {
        return completedTo;
    }
}
