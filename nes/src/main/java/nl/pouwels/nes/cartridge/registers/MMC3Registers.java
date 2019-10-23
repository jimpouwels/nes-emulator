package nl.pouwels.nes.cartridge.registers;

public class MMC3Registers {

    public int bankSelect_8;
    public int bankData_8;
    public int mirroring_8;
    public int pgrRamProtect_8;
    public int irqLatch_8;
    public int irqReload_8;
    public boolean irqEnable;

    public int[] bankRegisters = new int[8];

}
