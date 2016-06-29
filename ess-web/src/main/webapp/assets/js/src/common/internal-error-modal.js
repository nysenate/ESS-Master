var essApp = angular.module('ess');

essApp.directive('internalErrorModal', ['modals',
function (modals) {
    return {
        template:
        '<section id="internal-error-modal" title="Internal Error">' +
            '<h1>Internal Error</h1>' +
            '<p class="internal-error-text">' +
                'We are sorry to report that an error occurred on the ESS server while processing your request.<br>' +
                'Please contact Personnel at (518) 455-3376 and notify us of this issue so that we can fix it!' +
            '</p>' +
            '<pre class="internal-error-details" ng-show="showDetails">{{details | json}}</pre>' +
            '<div class="button-container">' +
                '<input type="button" class="reject-button" ng-click="showDetails = !showDetails" value="{{showDetails ? \'Hide\' : \'Show\'}} Details"/>' +
                '<input type="button" class="reject-button" ng-click="close()" value="OK"/>' +
            '</div>' +
        '</section>',
        link: function ($scope, $element, $attrs) {
            $scope.showDetails = false;
            $scope.details = modals.params().details;

            $scope.close = modals.reject;
        }
    };
}]);
