package gov.nysenate.ess.core.service.notification.base.header.castType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 * Created by Chenguang He  on 6/15/2016.
 * An event is a group event, if and only if the event send to specific group of subscriber.
 */
public interface Groupcast extends Header {
    StringBuilder castType = new StringBuilder(CastType.castType).append("." + Groupcast.class.getName());

}
