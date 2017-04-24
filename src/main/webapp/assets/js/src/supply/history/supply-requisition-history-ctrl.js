essSupply = angular.module('essSupply').controller('SupplyHistoryController',
    ['$scope', 'SupplyIssuersApi', 'SupplyRequisitionApi', 'LocationService', 'LocationApi',
        'PaginationModel', 'SupplyUtils', supplyHistoryController]);

function supplyHistoryController($scope, supplyIssuersApi, requisitionApi, locationService, locationApi,
                                 paginationModel, supplyUtils) {
    $scope.paginate = angular.extend({}, paginationModel);
    $scope.loading = true;
    $scope.shipments = null;
    $scope.locations = [];
    $scope.selectedIssuer = null;
    $scope.issuers = [];
    $scope.issuerNameToID = [];
    $scope.filter = {
        date: {
            from: moment().subtract(1, 'month').format("MM/DD/YYYY"),
            to: moment().format("MM/DD/YYYY")
        }
    };

    $scope.init = function () {
        $scope.paginate.itemsPerPage = 12;
        getUpdatedOrders().$promise
            .then(initFilters)
            .then(doneLoading);
    };

    $scope.init();

    function getUpdatedOrders() {
        var params = {
            status: ["APPROVED", "REJECTED"],
            // Only filtering by day so round dates to start/end of day.
            from: moment($scope.filter.date.from).startOf('day').format(),
            to: moment($scope.filter.date.to).endOf('day').format(),
            limit: $scope.paginate.itemsPerPage,
            offset: $scope.paginate.getOffset(),
            location: $scope.selectedLocation,
            issuerId: $scope.issuerNameToID[$scope.selectedIssuer]
        };
        return requisitionApi.get(params, function (response) {
            $scope.shipments = response.result;
            $scope.paginate.setTotalItems(response.total);
        },function (errorResponse) {
            modals.open('500', {details: errorResponse});
            console.error(errorResponse);
        })
    }

    function initFilters() {
        locationApi.get().$promise
            .then(setLocations);
        supplyIssuersApi.get().$promise
            .then(setIssuers);
    }

    /** Updates displayed requisitions when a filter is changed. */
    $scope.onFilterChange = function () {
        $scope.loading = true;
        $scope.paginate.reset();
        getUpdatedOrders().$promise
            .then(doneLoading).catch(
                function (errorResponse) {
            modals.open('500', {details: errorResponse});
            console.error(errorResponse);
        });
    };

    /** Updates the displayed requisitions whenever the page is changed. */
    $scope.onPageChange = function () {
        $scope.loading = true;
        getUpdatedOrders().$promise
            .then(doneLoading).catch(
            function (errorResponse) {
                modals.open('500', {details: errorResponse});
                console.error(errorResponse);
            }
        );
    };

    var setLocations = function (response) {
        response.result.forEach(function (e) {
            $scope.locations.push(e.locId);
        });
        sortCodes($scope.locations);
        $scope.locations.unshift("All");
        $scope.selectedLocation = $scope.locations[0];
    };

    var setIssuers = function (response) {
        response.result.forEach(function (e) {
            $scope.issuers.push(e.fullName);
            $scope.issuerNameToID[e.fullName] = e.employeeId;
        });
        $scope.issuers.unshift("All");
        $scope.selectedIssuer = $scope.issuers[0];
    };

    var sortCodes = function (codes) {
        codes.sort(function (a, b) {
            if (a < b) return -1;
            if (a > b) return 1;
            return 0;
        })
    };

    /** --- Util methods --- */

    $scope.distinctItemQuantity = function (requisition) {
        return supplyUtils.countDistinctItemsInRequisition(requisition);
    };

    function doneLoading() {
        $scope.loading = false;
    }

    $scope.viewRequisition = function (shipment) {
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + shipment.requisitionId + "&fromPage=requisitionhistory");
    };
}