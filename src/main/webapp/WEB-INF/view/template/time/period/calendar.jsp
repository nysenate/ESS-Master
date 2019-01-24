<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section ng-controller="PayPeriodCalendarCtrl">
    <div class="time-attendance-hero">
        <h2>Payroll Calendar</h2>
    </div>
    <div class="content-container content-controls">
        <p class="content-info">
            Year &nbsp;
            <select ng-model="state.year" ng-options="year for year in yearList"></select>
        </p>
    </div>

    <div class="content-container pay-period-cal-container">
        <div class="content-info legend-container">
            <div class="legend-block pay-period-cal-pay-period-end">&nbsp;</div>Pay Period End Date
            <div class="legend-block pay-period-cal-holiday">&nbsp;</div>Senate Holiday
            <div class="legend-block pay-period-cal-partial-holiday">&nbsp;</div>Partial Senate Holiday
        </div>
        <div class="pay-period-cal">
            <div ng-repeat="(i, month) in months" class="pay-period-month">
                <div datepicker
                     step-months="false" default-date="{{month}}" inline="false"
                     before-show-day="periodHighlight()"></div>
            </div>
        </div>
    </div>
    <div modal-container></div>
</section>