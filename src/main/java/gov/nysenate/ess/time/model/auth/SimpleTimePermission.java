package gov.nysenate.ess.time.model.auth;

import org.apache.shiro.authz.permission.WildcardPermission;


/**
 * Enumerates ess time permissions that do not need to be generated in a dynamic way
 */
public enum SimpleTimePermission {

    /** Granted to supervisors allowing them to use the time management pages */
    MANAGEMENT_PAGES("time:management-pages"),

    /** Granted to users that can enter time and view time attendance history */
    ATTENDANCE_RECORD_PAGES("time:attendance-record-pages"),

    /** Granted to users that can view accrual history and projections pages */
    ACCRUAL_PAGES("time:accrual-pages"),

    /** Granted to annual employees allowing them to view accrual projections */
    ACCRUAL_PROJECTIONS("time:accrual-projections:view"),

    /** Granted to temporary employees allowing them to view the allowance page */
    ALLOWANCE_PAGE("time:allowance-page"),

    /* --- Admin Permissions --- */

    /** Allows full use of the time record manager to create / modify records */
    TIME_RECORD_MANAGEMENT("admin:time:timerecords:manager")

    ;

    private String permissionString;

    SimpleTimePermission(String permissionString) {
        this.permissionString = permissionString;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public WildcardPermission getPermission() {
        return new WildcardPermission(permissionString);
    }
}
