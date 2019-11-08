(function () {
    var essTime = angular.module('essTime');

    essTime.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }]);

    essTime.factory('ActiveRequestsApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals/request/supervisor/:supId/active");
    }]);

    essTime.factory('PendingRequestsApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals/request/supervisor/:supId/approval");
    }]);

    essTime.factory('ReviewRequestApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals/request/review/:requestId", {
            requestId: '@requestId',
            action: '@action',
            comment: '@comment'
        });
    }]);

    essTime.controller('RequestApprovalCtrl', ['$scope', '$route', 'appProps', 'ActiveRequestsApi',
                                               'PendingRequestsApi', 'ReviewRequestApi',
                                               'TimeOffRequestListService', 'modals', requestApprovalCtrl]);

    function requestApprovalCtrl($scope, $route, appProps, ActiveRequestsApi, PendingRequestsApi,
                                 ReviewRequestApi, TimeOffRequestListService, modals) {
        $scope.pendingFormat = "pending";
        $scope.approvedFormat = "approved";
        $scope.supId = appProps.user.employeeId;
        $scope.activeRequests = [];   //active requests
        $scope.pendingRequests = [];   //requests that need approval
        $scope.loadingRequests = true;
        $scope.popUp = true;


        $scope.reloadPage = function () {
            $scope.updateLists();
            return $route.reload().$promise.resolve();
        };

        $scope.errorHandler = function () {
            $scope.errmsg = "An error occurred. Possibly because of an invalid supervisor ID";
        };

        $scope.handleActiveResultAndMakePendingCall = function (data) {
            $scope.activeRequests = TimeOffRequestListService.formatData(data);

            /* Make the API call for requests that need approval */
            return PendingRequestsApi.query({supId: $scope.supId}).$promise
        };

        $scope.handlePendingResult = function (data) {
            $scope.pendingRequests = TimeOffRequestListService.formatData(data);
        };

        $scope.hasSelections = function (status) {
            var hasSelections = false;
            if (status === 'SUBMITTED') {
                $scope.pendingRequests.forEach(function (request) {
                    if (request.checked) {
                        hasSelections = true;
                    }
                })
            } else {
                $scope.activeRequests.forEach(function (request) {
                    if (request.checked) {
                        hasSelections = true;
                    }
                })
            }
            return hasSelections;
        };

        $scope.selectAll = function (status) {
            if (status === 'SUBMITTED') {
                $scope.pendingRequests.forEach(function (request) {
                    request.checked = true;
                })
            } else {
                $scope.activeRequests.forEach(function (request) {
                    request.checked = true;
                })
            }
        };

        $scope.selectNone = function (status) {
            if (status === 'SUBMITTED') {
                $scope.pendingRequests.forEach(function (request) {
                    request.checked = false;
                })
            } else {
                $scope.activeRequests.forEach(function (request) {
                    request.checked = false;
                })
            }
        };

        $scope.approveSelected = function () {
            $scope.pendingRequests.forEach(function (request) {
                if (request.checked) {
                    console.log("Approved request #", request.requestId);
                    $scope.updateRequest(request.requestId, 'APPROVE', "");
                }
            });
        };

        $scope.rejectSelected = function () {
            var promises = [];
            $scope.activeRequests.forEach(function (request) {
                if (request.checked) {
                    promises.push($scope.updateRequest(request.requestId, 'DISAPPROVE', ""));
                    console.log("Rejected request #", request.requestId);
                }
            });
            Promise.all(promises).then($route.reload());
        };

        $scope.reviewSelected = function (status) {
            //Status will be either 'SUBMITTED' or 'APPROVED'
            //Turn modal on and include parameter with the requests that are checked
            var selectedRequests = [];
            var alreadyApproved = false;
            if (status === 'SUBMITTED') {
                $scope.pendingRequests.forEach(function (r) {
                    if (r.checked) {
                        selectedRequests.push(r);
                    }
                });
            } else {
                alreadyApproved = true;
                $scope.activeRequests.forEach(function (r) {
                    if (r.checked) {
                        selectedRequests.push(r);
                    }
                });
            }
            //Open the Modal
            var params = {
                requests: selectedRequests,
                alreadyApproved: alreadyApproved
            };
            modals.open('time-off-request-review', params, false);
        };


        //Function to update a request with an API Call
        $scope.updateRequest = function (requestId, action, comment) {
            console.log("RequestId: ", requestId);
            var params = {
                requestId: requestId,
                action: action
            };
            if (comment !== "" && comment != null) {
                params.comment = comment;
            }
            return ReviewRequestApi.save(params).$promise.then(
                function (data) {
                    console.log("Success!: ", data);
                    $route.reload();
                },
                function (data) {
                    console.log("ERROR", data);
                }
            );
        };

        //make the call to the back end to get all active requests for a supervisor's employees
        $scope.updateLists = function () {
            $scope.loadingRequests = true;

            ActiveRequestsApi.query({supId: $scope.supId}).$promise
                .then(function (data) {
                    console.log(data);
                    $scope.handleActiveResultAndMakePendingCall(data)
                        .then(function (data2) {
                            console.log(data2);
                            $scope.handlePendingResult(data2);
                        })
                        .finally(function () {
                            $scope.activeRequests.forEach(function (r) {
                                r.checked = false;
                                console.log(r.checked);
                            });
                            $scope.pendingRequests.forEach(function (r) {
                                r.checked = false;
                            });
                            sortRequests();
                            $scope.loadingRequests = false;
                            console.log("Active: " + $scope.activeRequests);
                            console.log("Pending: " + $scope.pendingRequests);
                        })
                    ;
                })
                .catch($scope.errorHandler());
        };

        /**
         * function to sort the requests by date, from earliest to latest
         */
        function sortRequests() {
            ($scope.activeRequests).sort(function (a, b) {
                if (a.startDate > b.startDate)
                    return 1;
                return -1
            });
            ($scope.pendingRequests).sort(function (a, b) {
                if (a.startDate > b.startDate)
                    return 1;
                return -1
            });
        }
    }
})();
