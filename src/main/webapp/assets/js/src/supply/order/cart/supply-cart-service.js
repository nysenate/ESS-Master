var essSupply = angular.module('essSupply');

/**
 * When ordering, this cart contains a line item of zero quantity for all supply items.
 * Items that are 'really' in the cart will have a positive quantity.
 * This was done to reduce the layers between the model on view.
 * When saving, only line items with positive quantities are saved.
 *
 * On the cart page, this cart is only initialized with persisted line items, which should
 * all have a positive quantity.
 */
essSupply.service('SupplyCartService', ['SupplyLocationAllowanceService', 'SupplyCookieService', function (allowanceService, cookies) {

    /**
     * A Map of itemId's to LineItems.
     */
    var cart = undefined;

    function newQuantity(quantity, lineItem) {
        return lineItem ? lineItem.quantity + quantity : quantity;
    }

    return {
        /**
         * Initializes the cart with the given line items plus saved line items.
         * @param lineItems LineItems to initialize the cart with.
         * Typically a array of zero quantity line items to show on the order form.
         */
        initializeCart: function (lineItems) {
            cart = new Map();
            var addToCart = function (lineItem) {
                cart.set(lineItem.item.id, lineItem);
            };

            if (lineItems) {
                lineItems.forEach(addToCart)
            }
            cookies.getCart().forEach(addToCart);
            return cart;
        },

        /**
         * Add or remove a line item from the cart.
         * Any added line item is copied first so changes to the original don't effect the cart.
         * If the line item's quantity is zero remove it, otherwise add it.
         * Return the updated cart object.
         */
        updateCartLineItem: function (lineItem) {
            var li = angular.copy(lineItem);
            if (li.quantity === 0) {
                cart.delete(li.item.id);
            }
            else {
                cart.set(li.item.id, li);
            }
            return cart;
        },

        getCart: function () {
            return cart;
        },

        getLineItems: function () {
            var lineItems = [];
            cart.forEach(function (lineItem, itemId) {
                lineItems.push(lineItem);
            });
            return lineItems;
        },

        isItemIdOrdered: function (itemId) {
            var lineItem = cart.get(itemId);
            return lineItem != undefined && lineItem.quantity > 0;
        },

        /** Get an item in the cart by its id. returns null if no match is found. */
        getCartLineItem: function (itemId) {
            if (!this.isItemIdOrdered(itemId)) {
                return null;
            }
            return cart.get(itemId);
        },

        getSize: function () {
            var size = 0;
            angular.forEach(cart, function (lineItem) {
                size += lineItem.quantity || 0;
            });
            return size;
        },

        reset: function () {
            cart = new Map();
            cookies.saveCartCookie(cart);
        },

        save: function () {
            cookies.saveCartCookie(cart);
        }
    }
}]);
