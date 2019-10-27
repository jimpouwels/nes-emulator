package nl.pouwels.nes.apu;

public class Rp2a0x {

    private PulseRegister pulseRegister1 = new PulseRegister();
    private PulseRegister pulseRegister2 = new PulseRegister();

    public void clock() {
    }

    public void cpuWrite(int address_16, int data_8) {
        switch (address_16) {
            case 0x4000:
                pulseRegister1.timer_8 = data_8;
                break;
            case 0x4001:
                pulseRegister1.sweepUnit_period_negate_shift_8 = data_8;
                break;
            case 0x4002:
                pulseRegister1.timerLow_8 = data_8;
                break;
            case 0x4003:
                pulseRegister1.lengthCounterLoad_timerHigh_8 = data_8;
                break;
            case 0x4004:
                pulseRegister2.timer_8 = data_8;
                break;
            case 0x4005:
                pulseRegister2.sweepUnit_period_negate_shift_8 = data_8;
                break;
            case 0x4006:
                pulseRegister2.timerLow_8 = data_8;
                break;
            case 0x4007:
                pulseRegister2.lengthCounterLoad_timerHigh_8 = data_8;
                break;
        }
    }

    public boolean isInRange(int address_16) {
        return (address_16 >= 0x4000 && address_16 <= 0x4013) || address_16 == 0x4015 || address_16 == 0x4017;
    }
}
