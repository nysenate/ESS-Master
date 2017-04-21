angular.module('essTime')
    .filter('entryHours', [entryHoursFilter])
    .directive('timeRecordInput', [timeRecordInputDirective])
    .directive('recordDetails', ['appProps', 'modals', 'AccrualPeriodApi', recordDetailsDirective])
    .directive('recordDetailModal', ['modals', recordDetailModalDirective])
;

/* --- Filters */

/**
 * Returns hour value if input is a valid number, "--" otherwise
 */
function entryHoursFilter () {
    var unenteredValue = "--";
    return function (value) {
        if (isNaN(parseInt(value))) {
            return unenteredValue;
        }
        return value;
    }
}

/* --- Directives --- */

function timeRecordInputDirective () {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.on('focus', function(event){
                $(this).parent().parent().addClass("active");
            });
            element.on('blur', function(event){
                $(this).parent().parent().removeClass("active");
            });
        }
    }
}

/**
 * A table that displays details for a specific time record
 */
function recordDetailsDirective(appProps, modals, accrualApi) {
    return {
        scope: {
            record: '='
        },
        templateUrl: appProps.ctxPath + '/template/time/record/details',
        link: function($scope, $elem, $attrs) {
            var showAccrualsSelected = $attrs['showAccruals'] === "true";
            $scope.close = modals.reject;
            $scope.showExitBtn = $attrs['exitBtn'] !== "false";
            $scope.showAccruals = showAccrualsSelected;
            $scope.loadingAccruals = false;

            $scope.$watch('record', function (record) {
                if (record) {
                    detectPayTypes();
                    loadAccruals();
                }
            });

            function detectPayTypes() {
                angular.forEach($scope.record.timeEntries, function (entry) {
                    $scope.tempEntries = $scope.annualEntries = false;
                    $scope.tempEntries = $scope.tempEntries || entry.payType === 'TE';
                    $scope.annualEntries = $scope.annualEntries || ['RA', 'SA'].indexOf(entry.payType) > -1;
                    $scope.showAccruals = showAccrualsSelected && $scope.annualEntries;
                });
            }

            function loadAccruals() {
                if (!$scope.showAccruals) {
                    return;
                }
                var record = $scope.record;
                console.log(record);
                var empId = record.employeeId;
                var recordStartDate = moment(record.beginDate);
                var params = {
                    empId: empId,
                    beforeDate: recordStartDate.format('YYYY-MM-DD')
                };
                $scope.loadingAccruals = true;
                return accrualApi.get(params,
                    function (resp) {
                        if (resp.success) {
                            $scope.accrual = resp.result;
                            console.log($scope.accrual);
                        }
                    }, function (resp) {
                        modals.open('500', {details: resp});
                        console.error(resp);
                    }).$promise.finally(function() {
                        $scope.loadingAccruals = false;
                    });
            }
        }
    };
}

/**
 * A modal containing a record-details view
 * @param modals
 * @returns {{template: string, link: link}}
 */
function recordDetailModalDirective(modals) {
    return {
        template: '<div class="record-detail-modal" record-details record="record"></div>',
        link: function ($scope, $elem, $attrs) {
            var params = modals.params();
            $scope.record = params.record;
        }
    }
}

