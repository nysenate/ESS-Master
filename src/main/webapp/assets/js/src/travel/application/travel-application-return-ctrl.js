var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationReturnCtrl', ['$scope', 'AddressCountyService', returnCtrl]);

function returnCtrl($scope, countyService) {

    $scope.return = {
        form: {}
    };

    $scope.route = angular.copy($scope.app.route);

    if ($scope.route.returnLegs.length === 0) {
        // Init return leg
        var segment = new Segment();
        segment.from = angular.copy($scope.route.outboundLegs[$scope.route.outboundLegs.length - 1].to);
        segment.to = angular.copy($scope.route.outboundLegs[0].from);
        // If only 1 outbound mode of transportation, initialize to that.
        if ($scope.numDistinctModesOfTransportation($scope.app) === 1) {
            segment.modeOfTransportation = angular.copy($scope.route.outboundLegs[0].modeOfTransportation);
        }
        $scope.route.returnLegs.push(segment);
    }

    $scope.addSegment = function () {
        // When adding a new segment, mark the form as unsubmitted if there are not any errors.
        // This prevents the error notification from being display for errors in the new segment
        // which the user has not had a chance to fill out yet.
        if ($scope.return.form.$valid) {
            $scope.return.form.$submitted = false;
        }

        // Initialize new leg
        var segment = new Segment();
        var prevSeg = $scope.route.returnLegs[$scope.route.returnLegs.length - 1];
        segment.from = prevSeg.to;
        segment.to = angular.copy($scope.route.outboundLegs[0].from);
        segment.modeOfTransportation = prevSeg.modeOfTransportation;
        segment.isMileageRequested = prevSeg.isMileageRequested;
        segment.isMealsRequested = prevSeg.isMealsRequested;
        segment.isLodgingRequested = prevSeg.isLodgingRequested;
        $scope.route.returnLegs.push(segment);
        console.log($scope.app);
    };

    $scope.setFromAddress = function (leg, address) {
        leg.from = address;
    };

    $scope.setToAddress = function (leg, address) {
        leg.to = address;
    };

    $scope.isLastSegment = function (index) {
        return $scope.route.returnLegs.length - 1 === index;
    };

    $scope.deleteSegment = function () {
        $scope.route.returnLegs.pop();
    };

    // Ensure user does not select a return travel date before the outbound travel date.
    $scope.fromDate = function () {
        return $scope.route.outboundLegs.slice(-1)[0].travelDate;
    };

    $scope.submit = function () {
        for (var prop in $scope.return.form) {
            // Set all form elements as touched so they can be styled appropriately if they have errors.
            if ($scope.return.form[prop] && typeof($scope.return.form[prop].$setTouched) === 'function') {
                $scope.return.form[prop].$setTouched();
            }
        }
        // If the entire form is valid, check for counties and continue to next page.
        if ($scope.return.form.$valid) {
            var addrsMissingCounty = findAddressesWithoutCounty();

            if (addrsMissingCounty.isEmpty) {
                $scope.continue();
            }
            else {
                $scope.openLoadingModal();
                countyService.updateWithGeocodeCounty(addrsMissingCounty)
                    .then(countyService.addressesMissingCounty) // filter out addresses that were updated with a county.
                    .then(countyService.promptUserForCounty)
                    .then($scope.closeLoadingModal)
                    .then($scope.continue);
            }
        }

        function findAddressesWithoutCounty() {
            var addresses = $scope.route.returnLegs.map(function (leg) {
                return leg.from;
            }).concat($scope.route.returnLegs.map(function (leg) {
                return leg.to;
            }));
            return countyService.addressesMissingCounty(addresses);
        }
    };

    $scope.continue = function () {
        $scope.returnCallback($scope.ACTIONS.NEXT, $scope.route);
    }
}
