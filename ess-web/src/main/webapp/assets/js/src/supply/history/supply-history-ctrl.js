essSupply = angular.module('essSupply').controller('SupplyHistoryController',
['$scope', 'SupplyShipmentsApi', 'LocationService', supplyHistoryController]);

function supplyHistoryController($scope, shipmentsApi, locationService) {

    $scope.filter = {
        date: {
            from: moment().subtract(1, 'month').toDate(),
            to: new Date(), // TODO why not moment()
            min: new Date(2016, 1, 1),
            max: moment().format('YYYY-MM-DD') //TODO: max and min not working
        },
        location: {                                         // LocationView object
            locations: null,
            selectedLocations: null
        },
        issuer: {                                           // EmployeeView object
            issuers: null,
            selectedIssuer: null
        }
    };

    $scope.shipments = null;
    $scope.filteredShipments = [];

    // TODO: Temp to get this working for demo.
    $scope.filteredLocations = null;
    $scope.locations = [];
    $scope.selectedIssuer = null;
    $scope.issuers = [];
    // TODO: ---------------------------------

    $scope.init = function() {
        getCompletedOrders();
    };

    $scope.init();

    function getCompletedOrders() {
        var params = {
            status: "COMPLETED",
            from: moment($scope.filter.date.from).format(),
            to: moment($scope.filter.date.to).format()
        };
        shipmentsApi.get(params, function(response) {
            $scope.shipments = response.result;
            $scope.filteredShipments = $scope.shipments;
            $scope.initFilters();
            $scope.selectedLocation = $scope.locations[0];
            $scope.selectedIssuer = $scope.issuers[0];
        }, function(response) {

        })
    }

    // TODO: can't create filters by looping over shipments. Will NOT work once pagination in use.
    // TODO: will need to add location & issuer API with status and date range filters.
    $scope.initFilters = function() {
        $scope.locations.push("All");
        $scope.issuers.push("All");
        angular.forEach($scope.shipments, function (shipment) {
            if ($scope.locations.indexOf(shipment.order.activeVersion.destination.code + "-" + shipment.order.activeVersion.destination.locationTypeCode) === -1) {
                $scope.locations.push(shipment.order.activeVersion.destination.code + "-" + shipment.order.activeVersion.destination.locationTypeCode);
            }
            if ($scope.issuers.indexOf(shipment.activeVersion.issuer.firstName + " " + shipment.activeVersion.issuer.lastName) === -1) {
                $scope.issuers.push(shipment.activeVersion.issuer.firstName + " " + shipment.activeVersion.issuer.lastName);
            }
        });
    };

    /** --- Filter --- */

    $scope.isInFilter = function(order) {
        if ($scope.selectedLocation === "All") {
            return true;
        }
        else {
            if (order.location.code + '-' + order.location.locationTypeCode === $scope.selectedLocation) {
                return true;
            }
        }
        return false;
    };

    /** --- Util methods --- */
    
    // TODO: do we want quantity of items orderd or number of distinct items ordered?
    $scope.getOrderQuantity = function(shipment) {
        var size = 0;
        angular.forEach(shipment.order.activeVersion.lineItems, function(lineItem) {
            size += lineItem.quantity; 
        });
        return size;
    };

    $scope.viewOrder = function(shipment) {
        locationService.go("/supply/requisition/view", false, "order=" + shipment.order.id);
    };
}