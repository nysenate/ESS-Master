package gov.nysenate.ess.core.service.pec;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.dao.pec.PATQueryBuilder;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
@Transactional(DatabaseConfig.localTxManager)
public class EssPersonnelTaskAssignerIT extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(EssPersonnelTaskAssignerIT.class);

    @Autowired private PersonnelTaskAssigner taskAssigner;
    @Autowired private PersonnelTaskSource taskSource;
    @Autowired private PersonnelAssignedTaskDao taskDao;
    @Autowired private EmployeeInfoService empInfoService;

    @Test
    public void empTaskAssignTest() {
        final int bogusEmpId = 1122334455;
        assertTrue("Test employee has no initial assignments",
                taskDao.getTasksForEmp(bogusEmpId).isEmpty());

        taskAssigner.assignTasks(bogusEmpId);

        Set<PersonnelTaskId> tasksPresent = taskDao.getTasksForEmp(bogusEmpId).stream()
                .map(PersonnelAssignedTask::getTaskId)
                .collect(Collectors.toSet());

        Set<PersonnelTaskId> allTaskIds = taskSource.getAllPersonnelTaskIds();

        assertEquals("Test employee is assigned all active tasks", allTaskIds, tasksPresent);
    }

    @Test
    public void assignAllEmpsTest() {
        Set<PersonnelTaskId> allTaskIds = taskSource.getAllPersonnelTaskIds();
        if (allTaskIds.isEmpty()) {
            // If there are no tasks to assign this test won't work properly.
            logger.warn("Skipping \"assignAllEmpsTest\" due to lack of active tasks.");
            return;
        }
        logger.info("assigning tasks to all employees...");
        taskAssigner.assignTasks();
        logger.info("done assigning tasks");

        List<PersonnelAssignedTask> allTasks = taskDao.getTasks(new PATQueryBuilder());
        HashMultimap<Integer, PersonnelTaskId> empTaskMultimap = allTasks.stream().collect(Multimaps.toMultimap(
                PersonnelAssignedTask::getEmpId,
                PersonnelAssignedTask::getTaskId,
                HashMultimap::create
        ));

        Set<Integer> activeEmpIds = empInfoService.getActiveEmpIds();

        for (int empId : activeEmpIds) {
            assertTrue("Active employee " + empId + " must have active tasks", empTaskMultimap.containsKey(empId));
            assertTrue("Active employee " + empId + " must be assigned all currently active tasks",
                    empTaskMultimap.get(empId).containsAll(allTaskIds));
        }
    }

}