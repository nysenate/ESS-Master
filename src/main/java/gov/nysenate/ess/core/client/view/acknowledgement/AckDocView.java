package gov.nysenate.ess.core.client.view.acknowledgement;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.acknowledgement.AckDoc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement
public class AckDocView implements ViewObject {

    private static final String ackDocDir = "/assets/ack_docs/";

    private String title;
    private String path;
    private Boolean active;
    private Integer id;
    private LocalDateTime effectiveDateTime;

    protected AckDocView() {}

    public AckDocView(AckDoc ackDoc) {
        this.title = ackDoc.getTitle();
        this.path = ackDocDir + ackDoc.getFilename();
        this.active = ackDoc.getActive();
        this.id = ackDoc.getId();
        this.effectiveDateTime = ackDoc.getEffectiveDateTime();
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    @XmlElement
    public String getPath() {
        return path;
    }

    @XmlElement
    public Boolean getActive() {
        return active;
    }

    @XmlElement
    public Integer getId() {
        return id;
    }

    @XmlElement
    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "ack_doc";
    }
}
