package addressmode;

public abstract class AddressingMode {

    public abstract short set();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
