var essTravel = angular.module('essTravel');

/**
 * The Main/Parent controller for the travel application form.
 * This controller controls data updates and the page of the application.
 *
 * Each page of the application is implemented as its own directive (defined below).
 * These page directives don't modify the application directly, but call callback functions
 * that are defined in this Parent controller.
 */
essTravel.controller('NewApplicationCtrl',
                     ['$scope', '$window', 'appProps', 'modals', 'LocationService', 'AppEditStateService', 'UnsubmittedAppApi', 'TravelApplicationByIdApi', travelAppController]);

function travelAppController($scope, $window, appProps, modals, locationService, stateService, unsubmittedAppApi, appIdApi) {

    $scope.stateService = stateService;
    // Common data shared between all child controllers.
    $scope.data = {
        app: undefined
    };

    this.$onInit = function () {
        $scope.stateService = stateService;
        $scope.stateService.setPurposeState();
        initApplication(appProps.user.employeeId);

        /**
         * Checks to see if the logged in employee has an uncompleted travel application in progress.
         * If so, return that application so they can continue to work on it.
         * If not, initialize a new application.
         *
         * If continuing a previous application, display modal asking user if they
         * would like to continue working on it or start over.
         * @param travelerId
         */
        function initApplication(travelerId) {
            unsubmittedAppApi.get({userId: appProps.user.employeeId, travelerId: travelerId}, {}, function (response) {
                $scope.data.app = response.result;
                if (hasUncompleteApplication()) {
                    modals.open('ess-continue-saved-app-modal')
                        .catch(function () { // Restart application on modal rejection.
                            cancelApplication($scope.data.app) // TODO replace this functionality
                        });
                }
                console.log($scope.data.app.id);
            }, $scope.handleErrorResponse);

            function hasUncompleteApplication() {
                return $scope.data.app.purposeOfTravel !== "";
            }
        }
    };

    $scope.savePurpose = function (app) {
        console.log(app);
        unsubmittedAppApi.update({userId: appProps.user.employeeId, travelerId: app.traveler.employeeId}, {purposeOfTravel: app.purposeOfTravel}, function (response) {
            $scope.data.app = response.result;
            stateService.setOutboundState();
        }, $scope.handleErrorResponse)
    };

    $scope.saveOutbound = function (app) {
        $scope.data.app.route.outboundLegs = app.route.outboundLegs;
        stateService.setReturnState();
    };

    $scope.saveRoute = function (app) {
        $scope.openLoadingModal();
        unsubmittedAppApi.update({userId: appProps.user.employeeId, travelerId: app.traveler.employeeId}, {route: JSON.stringify(app.route)}, function (response) {
            $scope.data.app = response.result;
            stateService.setAllowancesState();
            $scope.closeLoadingModal();
        }, function (error) {
            $scope.closeLoadingModal();
            if (error.status === 502) {
                $scope.handleDataProviderError();
            } else {
                $scope.handleErrorResponse(error);
            }
        });
    };

    $scope.saveAllowances = function (app) {
        var patches = {
            allowances: JSON.stringify(app.allowances),
            mealPerDiems: JSON.stringify(app.mealPerDiems),
            lodgingPerDiems: JSON.stringify(app.lodgingPerDiems),
            mileagePerDiems: JSON.stringify(app.mileagePerDiems)
        };
        unsubmittedAppApi.update({userId: appProps.user.employeeId, travelerId: app.traveler.employeeId}, patches, function (response) {
            $scope.data.app = response.result;
            stateService.setReviewState();
        }, $scope.handleErrorResponse)
    };

    $scope.submitApplication = function (app) {
        modals.open('submit-confirm')
            .then(function () {
                modals.open("submit-progress");
                unsubmittedAppApi.update({userId: appProps.user.employeeId, travelerId: app.traveler.employeeId}, {action: "submit"}).$promise
                    .then(function (response) {
                        $scope.data.app = response.result;
                        modals.resolve({});
                    })
                    .then(function () {
                        modals.open("submit-results").then(function () {
                            locationService.go("/travel", true);
                        });
                    })
                    .catch($scope.handleErrorResponse);
            })
    };

    $scope.cancel = function (app) {
        modals.open("cancel-application").then(function () {
            modals.resolve({});
            $scope.openLoadingModal();
            cancelApplication(app);
        });
    };

    $scope.toPurposeState = function (app) {
        $scope.stateService.setPurposeState();
    };

    $scope.toOutboundState = function (app) {
        $scope.stateService.setOutboundState();
    };

    $scope.toReturnState = function (app) {
        $scope.stateService.setReturnState();
    };

    $scope.toAllowancesState = function (app) {
        $scope.stateService.setAllowancesState();
    };

    function cancelApplication(app) {
        unsubmittedAppApi.remove({userId: appProps.user.employeeId, travelerId: app.traveler.employeeId})
            .$promise
            .then(reload)
            .catch($scope.handleErrorResponse)
    }

    function reload() {
        locationService.go("/travel/application/new", true);
    }

    $scope.openLoadingModal = function () {
        modals.open('loading');
    };

    $scope.closeLoadingModal = function () {
        if (modals.isTop('loading')) {
            modals.resolve();
        }
    };

    $scope.handleDataProviderError = function () {
        modals.open("external-api-error")
            .then(function () {
                reload();
            })
            .catch(function () {
                locationService.go("/logout", true);
            });
    };

    /**
     * Error handler for google maps api.
     * Docs: https://developers.google.com/maps/documentation/javascript/events#auth-errors
     * and https://developers.google.com/maps/documentation/javascript/error-messages
     */
    $window.gm_authFailure = function () {
        $scope.$apply(function () {
            $scope.handleErrorResponse("Google maps api authentication error.");
        });
    }
}
