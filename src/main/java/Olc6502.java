import addressmode.*;
import opcodes.*;

public class Olc6502 {

    private Bus bus;
    private byte accumulatorRegister = 0x00;
    private byte xRegister = 0x00;
    private byte yRegister = 0x00;
    private byte stackPointer = 0x00;
    private short programCounter = 0x00;
    private byte status = 0x00;
    private byte fetched = 0x00;
    private short addrAbs = 0x0000;
    private short addrRel = 0x00;
    private byte opcode = 0x00;
    private int cycles = 0;
    private Instruction[][] instructionLookup = new Instruction[][]{
            {instruction("BRK", new Brk(), new Imp(), 7), instruction("ORA", new Ora(), new Izx(), 6), unknown(), unknown(), unknown(), instruction("ORA", new Ora(), new Zp0(), 3), instruction("ASL", new Asl(), new Zp0(), 5), unknown(), instruction("PHP", new Php(), new Imp(), 3), instruction("ORA", new Ora(), new Imm(), 2), instruction("ASL", new Asl(), new Imp(), 2), unknown(), unknown(), instruction("ORA", new Ora(), new Abs(), 6), instruction("ASL", new Asl(), new Abs(), 6), unknown()},
            {instruction("BPL", new Bpl(), new Rel(), 2), instruction("ORA", new Ora(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("ORA", new Ora(), new Zpx(), 4), instruction("ASL", new Ora(), new Zpx(), 6), unknown(), instruction("CLC", new Clc(), new Imp(), 2), instruction("ORA", new Ora(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("ORA", new Ora(), new Abx(), 4), instruction("ASL", new Asl(), new Abx(), 7), unknown()},
            {instruction("JSR", new Jsr(), new Abs(), 6), instruction("AND", new And(), new Izx(), 6), unknown(), unknown(), instruction("BIT", new Bit(), new Zp0(), 3), instruction("AND", new And(), new Zp0(), 3), instruction("ROL", new Rol(), new Zp0(), 5), unknown(), instruction("PLP", new Plp(), new Imp(), 4), instruction("AND", new And(), new Imm(), 2), instruction("ROL", new Rol(), new Imp(), 2), unknown(), instruction("BIT", new Bit(), new Abs(), 4), instruction("AND", new And(), new Abs(), 4), instruction("ROL", new Rol(), new Abs(), 6), unknown()},
            {instruction("BMI", new Bmi(), new Rel(), 2), instruction("AND", new And(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("AND", new And(), new Zpx(), 4), instruction("ROL", new Rol(), new Zpx(), 6), unknown(), instruction("SEC", new Sec(), new Imp(), 2), instruction("AND", new And(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("AND", new And(), new Abx(), 4), instruction("ROL", new Rol(), new Abx(), 7), unknown()},
            {instruction("RTI", new Rti(), new Imp(), 6), instruction("EOR", new Eor(), new Izx(), 6), unknown(), unknown(), unknown(), instruction("EOR", new Eor(), new Zp0(), 3), instruction("LSR", new Lsr(), new Zp0(), 5), unknown(), instruction("PHA", new Pha(), new Imp(), 3), instruction("EOR", new Eor(), new Imm(), 2), instruction("LSR", new Lsr(), new Imp(), 2), unknown(), instruction("JMP", new Jmp(), new Abs(), 3), instruction("EOR", new Eor(), new Abs(), 4), instruction("LSR", new Lsr(), new Abs(), 6), unknown()},
            {instruction("BVC", new Bvc(), new Rel(), 2), instruction("EOR", new Eor(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("EOR", new Eor(), new Zpx(), 4), instruction("LSR", new Lsr(), new Zpx(), 6), unknown(), instruction("CLI", new Cli(), new Imp(), 2), instruction("EOR", new Eor(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("EOR", new Eor(), new Abx(), 4), instruction("LSR", new Lsr(), new Abx(), 7), unknown()},
            {instruction("RTS", new Rts(), new Imp(), 6), instruction("ADC", new Adc(), new Izx(), 6), unknown(), unknown(), unknown(), instruction("ADC", new Adc(), new Zp0(), 3), instruction("ROR", new Ror(), new Zp0(), 5), unknown(), instruction("PLA", new Pla(), new Imp(), 4), instruction("ADC", new Adc(), new Imm(), 2), instruction("ROR", new Ror(), new Imp(), 2), unknown(), instruction("JMP", new Jmp(), new Ind(), 5), instruction("ADC", new Adc(), new Abs(), 4), instruction("ROR", new Ror(), new Abs(), 6), unknown()},
            {instruction("BVS", new Bvs(), new Rel(), 2), instruction("ADC", new Adc(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("ADC", new Adc(), new Zpx(), 4), instruction("ROR", new Ror(), new Zpx(), 6), unknown(), instruction("SEI", new Sei(), new Imp(), 2), instruction("ADC", new Adc(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("ADC", new Adc(), new Abx(), 4), instruction("ROR", new Ror(), new Abx(), 7), unknown()},
            {unknown(), instruction("STA", new Sta(), new Izx(), 6), unknown(), unknown(), instruction("STY", new Sty(), new Zp0(), 3), instruction("STA", new Sta(), new Zp0(), 3), instruction("STX", new Stx(), new Zp0(), 3), unknown(), instruction("DEY", new Dey(), new Imp(), 2), unknown(), instruction("TXA", new Txa(), new Imp(), 2), unknown(), instruction("STY", new Sty(), new Abs(), 4), instruction("STA", new Sta(), new Abs(), 4), instruction("STX", new Stx(), new Abs(), 4), unknown()},
            {instruction("BCC", new Bcc(), new Rel(), 2), instruction("STA", new Sta(), new Izy(), 6), unknown(), unknown(), instruction("STY", new Sty(), new Zpx(), 4), instruction("STA", new Sta(), new Zpx(), 4), instruction("STX", new Stx(), new Zpy(), 4), unknown(), instruction("TYA", new Tya(), new Imp(), 2), instruction("STA", new Sta(), new Aby(), 5), instruction("TXS", new Txs(), new Imp(), 2), unknown(), unknown(), instruction("STA", new Sta(), new Abx(), 5), unknown(), unknown()},
            {instruction("LDY", new Ldy(), new Imm(), 2), instruction("LDA", new Lda(), new Izx(), 6), instruction("LDX", new Ldx(), new Imm(), 2), unknown(), instruction("LDY", new Ldy(), new Zp0(), 3), instruction("LDA", new Lda(), new Zp0(), 3), instruction("LDX", new Ldx(), new Zp0(), 3), unknown(), instruction("TAY", new Tay(), new Imp(), 2), instruction("LDA", new Lda(), new Imm(), 2), instruction("TAX", new Tax(), new Imp(), 2), unknown(), instruction("LDY", new Ldy(), new Abs(), 4), instruction("LDA", new Lda(), new Abs(), 4), instruction("LDX", new Ldx(), new Abs(), 4), unknown()},
            {instruction("BCS", new Bcs(), new Rel(), 2), instruction("LDA", new Lda(), new Izy(), 5), unknown(), unknown(), instruction("LDY", new Ldy(), new Zpx(), 4), instruction("LDA", new Lda(), new Zpx(), 4), instruction("LDX", new Ldx(), new Zpy(), 4), unknown(), instruction("CLV", new Clv(), new Imp(), 2), instruction("LDA", new Lda(), new Aby(), 4), instruction("TSX", new Tsx(), new Imp(), 2), unknown(), instruction("LDY", new Ldy(), new Abx(), 4), instruction("LDA", new Lda(), new Abx(), 4), instruction("LDX", new Ldx(), new Aby(), 4), unknown()},
            {instruction("CPY", new Cpy(), new Imm(), 2), instruction("CMP", new Cmp(), new Izx(), 6), unknown(), unknown(), instruction("CPY", new Cpy(), new Zp0(), 3), instruction("CMP", new Cmp(), new Zp0(), 3), instruction("DEC", new Dec(), new Zp0(), 5), unknown(), instruction("INY", new Iny(), new Imp(), 2), instruction("CMP", new Cmp(), new Imm(), 2), instruction("DEX", new Dex(), new Imp(), 2), unknown(), instruction("CPY", new Cpy(), new Abs(), 4), instruction("CMP", new Cmp(), new Abs(), 4), instruction("DEC", new Dec(), new Abs(), 6), unknown()},
            {instruction("BNE", new Bne(), new Rel(), 2), instruction("CMP", new Cmp(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("CMP", new Cmp(), new Zpx(), 4), instruction("DEC", new Dec(), new Zpx(), 6), unknown(), instruction("CLD", new Cld(), new Imp(), 2), instruction("CMP", new Cmp(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("CMP", new Cmp(), new Abx(), 4), instruction("DEC", new Dec(), new Abx(), 7), unknown()},
            {instruction("CPX", new Cpx(), new Imm(), 2), instruction("SBC", new Sbc(), new Izx(), 6), unknown(), unknown(), instruction("CPX", new Cpx(), new Zp0(), 3), instruction("SBC", new Sbc(), new Zp0(), 3), instruction("INC", new Inc(), new Zp0(), 5), unknown(), instruction("INX", new Inx(), new Imp(), 2), instruction("SBC", new Sbc(), new Imm(), 2), instruction("NOP", new Nop(), new Imp(), 2), unknown(), instruction("CPX", new Cpx(), new Abs(), 4), instruction("SBC", new Sbc(), new Abs(), 4), instruction("INC", new Inc(), new Abs(), 6), unknown()},
            {instruction("BEQ", new Beq(), new Rel(), 2), instruction("SBC", new Sbc(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("SBC", new Sbc(), new Zpx(), 4), instruction("INC", new Inc(), new Zpx(), 6), unknown(), instruction("SED", new Sed(), new Imp(), 2), instruction("SBC", new Sbc(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("SBC", new Sbc(), new Abx(), 4), instruction("INC", new Inc(), new Abx(), 7), unknown()}
    };

    public Olc6502(Bus bus) {
        this.bus = bus;
    }

    public void write(int addr, byte data) {
        bus.write(addr, data);
    }

    public byte read(int addr) {
        return bus.read(addr, false);
    }

    public Flag getFlag(Flag flag) {
        return null;
    }

    public void setFlag(Flag flag) {
    }

    // clock signal

    public void clock() {

    }
    // reset signal

    public void reset() {

    }
    // interrupt request signal

    public void irq() {

    }
    // non maskable request

    public void nmi() {

    }

    public byte fetch() {
        return 0x0;
    }

    public enum Flag {
        CARRY(0),
        ZERO(1),
        DISABLE_INTERRUPTS(2),
        DECIMAL_MODE(3),
        BREAK(4),
        UNUSED(5),
        OVERFLOW(6),
        NEGATIVE(7);

        private byte value;

        Flag(int position) {
            value = (byte) (1 << position);
        }

    }

    private Instruction unknown() {
        return instruction("???", new InvalidOpcode(), new Imp(), 8);
    }

    private Instruction instruction(String name, Opcode opcode, AddressingMode addressingMode, int cycles) {
        return new Instruction(name, opcode, addressingMode, cycles);
    }
}
