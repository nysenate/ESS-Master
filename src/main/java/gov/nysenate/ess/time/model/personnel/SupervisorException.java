package gov.nysenate.ess.time.model.personnel;

public class SupervisorException extends RuntimeException
{
    public SupervisorException() {}

    public SupervisorException(String message) {
        super(message);
    }

    public SupervisorException(String message, Throwable cause) {
        super(message, cause);
    }
}
