package nl.pouwels.nes.cartridge.registers;

public class MMC3Registers {

    public int bankSelect_8;
    public int bankData_8;
    public int mirroring_8;
    public int pgrRamProtect_8;
    public int irqLatch_8;
    public int irqReload_8;
    public int irqEnable_8;
    public int irqDisable_8;

    public int[] bankRegisters = new int[8];

    public void reset() {
        bankSelect_8 = 0x00;
        bankData_8 = 0x00;
        mirroring_8 = 0x00;
        pgrRamProtect_8 = 0x00;
        irqLatch_8 = 0x00;
        irqReload_8 = 0x00;
        irqEnable_8 = 0x00;
        irqDisable_8 = 0x00;
    }
}
