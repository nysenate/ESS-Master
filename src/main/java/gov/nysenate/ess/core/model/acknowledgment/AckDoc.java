package gov.nysenate.ess.core.model.acknowledgment;

import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;

import java.time.LocalDateTime;

/**
 * A document that must be acknowledged by active employees.
 */
public class AckDoc {

    /** Document title */
    private String title;
    /** Path to document pdf */
    private String filename;
    /** True iff the document is currently active, i.e. required to be acknowledged. */
    private boolean active;
    /** Unique id for this document */
    private int id;
    /** The date after which this document is active */
    private LocalDateTime effectiveDateTime;

    public AckDoc() {}

    public AckDoc(String title, String filename, boolean active, int id, LocalDateTime effectiveDateTime) {
        this.title = title;
        this.filename = filename;
        this.active = active;
        this.id = id;
        this.effectiveDateTime = effectiveDateTime;
    }

    /* --- Functional Getters --- */

    public PersonnelTaskId getTaskId() {
        return new PersonnelTaskId(PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT, id);
    }

    /* --- Getters / Setters --- */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public void setEffectiveDateTime(LocalDateTime effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }
}
