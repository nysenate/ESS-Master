var essSupply = angular.module('essSupply');

/**
 * This service converts a collection of items
 * into a collection of line items.
 */
essSupply.service('SupplyLineItemService', [function () {

    /**
     * A LineItem is a SupplyItem with an order quantity.
     */
    function LineItem(item, quantity) {
        this.MAX_QTY = 9999;
        this.item = item;
        this.quantity = quantity;

        this.increment = function () {
            if (this.quantity < this.MAX_QTY) {
                this.quantity++;
            }
        };

        this.decrement = function () {
            if (this.quantity > 0) {
                this.quantity--;
            }
        }
    }

    return {
        /**
         * Generates a array of line items from an array of items.
         * Returns the generated line itemm array.
         */
        generateLineItems: function (items) {

        }
    }
}]);
