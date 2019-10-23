var essTravel = angular.module('essTravel');

essTravel.controller('UserAppsCtrl', ['$scope', 'appProps', 'modals', 'TravelApplicationsForTravelerApi', 'PaginationModel', viewApplicationsCtrl]);

function viewApplicationsCtrl($scope, appProps, modals, travelerAppApi) {

    var DATE_FORMAT = "MM/DD/YYYY";

    $scope.apps = {
        all: [],
        filtered: []
    };

    $scope.appRequest = {};

    $scope.date = {
        from: moment().subtract(1, 'month').format(DATE_FORMAT),
        to: moment().add(6, 'month').format(DATE_FORMAT)
    };

    $scope.init = function() {
        fetchApplications(appProps.user.employeeId);
    };

    function fetchApplications(empId) {
        $scope.appRequest = travelerAppApi.get({travelerId: empId}, onSuccess, $scope.handleErrorResponse);

        function onSuccess (resp) {
            parseResponse(resp);
            $scope.applyFilters();
            console.log($scope.apps.all);
        }

        function parseResponse(resp) {
            var result = resp.result;
            for (var i = 0; i < result.length; i++) {
                $scope.apps.all.push(result[i]);
            }
        }
    }

    $scope.applyFilters = function () {
        $scope.apps.filtered = angular.copy($scope.apps.all);
        $scope.apps.filtered = $scope.apps.filtered.filter(function (app) {
            return Date.parse(app.startDate) >= Date.parse($scope.date.from) &&
                Date.parse(app.startDate) <= Date.parse($scope.date.to)
        });
    };

    $scope.viewApplicationForm = function(app) {
        modals.open("app-form-view-modal", app, true)
            .catch(function() {})
    };

    $scope.init();
}