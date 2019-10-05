package nesemulator.exception;

public class MemoryAddressExceedsMemoryException extends RuntimeException {

    public MemoryAddressExceedsMemoryException(String message) {
        super(message);
    }
}
