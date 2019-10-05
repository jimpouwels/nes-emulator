package nesemulator.cpu.addressmode;

/**
 * Zero Page Addressing.
 */
public class Zp0 extends AddressingMode {

    @Override
    public short set() {
        return 0;
    }
}