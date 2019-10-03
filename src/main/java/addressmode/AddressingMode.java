package addressmode;

public abstract class AddressingMode {

    abstract byte mode();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
