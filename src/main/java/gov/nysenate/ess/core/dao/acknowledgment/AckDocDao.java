package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.model.pec.acknowledgment.*;

import java.util.List;

public interface AckDocDao {

    /**
     * Gets an AckDoc by its ID in the database
     *
     * @param ackDocId - the id of the AckDoc object
     * @return {@link AckDoc}
     * @throws AckDocNotFoundEx if no {@link AckDoc} can be found with the given id
     */
    AckDoc getAckDoc(int ackDocId) throws AckDocNotFoundEx;

    /**
     * Inserts and ackDoc into the database
     *
     * @param ackDoc - the ackDoc to be inserted into the database
     */
    void insertAckDoc(AckDoc ackDoc);

    /**
     * Gets a set of all active AckDocs
     *
     * @return {@link List<AckDoc>}
     */
    List<AckDoc> getActiveAckDocs();

    /**
     * Gets an Acknowledgment from an employee id and an ackDocId
     *
     * @param empId - the Id of the employee
     * @param ackDocId - the id of the ackDoc
     * @return {@link Acknowledgment} - the acknowledgment corresponding to the emp and the ackDoc
     */
    Acknowledgment getAcknowledgmentById(int empId, int ackDocId);

    /**
     * Inserts an Acknowledgment into the database
     *
     * @param acknowledgment - the Acknowledgment to be inserted into the database
     */
    void insertAcknowledgment(Acknowledgment acknowledgment);

    /**
     * Gets all acknowledgments in the database
     *
     * @return {@link List< Acknowledgment >}
     */
    List<Acknowledgment> getAllAcknowledgments();

    /**
     * Gets all acknowledgments in the database for a specified employee
     *
     * @return {@link List< Acknowledgment >}
     */
    List<Acknowledgment> getAllAcknowledgmentsForEmp(int empId);

    /**
     * This method returns all acknowledgments for the ackDoc with the requested id for all active employees.
     *
     * {@link ReportAck}
     * @param ackDocId - The id of the ack doc a personnel member would want to generate a report for
     * @return {@link List<ReportAck>} - The list of all acknowlegments in report form for the requested ackdoc
     */
    List<ReportAck> getAllAcksForAckDocById(int ackDocId);

    /**
     * Retrieves all AckDocs whether they are active or not
     *
     * @return {@link List<AckDoc>}
     */
    List<AckDoc> getAllAckDocs();

    /**
     * Retrieves all years that have an ack doc with an effective date time in any year.
     * For ex. if 2017 has 2 ackdocs and 2018 has 1 ack doc, 2017, and 2018 would be returned
     *
     * @return {@link List<String>}
     */
    List<Integer> getAllYearsContainingAckDocs();

    /**
     * Retrieves all ack docs that have an effective date time in that year
     * For ex. if 2017 has 2 ackdocs then both of those ack docs would be returned regardless of
     * other documents in 2018,19 etc.
     *
     * @return {@link List<AckDoc>}
     */
    List<AckDoc> getAckDocsForYear(int year);

}
