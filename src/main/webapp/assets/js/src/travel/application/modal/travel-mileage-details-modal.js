var essTravel = angular.module('essTravel');

essTravel.directive('travelMileageDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-mileage-details-modal',
        controller: 'MileageDetailsModalCtrl'
    }
}])
    .controller('MileageDetailsModalCtrl', ['$scope', 'modals', mileageDetailsModalCtrl]);

function mileageDetailsModalCtrl($scope, modals) {

    $scope.app = modals.params().app;
    $scope.legs = $scope.app.route.outboundLegs.concat($scope.app.route.returnLegs);

    $scope.mileageAllowances = $scope.app.mileageAllowance.outboundAllowances.concat(
        $scope.app.mileageAllowance.returnAllowances);

    $scope.closeModal = function() {
        modals.resolve();
    };
}