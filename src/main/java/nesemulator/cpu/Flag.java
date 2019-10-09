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

    public int value_8;

    Flag(int position) {
        value_8 = 1 << position;
    }

}