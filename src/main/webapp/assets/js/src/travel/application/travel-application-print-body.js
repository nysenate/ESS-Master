var travel = angular.module('essTravel');

travel.directive('travelAppPrintBody', ['appProps', 'TravelModeOfTransportationApi', function (appProps, motApi) {
    return {
        restrict: 'E',
        scope: {
            app: '='
        },
        templateUrl: appProps.ctxPath + '/template/travel/application/travel-application-print-body',
        link: function ($scope, $elem, $attrs) {

            console.log($scope.app);
            $scope.NOT_AVAILABLE = "N/A";

            $scope.containsMot = function (mot) {
                var appModesOfTransportation = [];
                $scope.app.route.outboundLegs.forEach(function (leg) {
                    appModesOfTransportation.push(leg.methodOfTravelDisplayName);
                });

                return appModesOfTransportation.includes(mot.displayName);
            };

            function init() {
                motApi.get().$promise
                    .then(extractMots)
                    .catch($scope.handleErrorResponse);

                function extractMots(response) {
                    $scope.modeOfTransportations = response.result;
                }
            }

            init();
        }
    }
}]);
