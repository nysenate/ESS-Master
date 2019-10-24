var essTravel = angular.module('essTravel');

essTravel.controller('ReviewHistoryCtrl', ['$scope', 'LocationService', 'modals', 'ApplicationReviewApi', reviewHistory]);

function reviewHistory($scope, locationService, modals, appReviewApi) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";
    const ISO_FORMAT = "YYYY-MM-DD";

    var vm = this;
    vm.data = {
        isLoading: true,
        reviews: {
            all: [],
            filtered: []
        }
    };
    vm.date = {
        from: moment().subtract(3, 'month').format(DATEPICKER_FORMAT),
        to: moment().add(3, 'month').format(DATEPICKER_FORMAT)
    };

    (function () {
        appReviewApi.reviewHistory()
            .$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (appReviews) {
                appReviews.forEach(function (review) {
                    vm.data.reviews.all.push(review);
                });
                vm.applyFilters();
                vm.data.isLoading = false;
            })
            .catch($scope.handleErrorResponse);
    })();

    vm.displayReviewViewModal = function (review) {
        modals.open("app-review-view-modal", review, true);
    };

    // Called by the app-review-view-modal.
    vm.onEdit = function (reivew) {
        modals.reject();
        locationService.go("/travel/application/edit", true, {appId: review.travelApplication.id});
    };

    vm.applyFilters = function () {
        vm.data.reviews.filtered = angular.copy(vm.data.reviews.all);
        vm.data.reviews.filtered = vm.data.reviews.filtered.filter(function (r) {
            return moment(r.travelApplication.startDate, ISO_FORMAT) >= moment(vm.date.from, DATEPICKER_FORMAT) &&
                moment(r.travelApplication.startDate, ISO_FORMAT) <= moment(vm.date.to, DATEPICKER_FORMAT);
        });
    };
}