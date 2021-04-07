package gov.nysenate.ess.core.service.pec.search;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryBuilder;
import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryCompletionStatus;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class EssEmpTaskSearchService implements EmpTaskSearchService {

    private static final Logger logger = LoggerFactory.getLogger(EssEmpTaskSearchService.class);

    private static final LocalDate placeholderContSrvDate = LocalDate.of(1995, 1, 1);

    private static final Comparator<EmployeeTaskSearchResult> defaultComparator =
            EmpTaskOrderBy.NAME.getComparator();

    private final EmployeeInfoService employeeInfoService;
    private final PersonnelTaskAssignmentDao patDao;

    public EssEmpTaskSearchService(EmployeeInfoService employeeInfoService, PersonnelTaskAssignmentDao patDao) {
        this.employeeInfoService = employeeInfoService;
        this.patDao = patDao;
    }

    @Override
    public PaginatedList<EmployeeTaskSearchResult> searchForEmpTasks(EmpPTAQuery query, LimitOffset limitOffset) {

        List<PersonnelTaskAssignment> tasks = patDao.getTasks(query.getPatQuery());
        ImmutableListMultimap<Integer, PersonnelTaskAssignment> empTaskMap =
                Multimaps.index(tasks, PersonnelTaskAssignment::getEmpId);

        EmployeeSearchBuilder esb = query.getEmpQuery();

        List<EmployeeTaskSearchResult> resultList = empTaskMap.asMap().entrySet().stream()
                .map(e -> new EmployeeTaskSearchResult(
                        employeeInfoService.getEmployee(e.getKey()),
                        e.getValue()
                ))
                .collect(Collectors.toList());

        Comparator<EmployeeTaskSearchResult> comparator = getComparator(query.getSortDirectives());

        resultList = resultList.stream()
                .filter(etsr -> resultMatchesQuery(etsr, query))
                .sorted(comparator)
                .collect(Collectors.toList());

        List<EmployeeTaskSearchResult> limitedResultList = LimitOffset.limitList(resultList, limitOffset);

        return new PaginatedList<>(resultList.size(), limitOffset, limitedResultList);
    }

    /**
     * Determine if a result matches the query.
     * <p>
     * This only covers parameters not filtered in {@link PersonnelTaskAssignmentDao#getTasks(PTAQueryBuilder)}.
     */
    private boolean resultMatchesQuery(EmployeeTaskSearchResult result, EmpPTAQuery query) {
        Employee employee = result.getEmployee();
        EmployeeSearchBuilder employeeQuery = query.getEmpQuery();
        PTAQueryBuilder patQuery = query.getPatQuery();

        return resultEmpMatchesActive(employee, employeeQuery) &&
                resultEmpMatchesRCHC(employee, employeeQuery) &&
                resultEmpMatchesName(employee, employeeQuery) &&
                resultEmpMatchesContSrv(employee, employeeQuery) &&
                resultCompletionStatusMatches(result, patQuery);
    }

    private boolean resultEmpMatchesActive(Employee employee, EmployeeSearchBuilder employeeQuery) {
        return employeeQuery.getActive() == null ||
                employeeQuery.getActive().equals(employee.isActive());
    }

    private boolean resultEmpMatchesRCHC(Employee employee, EmployeeSearchBuilder employeeQuery) {
        return employeeQuery.getRespCtrHeadCodes() == null ||
                employeeQuery.getRespCtrHeadCodes().isEmpty() ||
                employeeQuery.getRespCtrHeadCodes().contains(employee.getRespCenterHeadCode());
    }

    private boolean resultEmpMatchesName(Employee employee, EmployeeSearchBuilder employeeQuery) {
        if (employeeQuery.getName() == null) {
            return true;
        }
        String resultSearchString = normalizeSearchString(
                employee.getLastName() + " " + employee.getFirstName() + " " + employee.getInitial());
        String querySearchString = normalizeSearchString(employeeQuery.getName());
        return resultSearchString.contains(querySearchString);
    }

    private boolean resultEmpMatchesContSrv(Employee employee, EmployeeSearchBuilder employeeQuery) {
        if (employeeQuery.getContinuousServiceFrom() == null && employeeQuery.getContinuousServiceTo() == null) {
            return true;
        }
        LocalDate fromDate = Optional.ofNullable(employeeQuery.getContinuousServiceFrom())
                .orElse(DateUtils.LONG_AGO);
        LocalDate toDate = Optional.ofNullable(employeeQuery.getContinuousServiceTo())
                .orElse(DateUtils.THE_FUTURE);
        Range<LocalDate> dateRange = Range.closed(fromDate, toDate);
        LocalDate senateContServiceDate = Optional.ofNullable(employee.getSenateContServiceDate())
                .orElse(placeholderContSrvDate);
        return dateRange.contains(senateContServiceDate);
    }

    private boolean resultCompletionStatusMatches(EmployeeTaskSearchResult result, PTAQueryBuilder patQuery) {
        PTAQueryCompletionStatus completionStatus = patQuery.getTotalCompletionStatus();
        if (completionStatus == null) {
            return true;
        }

        Predicate<PersonnelTaskAssignment> matchCondition = task -> task.isCompleted() == completionStatus.isCompleted();

        List<PersonnelTaskAssignment> tasks = result.getTasks();

        if (completionStatus.isAll()) {
            return tasks.stream().allMatch(matchCondition);
        } else {
            return tasks.stream().anyMatch(matchCondition);
        }
    }

    private String normalizeSearchString(String searchString) {
        return searchString.replaceAll("[^a-zA-Z ]", "")
                .trim()
                .replaceAll(" +", " ")
                .toLowerCase();
    }

    /**
     * Generate a result comparator by chaining the comparators of the given sort directives.
     * <p>
     * Return a default comparator if no sort directives are passed in.
     *
     * @param sortDirectives {@link List<EmpTaskSort>}
     * @return {@link Comparator<EmployeeTaskSearchResult>}
     */
    private Comparator<EmployeeTaskSearchResult> getComparator(List<EmpTaskSort> sortDirectives) {
        if (sortDirectives == null || sortDirectives.isEmpty()) {
            return defaultComparator;
        }
        Comparator<EmployeeTaskSearchResult> overallComparator = null;
        for (EmpTaskSort sort : sortDirectives) {
            Comparator<EmployeeTaskSearchResult> comparator = sort.getOrderBy().getComparator();
            if (sort.getSortOrder() == SortOrder.DESC) {
                comparator = comparator.reversed();
            }
            if (overallComparator == null) {
                overallComparator = comparator;
            } else {
                overallComparator = overallComparator.thenComparing(comparator);
            }
        }
        return overallComparator;
    }

}
