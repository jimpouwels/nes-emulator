package nesemulator.cpu;

public enum Flag {
    CARRY(0),
    ZERO(1),
    DISABLE_INTERRUPTS(2),
    DECIMAL_MODE(3),
    BREAK(4),
    UNUSED(5),
    OVERFLOW(6),
    NEGATIVE(7);

    public int value;

    Flag(int position) {
        value = (byte) (1 << position);
    }

}