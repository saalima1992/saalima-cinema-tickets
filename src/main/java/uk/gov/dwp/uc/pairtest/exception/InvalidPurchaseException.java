package uk.gov.dwp.uc.pairtest.exception;

public class InvalidPurchaseException extends RuntimeException {
    /**
     * Default constructor with a generic message.
     */
    public InvalidPurchaseException() {
        super("Invalid ticket purchase request. Please check business rules.");
    }

    /**
     * Constructor with a custom error message.
     *
     * @param message Detailed exception message.
     */
    public InvalidPurchaseException(String message) {
        super(message);
    }

    /**
     * Constructor with a custom message and cause.
     *
     * @param message Detailed exception message.
     * @param cause   The root cause of the exception.
     */
    public InvalidPurchaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
