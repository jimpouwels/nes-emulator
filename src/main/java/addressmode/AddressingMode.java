package addressmode;

public abstract class AddressingMode {

    public abstract byte set();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
