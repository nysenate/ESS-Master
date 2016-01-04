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
    public String orderHistory() {
        return SUPPLY_TMPL_BASE_URL + "/history/history";
    }

    @RequestMapping(value="/history/location-history")
    public String locationHistory() {
        return SUPPLY_TMPL_BASE_URL + "/history/location-history";
    }

    /** --- Manage --- */

    @RequestMapping(value="/manage/manage")
    public String manageOrder() {
        return SUPPLY_TMPL_BASE_URL + "/manage/manage";
    }

    @RequestMapping(value="/manage/reconciliation")
    public String reconciliation() {
        return SUPPLY_TMPL_BASE_URL + "/manage/reconciliation";
    }

    @RequestMapping(value="/manage/modal/editing-modal")
    public String managePendingModal() {
        return SUPPLY_TMPL_BASE_URL + "/manage/modal/manage-editing-modal";
    }

    @RequestMapping(value="/manage/modal/completed-modal")
    public String manageCompletedModal() {
        return SUPPLY_TMPL_BASE_URL + "/manage/modal/manage-completed-modal";
    }

    @RequestMapping(value="/manage/editable-order-listing")
    public String editableOrderListing() {
        return SUPPLY_TMPL_BASE_URL + "/manage/editable-order-listing";
    }

    /** --- Order --- */

    @RequestMapping(value="/order/order")
    public String supplyOrder() {
        return SUPPLY_TMPL_BASE_URL + "/order/order";
    }

    /** --- Cart --- */

    @RequestMapping(value="/order/cart/cart")
    public String cart() {
        return SUPPLY_TMPL_BASE_URL + "/order/cart/cart";
    }

    @RequestMapping(value="/order/cart/cart-summary")
    public String cartSummary() {
        return SUPPLY_TMPL_BASE_URL + "/order/cart/cart-summary";
    }

    /** --- Requisition --- */

    @RequestMapping(value="/requisition/view")
    public String viewOrder() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/view";
    }

}
