var essTravel = angular.module('essTravel');

essTravel.directive('appFormReviewModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review/app-form-review-modal',
        scope: {},
        controller: 'AppFormReviewCtrl'
    }
}])
    .controller('AppFormReviewCtrl', ['$scope', 'modals', 'ApplicationReviewApi', appFormReviewCtrl]);

function appFormReviewCtrl($scope, modals, appReviewApi) {

    $scope.appReview = modals.params();

    $scope.approve = function () {
        appReviewApi.approve($scope.appReview.appReviewId);
        modals.resolve();
    };

    $scope.disapprove = function () {
        appReviewApi.disapprove($scope.appReview.appReviewId);
        modals.resolve();
    };

    $scope.exit = function () {
        modals.reject();
    };
}