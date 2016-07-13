package gov.nysenate.ess.core.client.response.auth;

import gov.nysenate.ess.core.client.view.AuthorizationStatusView;
import gov.nysenate.ess.core.model.auth.AuthorizationStatus;
import org.apache.shiro.subject.Subject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AuthorizationResponse
{
    @XmlElement protected AuthorizationStatusView status;
    @XmlElement protected String user;
    @XmlElement protected String url;

    public AuthorizationResponse(AuthorizationStatus status, Subject subject, String url) {
        this.status = new AuthorizationStatusView(status);
        if (subject != null && subject.getPrincipal() != null) {
            this.user = subject.getPrincipal().toString();
        }
        this.url = url;
    }

    public AuthorizationStatusView getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }

    public String getUrl() {
        return url;
    }
}
