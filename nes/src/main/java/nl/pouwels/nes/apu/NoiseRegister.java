package nl.pouwels.nes.apu;

public class NoiseRegister {

    // $400C	--LC VVVV	Envelope loop / length counter halt (L), constant volume (C), volume/envelope (V)
    public int timer_8;

    public int unused_8;

    // $400E	L--- PPPP	Loop noise (L), noise period (P)
    public int loopNoise_noisePeriod_8;

    // $400F	LLLL L---	Length counter load (L)
    public int lengthCounterLoad_8;
}
