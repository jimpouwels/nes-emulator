package nl.pouwels.nes.apu;

public class TriangleRegister {

    // $4008	CRRR RRRR	Length counter halt / linear counter control (C), linear counter load (R)
    public int timer_8;

    // $4009	---- ----	Unused
    public int unused_8;

    // $400A	TTTT TTTT	Timer low (T)
    public int timerLow_8;

    // $400B	LLLL LTTT	Length counter load (L), timer high (T)
    public int lengthCounterLoad_timerHigh_8;
}
