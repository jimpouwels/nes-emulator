package nl.pouwels.nes.ppu.register;

public abstract class Register {

    public abstract void write(int data_8);

    protected int getBitValue(int data_8, int bitIndex) {
        return (data_8 >> bitIndex) & 0x01;
    }

    public abstract int getAsByte();

    public abstract void incrementWith(int incrementValue);
}
