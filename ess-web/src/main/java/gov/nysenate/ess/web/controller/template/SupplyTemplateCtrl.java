package gov.nysenate.ess.web.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(SupplyTemplateCtrl.SUPPLY_TMPL_BASE_URL)
public class SupplyTemplateCtrl extends BaseTemplateCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SupplyTemplateCtrl.class);
    protected static final String SUPPLY_TMPL_BASE_URL = TMPL_BASE_URL + "/supply";

    /** --- History --- */

    @RequestMapping(value="/history/history")
    public String requisitionHistory() {
        return SUPPLY_TMPL_BASE_URL + "/history/history";
    }

    @RequestMapping(value="/order-history")
    public String orderHistory() {
        return SUPPLY_TMPL_BASE_URL + "/history/order-history";
    }

    /** --- Manage --- */

    @RequestMapping(value="/manage/fulfillment")
    public String manageOrder() {
        return SUPPLY_TMPL_BASE_URL + "/manage/fulfillment/fulfillment";
    }

    @RequestMapping(value="/manage/reconciliation")
    public String reconciliation() {
        return SUPPLY_TMPL_BASE_URL + "/manage/reconciliation";
    }

    @RequestMapping(value="/manage/fulfillment/modal/fulfillment-editing-modal")
    public String managePendingModal() {
        return SUPPLY_TMPL_BASE_URL + "/manage/fulfillment/modal/fulfillment-editing-modal";
    }

    @RequestMapping(value="/manage/fulfillment/modal/fulfillment-immutable-modal")
    public String manageCompletedModal() {
        return SUPPLY_TMPL_BASE_URL + "/manage/fulfillment/modal/fulfillment-immutable-modal";
    }

    @RequestMapping(value="/manage/fulfillment/modal/editable-order-listing")
    public String editableOrderListing() {
        return SUPPLY_TMPL_BASE_URL + "/manage/fulfillment/modal/editable-order-listing";
    }

    /** --- Order --- */

    @RequestMapping(value="/order")
    public String supplyOrder() {
        return SUPPLY_TMPL_BASE_URL + "/order/order";
    }

    @RequestMapping(value="/order/over-allowed-quantity-modal")
    public String overAllowedQuantityModal() {
        return SUPPLY_TMPL_BASE_URL + "/order/over-allowed-quantity-modal";
    }

    @RequestMapping(value="/order/item-special-request-modal")
    public String orderAdditional() {
        return SUPPLY_TMPL_BASE_URL + "/order/item-special-request-modal";
    }

    @RequestMapping(value="/order/special-order-item-modal")
    public String specialOrderItemModal() {
        return SUPPLY_TMPL_BASE_URL + "/order/special-order-item-modal";
    }

    /** --- Cart --- */

    @RequestMapping(value="/order/cart")
    public String cart() {
        return SUPPLY_TMPL_BASE_URL + "/order/cart/cart";
    }

    @RequestMapping(value="/order/cart/cart-summary")
    public String cartSummary() {
        return SUPPLY_TMPL_BASE_URL + "/order/cart/cart-summary";
    }

    @RequestMapping(value="/order/cart/cart-checkout-modal")
    public String cartCheckoutModal() {
        return SUPPLY_TMPL_BASE_URL + "/order/cart/cart-checkout-modal";
    }

    /** --- Requisition --- */

    @RequestMapping(value="/requisition/requisition-view")
    public String viewOrder() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/requisition-view";
    }

}
