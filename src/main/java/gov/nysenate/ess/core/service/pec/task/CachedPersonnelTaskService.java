package gov.nysenate.ess.core.service.pec.task;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.dao.pec.task.detail.PersonnelTaskDetailDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Implements {@link PersonnelTaskService} using {@link PersonnelTaskDao} and {@link PersonnelTaskDetailDao}
 * and utilizing caching to improve performance.
 */
@Service
public class CachedPersonnelTaskService implements PersonnelTaskService, CachingService<String> {

    private static final Logger logger = LoggerFactory.getLogger(CachedPersonnelTaskService.class);

    /** Key used to store a map of all tasks */
    private static final String TASK_MAP_KEY = "TASK_MAP_KEY";
    /** Number of seconds the task map is valid in cache before it is evicted */
    private static final Long cacheEvictTime = Duration.ofMinutes(5).getSeconds();

    private final PersonnelTaskDao taskDao;
    private final ImmutableMap<PersonnelTaskType, PersonnelTaskDetailDao<?>> taskDetailDaoMap;
    private final Cache taskCache;
    private final EmployeeDao employeeDao;
    private final PersonnelTaskAssignmentDao personnelTaskAssignmentDao;

    public CachedPersonnelTaskService(PersonnelTaskDao taskDao,
                                      List<PersonnelTaskDetailDao<?>> taskDetailDaos,
                                      EhCacheManageService cacheManageService,
                                      EventBus eventBus,
                                      EmployeeDao employeeDao,
                                      PersonnelTaskAssignmentDao personnelTaskAssignmentDao) {
        this.taskDao = taskDao;
        this.taskDetailDaoMap = Maps.uniqueIndex(taskDetailDaos, PersonnelTaskDetailDao::taskType);
        this.taskCache = cacheManageService.registerTimeBasedCache(getCacheType().name(), cacheEvictTime);
        this.employeeDao = employeeDao;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
        eventBus.register(this);
    }

    /* --- PersonnelTaskService implementations --- */

    @Override
    public Set<Integer> getAllTaskIds(boolean activeOnly) {
        return getPersonnelTasks(activeOnly).stream()
                .map(PersonnelTask::getTaskId)
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public PersonnelTask getPersonnelTask(int taskId) throws PersonnelTaskNotFoundEx {
        PersonnelTask personnelTask = getTaskMap().get(taskId);
        if (personnelTask == null) {
            throw new PersonnelTaskNotFoundEx(taskId);
        }
        return personnelTask;
    }

    @Override
    public List<PersonnelTask> getPersonnelTasks(boolean activeOnly) {
        return getTaskMap().values().stream()
                .filter(task -> !activeOnly || task.isActive())
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public List<PersonnelTask> getActiveTasksInGroup(PersonnelTaskAssignmentGroup assignmentGroup) {
        return getPersonnelTasks(true).stream()
                .filter(task -> task.getAssignmentGroup() == assignmentGroup)
                .collect(ImmutableList.toImmutableList());
    }

    /* --- CachingService implementations --- */

    @Override
    public ContentCache getCacheType() {
        return ContentCache.PERSONNEL_TASK;
    }

    @Override
    public void evictContent(String key) {
        // Removing all since there is only one key
        evictCache();
    }

    @Override
    public void evictCache() {
        logger.info("Clearing personnel task cache...");
        taskCache.removeAll();
    }

    @Override
    public void warmCache() {
        evictCache();
        logger.info("Warming personnel task cache...");
        getPersonnelTasks(false);
    }

    public void markTasksComplete() {
        logger.info("Beginning the process of marking specific employees tasks complete");
        Set<Employee> employees = employeeDao.getActiveEmployees();
        Set<Employee> employeesToMarkComplete = new HashSet<>();
        employeesToMarkComplete.add(employeeDao.getEmployeeById(7689));
        employeesToMarkComplete.add(employeeDao.getEmployeeById(9268));
        employeesToMarkComplete.add(employeeDao.getEmployeeById(12867));

        for (Employee employee : employees) {
            if (employee.isSenator()) {
                employeesToMarkComplete.add(employee);
            }
        }

        for (Employee employee : employeesToMarkComplete) {

            List<PersonnelTaskAssignment> assignments =
                    personnelTaskAssignmentDao.getAssignmentsForEmp(employee.getEmployeeId());

            for (PersonnelTaskAssignment assignment : assignments) {
                if (!assignment.isCompleted()) {
                    personnelTaskAssignmentDao.setTaskComplete(
                            employee.getEmployeeId(), assignment.getTaskId(), employee.getEmployeeId());
                }
            }
        }
        logger.info("Finished the process of marking specific employees tasks complete");
    }

    /* --- Internal Methods --- */

    @SuppressWarnings("unchecked")
    private ImmutableSortedMap<Integer, PersonnelTask> getTaskMap() {
        taskCache.acquireReadLockOnKey(TASK_MAP_KEY);
        Element result = taskCache.get(TASK_MAP_KEY);
        taskCache.releaseReadLockOnKey(TASK_MAP_KEY);
        final ImmutableSortedMap<Integer, PersonnelTask> taskMap;
        if (result == null) {
            taskMap = loadTaskMap();
            taskCache.acquireWriteLockOnKey(TASK_MAP_KEY);
            taskCache.put(new Element(TASK_MAP_KEY, taskMap));
            taskCache.releaseWriteLockOnKey(TASK_MAP_KEY);
        } else {
            taskMap = (ImmutableSortedMap<Integer, PersonnelTask>) result.getObjectValue();
        }
        return taskMap;
    }

    private ImmutableSortedMap<Integer, PersonnelTask> loadTaskMap() {
        return taskDao.getAllTasks().stream()
                .map(this::getDetailedTask)
                .collect(ImmutableSortedMap.toImmutableSortedMap(
                        Comparable::compareTo, PersonnelTask::getTaskId, Function.identity())
                );
    }

    private PersonnelTask getDetailedTask(PersonnelTask basicTask) {
        return taskDetailDaoMap.get(basicTask.getTaskType())
                .getTaskDetails(basicTask);
    }
}
