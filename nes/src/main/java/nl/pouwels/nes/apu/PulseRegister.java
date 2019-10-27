package nl.pouwels.nes.apu;

public class PulseRegister {

    // $4000 / $4004	DDLC VVVV	Duty (D), envelope loop / length counter halt (L), constant volume (C), volume/envelope (V)
    public int timer_8;

    // $4001 / $4005	EPPP NSSS	Sweep unit: enabled (E), period (P), negate (N), shift (S)
    public int sweepUnit_period_negate_shift_8;

    // $4002 / $4006	TTTT TTTT	Timer low (T)
    public int timerLow_8;

    // $4003 / $4007	LLLL LTTT	Length counter load (L), timer high (T)
    public int lengthCounterLoad_timerHigh_8;
}
