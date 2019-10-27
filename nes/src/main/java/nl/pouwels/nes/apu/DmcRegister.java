package nl.pouwels.nes.apu;

public class DmcRegister {

    // $4010	IL-- RRRR	IRQ enable (I), loop (L), frequency (R)
    public int irqEnable_loop_frequency_8;

    // $4011	-DDD DDDD	Load counter (D)
    public int loadCounter_8;

    // $4012	AAAA AAAA	Sample address (A)
    public int sampleAddress_8;

    // $4013	LLLL LLLL	Sample length (L)
    public int sampleLength_8;
}
