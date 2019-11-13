package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;

import java.util.List;

/**
 * Supports read and write operations to data store of {@link PersonnelAssignedTask} data.
 */
public interface PersonnelAssignedTaskDao {

    /**
     * Get all tasks assigned to the given employee.
     *
     * @param empId int
     * @return {@link List<PersonnelAssignedTask>}
     */
    List<PersonnelAssignedTask> getTasksForEmp(int empId);

    /**
     * Add or update the given {@link PersonnelAssignedTask} to the db.
     *
     * @param task {@link PersonnelAssignedTask}
     */
    void updatePersonnelAssignedTask(PersonnelAssignedTask task);
}
