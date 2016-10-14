<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="EmpRecordHistoryCtrl">
    <div class="time-attendance-hero">
        <h2>Employee Attendance History</h2>
    </div>
    <div class="content-container content-controls">
        <p class="content-info">View Attendance Records for Employee &nbsp;
            <select ng-model="state.selectedEmp" ng-if="state.allEmps.length > 0"
                    ng-init="state.selectedEmp = state.selectedEmp || state.allEmps[0]"
                    ng-change="getTimeRecordsForEmp(state.selectedEmp)"
                    ng-options="emp.dropDownLabel group by emp.group for emp in state.allEmps">
            </select>
        </p>
    </div>

    <div loader-indicator class="loader" ng-show="state.searching"></div>
    <section class="content-container" ng-hide="state.searching">
        <%--<div ng-show="state.recordYears.length == 0">--%>
            <%--<ess-notification level="warn" title="No time records were found for {{state.selectedEmp.empLastName}}"></ess-notification>--%>
        <%--</div>--%>
        <div ng-show="state.recordYears.length > 0">
            <h1>{{state.selectedEmp.empLastName}}'s Attendance Records</h1>
            <div class="content-controls">
                <p class="content-info" style="margin-bottom:0;">
                    View attendance records for year &nbsp;
                    <select ng-model="state.selectedRecYear"
                            ng-change="getRecordsForYear(state.selectedEmp, state.selectedRecYear)"
                            ng-options="year for year in state.recordYears">
                    </select>
                </p>
            </div>
            <ess-notification level="warn" title="No Employee Records For {{state.selectedRecYear}}"
                              ng-hide="state.records.length > 0">
                <p>
                    It appears as if the employee has no records for the selected year.<br>
                    Please contact Senate Personnel at (518) 455-3376 if you require any assistance.
                </p>
            </ess-notification>
            <div ng-show="state.records.length > 0">
                <p class="content-info">
                    Time records that have been submitted for pay periods during
                    {{state.selectedRecYear}} are listed in the table below.
                    <br/>
                    You can view details about each pay period by clicking the row.
                    <span ng-show="state.paperTimesheetsDisplayed">
                        <br>
                        <span class="bold">Note:</span>
                        Details are unavailable for attendance records entered via paper timesheet (designated by "(paper)" under Status)
                    </span>
                </p>
                <div class="padding-10">
                    <table id="attendance-history-table" ng-show="!state.searching" class="ess-table attendance-listing-table">
                        <thead>
                        <tr>
                            <th>Date Range</th>
                            <th>Pay Period</th>
                            <th>Status</th>
                            <th>Work</th>
                            <th>Holiday</th>
                            <th>Vacation</th>
                            <th>Personal</th>
                            <th>Sick Emp</th>
                            <th>Sick Fam</th>
                            <th>Misc</th>
                            <th>Total</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-repeat="record in state.records" ng-click="showDetails(record)"
                            ng-class="{'e-timesheet': !record.paperTimesheet}"
                            title="{{record.paperTimesheet ? '' : 'Click to view record details'}}">
                            <td>{{record.beginDate | moment:'l'}} - {{record.endDate | moment:'l'}}</td>
                            <td>{{record.payPeriod.payPeriodNum}}</td>
                            <td>
                                <span ng-bind-html="record.recordStatus | timeRecordStatus:true"></span>
                                <span ng-show="record.paperTimesheet">(paper)</span>
                            </td>
                            <td>{{record.totals.workHours}}</td>
                            <td>{{record.totals.holidayHours}}</td>
                            <td>{{record.totals.vacationHours}}</td>
                            <td>{{record.totals.personalHours}}</td>
                            <td>{{record.totals.sickEmpHours}}</td>
                            <td>{{record.totals.sickFamHours}}</td>
                            <td>{{record.totals.miscHours}}</td>
                            <td>{{record.totals.total}}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </section>
    <div modal-container ng-show="top" ng-switch="top">
        <div record-detail-modal ng-if="isOpen('details')"></div>
    </div>
</section>

