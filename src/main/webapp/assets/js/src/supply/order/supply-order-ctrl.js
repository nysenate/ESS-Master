var essSupply = angular.module('essSupply')
    .controller('SupplyOrderController', ['$scope', 'appProps', 'LocationService', 'SupplyCartService',
        'PaginationModel', 'SupplyLocationAutocompleteService', 'SupplyLocationAllowanceService',
        'SupplyOrderDestinationService', 'modals', 'SupplyUtils', supplyOrderController]);

function supplyOrderController($scope, appProps, locationService, supplyCart, paginationModel, locationAutocompleteService,
                               allowanceService, destinationService, modals, supplyUtils) {
    $scope.state = {};
    $scope.sorting = {
        Name: 0,
        Category: 10
    };
    $scope.sortBy = $scope.sorting.Alphabet;
    $scope.states = {
        LOADING: 0,
        SELECTING_DESTINATION: 5,
        SHOPPING: 10
    };
    $scope.displaySorting = Object.getOwnPropertyNames($scope.sorting);

    $scope.paginate = angular.extend({}, paginationModel);

    $scope.filter = {
        searchTerm: "",
        categories: []
    };

    // All allowances for the selected destination.
    var allowances = [];

    // An array of allowances which match the current filters.
    $scope.displayAllowances = [];

    // The user specified destination code. Defaults to the code of the employees work location.
    $scope.destinationCode = "";

    $scope.destinationDescription = "";

    /** --- Initialization --- */

    $scope.init = function () {
        $scope.state = $scope.states.LOADING;
        $scope.paginate.itemsPerPage = 16;
        updateFiltersFromUrlParams();
        if (!destinationService.isDestinationConfirmed()) {
            loadSelectDestinationState();
        }
        else {
            loadShoppingState();
        }

    };

    $scope.init();

    /** --- State --- */

    function loadSelectDestinationState() {
        locationAutocompleteService.initWithResponsibilityHeadLocations()
            .then(destinationService.queryDefaultDestination)
            .then(setDestinationCode)
            .then(setToSelectingDestinationState);
    }

    function setDestinationCode() {
        $scope.destinationCode = destinationService.getDefaultCode();
    }

    function setDestinationDescription() {
        $scope.destinationDescription = destinationService.getDestination().locationDescription || "";
    }

    function setToSelectingDestinationState() {
        $scope.state = $scope.states.SELECTING_DESTINATION;
    }

    function loadShoppingState() {
        $scope.state = $scope.states.LOADING;
        $scope.destinationCode = destinationService.getDestination().code; // Too much coupling with validator. If this is put in promise, errors occur.
        allowanceService.queryLocationAllowance(destinationService.getDestination())
            .then(saveAllowances)
            .then(filterAllowances)
            .then(setToShoppingState)
            .then(setDestinationDescription)
            .then(checkSortOrder);
    }

    function checkSortOrder(allowance) {
        $scope.updateSort();
    }
    function saveAllowances(allowanceResponse) {
        allowances = allowanceResponse.result.itemAllowances;
    }

    function filterAllowances() {
        $scope.displayAllowances = allowanceService.filterAllowances(allowances, $scope.filter.categories, $scope.filter.searchTerm);
        $scope.displayAllowances = supplyUtils.alphabetizeAllowances($scope.displayAllowances);
    }

    function Reset() {
        $scope.filter.searchTerm = "";
        filterAllowances();
    }

    function setToShoppingState() {
        $scope.state = $scope.states.SHOPPING;
    }

    /** --- Search --- */

    $scope.search = function () {
        filterAllowances();
    };

    /** --- Reset --- */
    $scope.reset = function () {
        Reset();
    };

    /** --- Navigation --- */

    /**
     * Synchronizes the categories and currPage objects with the values in the url.
     */
    function updateFiltersFromUrlParams() {
        $scope.filter.categories = locationService.getSearchParam("category") || [];
        $scope.paginate.currPage = locationService.getSearchParam("page") || 1;
        // Set page param. This ensures it gets set to 1 if it was never previously set.
        locationService.setSearchParam("page", $scope.paginate.currPage, true, true);
    }

    /**
     * Set the page url parameter when the user changes the page.
     * Triggers the $on('$locationChangeStart') event which will update url params and filter allowances.
     */
    $scope.onPageChange = function () {
        locationService.setSearchParam("page", $scope.paginate.currPage, true, false);
    };

    /**
     * Detect url param changes due to category side bar selections, page change, or back/forward browser navigation.
     * Update local $scope params to match the new url params and filter allowances for any categories specified.
     */
    $scope.$on('$locationChangeStart', function (event, newUrl) {
        if (newUrl.indexOf(appProps.ctxPath + "/supply/order") > -1) { // If still on order page.
            updateFiltersFromUrlParams();
            filterAllowances();
        }
    });

    /** --- Shopping --- */

    $scope.addToCart = function (allowance) {
        // If more is selected, display
        if (allowance.selectedQuantity === "more") {
            $scope.quantityChanged(allowance);
            return;
        }
        if (isNaN(allowance.selectedQuantity)) {
            return;
        }
        // Cant add more than is allowed per order.
        if (supplyCart.isOverOrderAllowance(allowance.item, allowance.selectedQuantity)) {
            return;
        }
        // first time adding special item, display modal.
        if (!supplyCart.isItemInCart(allowance.item.id) && allowance.visibility === 'SPECIAL') {
            modals.open('special-order-item-modal', {allowance: allowance});
        }
        else {
            supplyCart.addToCart(allowance.item, allowance.selectedQuantity);
        }
    };

    $scope.isInCart = function (item) {
        return supplyCart.isItemInCart(item.id) && supplyCart.getCartLineItem(item.id).quantity == 1;
    };
    $scope.isDuplicated = function (item) {
        return supplyCart.isItemInCart(item.id) && supplyCart.getCartLineItem(item.id).quantity > 1;
    };

    $scope.getAllowedQuantities = function (item) {
        var allowedQuantities = allowanceService.getAllowedQuantities(item);
        allowedQuantities.push("more");
        return allowedQuantities;
    };

    /** This is called whenever an items quantity is changed.
     * Used to determine when "more" is selected. */
    $scope.quantityChanged = function (allowance) {
        if (allowance.selectedQuantity === "more") {
            modals.open('order-more-prompt-modal', {allowance: allowance})
                .then(function (allowance) {
                    modals.open('order-custom-quantity-modal', {item: allowance.item});
                });
        }
    };

    /** --- Location selection --- */

    $scope.confirmDestination = function () {
        var success = destinationService.setDestination($scope.destinationCode);
        if (success) {
            loadShoppingState();
        }
    };
    $scope.$on('$locationChangeStart', function (event, newUrl) {
        $scope.updateSort();
    });

    $scope.getLocationAutocompleteOptions = function () {
        return locationAutocompleteService.getLocationAutocompleteOptions();
    };

    $scope.resetDestination = function (body) {
        supplyCart.reset();
        destinationService.reset();
        locationService.go("/supply/order", true);
    };

    $scope.backHidden = function () {
        return $scope.state == $scope.states.SELECTING_DESTINATION;
    };

    /** --- Sorting  --- */
    $scope.updateSort = function () {
        var cur = locationService.getSearchParam("sortBy") || [];
        if (cur.length == 0 || cur[0] != $scope.sortBy) {
            locationService.setSearchParam("sortBy", $scope.sortBy, true, false);
        }
        var allowancesCopy = angular.copy($scope.displayAllowances);
        if ($scope.sorting[$scope.sortBy] == $scope.sorting.Name) {
            allowancesCopy.sort(function (a, b) {
                if (a.item.description < b.item.description) return -1;
                if (a.item.description > b.item.description) return 1;
                return 0;
            });
        }
        else if ($scope.sorting[$scope.sortBy] == $scope.sorting.Category) {
            allowancesCopy.sort(function (a, b) {
                if (a.item.category.name < b.item.category.name) return -1;
                if (a.item.category.name > b.item.category.name) return 1;
                return 0;
            });
        }
        $scope.displayAllowances = allowancesCopy;
    }
}

/**
 * Directive for validating destination selection.
 */
essSupply.directive('destinationValidator', ['SupplyLocationAutocompleteService', function (locationAutocompleteService) {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.destination = function (modelValue, viewValue) {
                return locationAutocompleteService.isValidCode(modelValue) || modelValue.length === 0;
            }
        }
    }
}]);

/**
 * Validator for the special order quantity form.
 * Note: The form considers 'e' valid input. I would like to use this validator to mark 'e' as invalid, however
 * this validator is not being called when 'e' characters are entered...
 */
essSupply.directive('wholeNumberValidator', [function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.wholeNumber = function (modelValue, viewValue) {
                return modelValue % 1 === 0 && modelValue !== null;
            };
        }
    }
}]);