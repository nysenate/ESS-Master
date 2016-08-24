var essSupply = angular.module('essSupply')
    .directive('fulfillmentEditingModal', ['appProps', function (appProps) {
        return {
            templateUrl: appProps.ctxPath + '/template/supply/manage/fulfillment/modal/fulfillment-editing-modal',
            scope: {
                'supplyEmployees': '=',
                'locationStatistics': '='
            },
            controller: 'FulfillmentEditingModal',
            controllerAs: 'ctrl'
        };
    }])
    .directive('editableOrderListing', ['appProps', function (appProps) {
        return {
            restrict: 'A',
            scope: false,
            templateUrl: appProps.ctxPath + '/template/supply/manage/fulfillment/modal/editable-order-listing'
        }
    }])
    .controller('FulfillmentEditingModal', ['$scope', 'appProps', 'modals', 'SupplyRequisitionByIdApi',
        'SupplyLocationAutocompleteService', 'SupplyItemAutocompleteService', fulfillmentEditingModal]);

function fulfillmentEditingModal($scope, appProps, modals, requisitionApi,
                                 locationAutocompleteService, itemAutocompleteService) {
    $scope.dirty = false;
    $scope.originalRequisition = {};
    $scope.editableRequisition = {};
    $scope.newLocationCode = ""; // model for editing the location.
    $scope.newItemCommodityCode = ""; // model for adding an item.
    $scope.displayRejectInstructions = false;

    $scope.init = function () {
        $scope.originalRequisition = modals.params();
        $scope.originalRequisition.note = ""; // Reset note so new note can be added. // TODO: remove this 'feature'?
        $scope.editableRequisition = angular.copy($scope.originalRequisition);
        $scope.newLocationCode = $scope.editableRequisition.destination.code;
        itemAutocompleteService.initWithAllItems();
    };

    $scope.init();

    /** Close the modal and return the promise resulting from calling the save requisition api. */
    $scope.saveChanges = function () {
        modals.resolve(requisitionApi.save({id: $scope.originalRequisition.requisitionId}, $scope.editableRequisition).$promise);
    };

    $scope.closeModal = function () {
        modals.reject();
    };

    $scope.processOrder = function () {
        $scope.editableRequisition.status = 'PROCESSING';
        $scope.editableRequisition.processedDateTime = moment().format('YYYY-MM-DDTHH:mm:ss.SSS');
        if ($scope.editableRequisition.issuer === null) {
            setIssuerToLoggedInUser();
        }
        $scope.saveChanges();
    };

    function setIssuerToLoggedInUser() {
        angular.forEach($scope.supplyEmployees, function (emp) {
            if (emp.employeeId === appProps.user.employeeId) {
                $scope.editableRequisition.issuer = emp
            }
        })
    }

    $scope.completeOrder = function () {
        $scope.editableRequisition.status = 'COMPLETED';
        $scope.editableRequisition.completedDateTime = moment().format('YYYY-MM-DDTHH:mm:ss.SSS');
        $scope.saveChanges();
    };

    $scope.approveShipment = function () {
        $scope.editableRequisition.status = 'APPROVED';
        $scope.editableRequisition.approvedDateTime = moment().format('YYYY-MM-DDTHH:mm:ss.SSS');
        $scope.saveChanges();
    };

    /**
     * Attempts to reject the order.
     * A note must be given when rejecting an order. If no note is given,
     * display instructions informing the user to leave a note.
     */
    $scope.rejectOrder = function () {
        if ($scope.originalRequisition.note === $scope.editableRequisition.note) {
            $scope.displayRejectInstructions = true;
        }
        else {
            $scope.editableRequisition.status = 'REJECTED';
            $scope.editableRequisition.rejectedDateTime = moment().format('YYYY-MM-DDTHH:mm:ss.SSS');
            $scope.saveChanges();
        }
    };

    /**
     * Determines if an order has been edited and updates $scope.dirty appropriately.
     */
    $scope.onUpdate = function () {
        $scope.dirty = angular.toJson($scope.originalRequisition) !== angular.toJson($scope.editableRequisition);
    };

    /** --- Location Autocomplete --- */

    /**
     * Updates editableRequisition's destination to the location selected
     * in the edit location autocomplete.
     * If the selected destination is invalid, reset it to the original location instead.
     */
    $scope.onLocationUpdated = function () {
        var loc = locationAutocompleteService.getLocationFromCode($scope.newLocationCode);
        if (loc) {
            $scope.editableRequisition.destination = loc;
        }
        else {
            $scope.editableRequisition.destination = $scope.originalRequisition.destination;
        }
        $scope.onUpdate();
    };

    $scope.getLocationAutocompleteOptions = function () {
        return locationAutocompleteService.getLocationAutocompleteOptions(100);
    };

    /** -- Highlighting --- */

    $scope.calculateHighlighting = function (lineItem) {
        return {
            warn: isOverPerOrderMax(lineItem) || isOverPerMonthMax(lineItem) || containsSpecialItems(lineItem),
            bold: isOverPerMonthMax(lineItem)
        }
    };

    function isOverPerOrderMax(lineItem) {
        return lineItem.quantity > lineItem.item.maxQtyPerOrder
    }

    function containsSpecialItems(lineItem) {
        return lineItem.item.visibility === 'SPECIAL';
    }

    function isOverPerMonthMax(lineItem) {
        if ($scope.locationStatistics == null) {
            return false;
        }
        var monthToDateQty = $scope.locationStatistics.getQuantityForLocationAndItem($scope.originalRequisition.destination.locId,
                                                                                     lineItem.item.commodityCode);
        return monthToDateQty > lineItem.item.suggestedMaxQty
    }

    /** Determines if a line item should be highlighted in the editable-order-listing.jsp */
    $scope.highlightLineItem = function (lineItem) {
        return lineItem.quantity > lineItem.item.suggestedMaxQty || lineItem.item.visibility === 'SPECIAL';
    };

    /** --- Add Item --- **/

    $scope.addItem = function () {
        var newItem = itemAutocompleteService.getItemFromCommodityCode($scope.newItemCommodityCode);
        if (!newItem || isItemADuplicate(newItem)) {
            // Trying to add invalid item, don't do anything.
            return;
        }
        $scope.editableRequisition.lineItems.push({item: newItem, quantity: 1});
        $scope.newItemCommodityCode = "";
        $scope.onUpdate();
    };

    function isItemADuplicate(newItem) {
        var duplicateItem = false;
        angular.forEach($scope.editableRequisition.lineItems, function (lineItem) {
            if (newItem.id === lineItem.item.id) {
                duplicateItem = true;
            }
        });
        return duplicateItem;
    }

    $scope.getItemAutocompleteOptions = function () {
        return itemAutocompleteService.getItemAutocompleteOptions();
    };
}

