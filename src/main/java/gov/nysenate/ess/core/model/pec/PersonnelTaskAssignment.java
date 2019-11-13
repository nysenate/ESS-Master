package gov.nysenate.ess.core.model.pec;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

import java.time.LocalDateTime;

/**
 * An assignment of a personnel task to an employee.
 *
 * Contains reference to the task and the current completion status.
 */
public class PersonnelTaskAssignment implements Comparable<PersonnelTaskAssignment> {

    /** Identifies task to be completed */
    private final int taskId;
    /** Id of task assignee. */
    private final int empId;
    /** User that updated the task.  Generally same as employee id. */
    private final Integer updateEmpId;
    /** Time of last action on the task. */
    private final LocalDateTime updateTime;
    private final boolean completed;
    private final boolean active;

    public PersonnelTaskAssignment(int taskId,
                                   int empId,
                                   Integer updateEmpId,
                                   LocalDateTime updateTime,
                                   boolean completed,
                                   boolean active) {
        this.taskId = taskId;
        this.empId = empId;
        this.updateEmpId = updateEmpId;
        this.updateTime = updateTime;
        this.completed = completed;
        this.active = active;
    }

    public static PersonnelTaskAssignment newTask(int empId, int taskId) {
        return new PersonnelTaskAssignment(taskId, empId, null, null, false, true);
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonnelTaskAssignment)) return false;
        PersonnelTaskAssignment that = (PersonnelTaskAssignment) o;
        return taskId == that.taskId &&
                empId == that.empId &&
                updateEmpId == that.updateEmpId &&
                completed == that.completed &&
                active == that.active &&
                Objects.equal(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskId, empId, updateEmpId, updateTime, completed, active);
    }

    @Override
    public int compareTo(PersonnelTaskAssignment o) {
        return ComparisonChain.start()
                .compare(this.empId, o.empId)
                .compare(this.taskId, o.taskId)
                .result();
    }

    /* --- Getters --- */

    public int getTaskId() {
        return taskId;
    }

    public int getEmpId() {
        return empId;
    }

    public Integer getUpdateEmpId() {
        return updateEmpId;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isActive() {
        return active;
    }
}
