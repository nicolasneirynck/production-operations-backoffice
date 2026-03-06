package exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Jouw rol heeft niet de nodige permissions hiervoor.");
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessDeniedException(Throwable cause) {
        super(cause);
    }
}