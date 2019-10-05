package nesemulator.cpu;

import nesemulator.Bus;
import nesemulator.cpu.addressmode.*;
import nesemulator.cpu.opcodes.*;

public class Olc6502 {

    private Bus bus;
    private short accumulatorRegister = 0x00;
    private short xRegister = 0x00;
    private short yRegister = 0x00;
    private short stackPointer = 0x00;
    private short programCounter = 0x00;
    private short status = 0x00;
    private short fetched = 0x00;
    private short addrAbs = 0x00;
    private short addrRel = 0x00;
    private short opcode = 0x00;
    private int remainingCycles = 0;
    private Instruction[] instructionLookup = new Instruction[]{
            instruction("BRK", new Brk(), new Imp(this), 7), instruction("ORA", new Ora(), new Izx(this), 6), unknown(), unknown(), unknown(), instruction("ORA", new Ora(), new Zp0(this), 3), instruction("ASL", new Asl(), new Zp0(this), 5), unknown(), instruction("PHP", new Php(), new Imp(this), 3), instruction("ORA", new Ora(), new Imm(this), 2), instruction("ASL", new Asl(), new Imp(this), 2), unknown(), unknown(), instruction("ORA", new Ora(), new Abs(this), 6), instruction("ASL", new Asl(), new Abs(this), 6), unknown(),
            instruction("BPL", new Bpl(), new Rel(this), 2), instruction("ORA", new Ora(), new Izy(this), 5), unknown(), unknown(), unknown(), instruction("ORA", new Ora(), new Zpx(this), 4), instruction("ASL", new Ora(), new Zpx(this), 6), unknown(), instruction("CLC", new Clc(), new Imp(this), 2), instruction("ORA", new Ora(), new Aby(this), 4), unknown(), unknown(), unknown(), instruction("ORA", new Ora(), new Abx(this), 4), instruction("ASL", new Asl(), new Abx(this), 7), unknown(),
            instruction("JSR", new Jsr(), new Abs(this), 6), instruction("AND", new And(), new Izx(this), 6), unknown(), unknown(), instruction("BIT", new Bit(), new Zp0(this), 3), instruction("AND", new And(), new Zp0(this), 3), instruction("ROL", new Rol(), new Zp0(this), 5), unknown(), instruction("PLP", new Plp(), new Imp(this), 4), instruction("AND", new And(), new Imm(this), 2), instruction("ROL", new Rol(), new Imp(this), 2), unknown(), instruction("BIT", new Bit(), new Abs(this), 4), instruction("AND", new And(), new Abs(this), 4), instruction("ROL", new Rol(), new Abs(this), 6), unknown(),
            instruction("BMI", new Bmi(), new Rel(this), 2), instruction("AND", new And(), new Izy(this), 5), unknown(), unknown(), unknown(), instruction("AND", new And(), new Zpx(this), 4), instruction("ROL", new Rol(), new Zpx(this), 6), unknown(), instruction("SEC", new Sec(), new Imp(this), 2), instruction("AND", new And(), new Aby(this), 4), unknown(), unknown(), unknown(), instruction("AND", new And(), new Abx(this), 4), instruction("ROL", new Rol(), new Abx(this), 7), unknown(),
            instruction("RTI", new Rti(), new Imp(this), 6), instruction("EOR", new Eor(), new Izx(this), 6), unknown(), unknown(), unknown(), instruction("EOR", new Eor(), new Zp0(this), 3), instruction("LSR", new Lsr(), new Zp0(this), 5), unknown(), instruction("PHA", new Pha(), new Imp(this), 3), instruction("EOR", new Eor(), new Imm(this), 2), instruction("LSR", new Lsr(), new Imp(this), 2), unknown(), instruction("JMP", new Jmp(), new Abs(this), 3), instruction("EOR", new Eor(), new Abs(this), 4), instruction("LSR", new Lsr(), new Abs(this), 6), unknown(),
            instruction("BVC", new Bvc(), new Rel(this), 2), instruction("EOR", new Eor(), new Izy(this), 5), unknown(), unknown(), unknown(), instruction("EOR", new Eor(), new Zpx(this), 4), instruction("LSR", new Lsr(), new Zpx(this), 6), unknown(), instruction("CLI", new Cli(), new Imp(this), 2), instruction("EOR", new Eor(), new Aby(this), 4), unknown(), unknown(), unknown(), instruction("EOR", new Eor(), new Abx(this), 4), instruction("LSR", new Lsr(), new Abx(this), 7), unknown(),
            instruction("RTS", new Rts(), new Imp(this), 6), instruction("ADC", new Adc(), new Izx(this), 6), unknown(), unknown(), unknown(), instruction("ADC", new Adc(), new Zp0(this), 3), instruction("ROR", new Ror(), new Zp0(this), 5), unknown(), instruction("PLA", new Pla(), new Imp(this), 4), instruction("ADC", new Adc(), new Imm(this), 2), instruction("ROR", new Ror(), new Imp(this), 2), unknown(), instruction("JMP", new Jmp(), new Ind(this), 5), instruction("ADC", new Adc(), new Abs(this), 4), instruction("ROR", new Ror(), new Abs(this), 6), unknown(),
            instruction("BVS", new Bvs(), new Rel(this), 2), instruction("ADC", new Adc(), new Izy(this), 5), unknown(), unknown(), unknown(), instruction("ADC", new Adc(), new Zpx(this), 4), instruction("ROR", new Ror(), new Zpx(this), 6), unknown(), instruction("SEI", new Sei(), new Imp(this), 2), instruction("ADC", new Adc(), new Aby(this), 4), unknown(), unknown(), unknown(), instruction("ADC", new Adc(), new Abx(this), 4), instruction("ROR", new Ror(), new Abx(this), 7), unknown(),
            unknown(), instruction("STA", new Sta(), new Izx(this), 6), unknown(), unknown(), instruction("STY", new Sty(), new Zp0(this), 3), instruction("STA", new Sta(), new Zp0(this), 3), instruction("STX", new Stx(), new Zp0(this), 3), unknown(), instruction("DEY", new Dey(), new Imp(this), 2), unknown(), instruction("TXA", new Txa(), new Imp(this), 2), unknown(), instruction("STY", new Sty(), new Abs(this), 4), instruction("STA", new Sta(), new Abs(this), 4), instruction("STX", new Stx(), new Abs(this), 4), unknown(),
            instruction("BCC", new Bcc(), new Rel(this), 2), instruction("STA", new Sta(), new Izy(this), 6), unknown(), unknown(), instruction("STY", new Sty(), new Zpx(this), 4), instruction("STA", new Sta(), new Zpx(this), 4), instruction("STX", new Stx(), new Zpy(this), 4), unknown(), instruction("TYA", new Tya(), new Imp(this), 2), instruction("STA", new Sta(), new Aby(this), 5), instruction("TXS", new Txs(), new Imp(this), 2), unknown(), unknown(), instruction("STA", new Sta(), new Abx(this), 5), unknown(), unknown(),
            instruction("LDY", new Ldy(), new Imm(this), 2), instruction("LDA", new Lda(), new Izx(this), 6), instruction("LDX", new Ldx(), new Imm(this), 2), unknown(), instruction("LDY", new Ldy(), new Zp0(this), 3), instruction("LDA", new Lda(), new Zp0(this), 3), instruction("LDX", new Ldx(), new Zp0(this), 3), unknown(), instruction("TAY", new Tay(), new Imp(this), 2), instruction("LDA", new Lda(), new Imm(this), 2), instruction("TAX", new Tax(), new Imp(this), 2), unknown(), instruction("LDY", new Ldy(), new Abs(this), 4), instruction("LDA", new Lda(), new Abs(this), 4), instruction("LDX", new Ldx(), new Abs(this), 4), unknown(),
            instruction("BCS", new Bcs(), new Rel(this), 2), instruction("LDA", new Lda(), new Izy(this), 5), unknown(), unknown(), instruction("LDY", new Ldy(), new Zpx(this), 4), instruction("LDA", new Lda(), new Zpx(this), 4), instruction("LDX", new Ldx(), new Zpy(this), 4), unknown(), instruction("CLV", new Clv(), new Imp(this), 2), instruction("LDA", new Lda(), new Aby(this), 4), instruction("TSX", new Tsx(), new Imp(this), 2), unknown(), instruction("LDY", new Ldy(), new Abx(this), 4), instruction("LDA", new Lda(), new Abx(this), 4), instruction("LDX", new Ldx(), new Aby(this), 4), unknown(),
            instruction("CPY", new Cpy(), new Imm(this), 2), instruction("CMP", new Cmp(), new Izx(this), 6), unknown(), unknown(), instruction("CPY", new Cpy(), new Zp0(this), 3), instruction("CMP", new Cmp(), new Zp0(this), 3), instruction("DEC", new Dec(), new Zp0(this), 5), unknown(), instruction("INY", new Iny(), new Imp(this), 2), instruction("CMP", new Cmp(), new Imm(this), 2), instruction("DEX", new Dex(), new Imp(this), 2), unknown(), instruction("CPY", new Cpy(), new Abs(this), 4), instruction("CMP", new Cmp(), new Abs(this), 4), instruction("DEC", new Dec(), new Abs(this), 6), unknown(),
            instruction("BNE", new Bne(), new Rel(this), 2), instruction("CMP", new Cmp(), new Izy(this), 5), unknown(), unknown(), unknown(), instruction("CMP", new Cmp(), new Zpx(this), 4), instruction("DEC", new Dec(), new Zpx(this), 6), unknown(), instruction("CLD", new Cld(), new Imp(this), 2), instruction("CMP", new Cmp(), new Aby(this), 4), unknown(), unknown(), unknown(), instruction("CMP", new Cmp(), new Abx(this), 4), instruction("DEC", new Dec(), new Abx(this), 7), unknown(),
            instruction("CPX", new Cpx(), new Imm(this), 2), instruction("SBC", new Sbc(), new Izx(this), 6), unknown(), unknown(), instruction("CPX", new Cpx(), new Zp0(this), 3), instruction("SBC", new Sbc(), new Zp0(this), 3), instruction("INC", new Inc(), new Zp0(this), 5), unknown(), instruction("INX", new Inx(), new Imp(this), 2), instruction("SBC", new Sbc(), new Imm(this), 2), instruction("NOP", new Nop(), new Imp(this), 2), unknown(), instruction("CPX", new Cpx(), new Abs(this), 4), instruction("SBC", new Sbc(), new Abs(this), 4), instruction("INC", new Inc(), new Abs(this), 6), unknown(),
            instruction("BEQ", new Beq(), new Rel(this), 2), instruction("SBC", new Sbc(), new Izy(this), 5), unknown(), unknown(), unknown(), instruction("SBC", new Sbc(), new Zpx(this), 4), instruction("INC", new Inc(), new Zpx(this), 6), unknown(), instruction("SED", new Sed(), new Imp(this), 2), instruction("SBC", new Sbc(), new Aby(this), 4), unknown(), unknown(), unknown(), instruction("SBC", new Sbc(), new Abx(this), 4), instruction("INC", new Inc(), new Abx(this), 7), unknown()
    };

    public Olc6502(Bus bus) {
        this.bus = bus;
    }

    public void write(int addr, short data) {
        bus.write(addr, data);
    }

    public short read(int addr) {
        return bus.read(addr, false);
    }

    public short getFlag(Flag flag) {
        return (short) ((status & flag.value) > 0 ? 1 : 0);
    }

    public void setFlag(Flag flag) {
        status |= flag.value;
    }

    public void clearFlag(Flag flag) {
        status &= ~flag.value;
    }

    public void setFetched(short value) {
        this.fetched = value;
    }

    public short getAccumulatorRegister() {
        return accumulatorRegister;
    }

    public short incrementProgramCounter() {
        return ++programCounter;
    }

    public void setAddressAbsolute(short value) {
        this.addrAbs = value;
    }

    public void clock() {
        if (remainingCycles == 0) {
            short opcode = read(programCounter);
            programCounter++;
            nesemulator.cpu.Instruction instruction = instructionLookup[opcode];
            remainingCycles = instruction.cycles;
            short additionalCycle1 = instruction.addressingMode.set();
            short additionalCycle2 = instruction.opcode.operate();
            remainingCycles += additionalCycle1 & additionalCycle2;
        }
        remainingCycles--;
    }

    public void reset() {

    }
    // interrupt request signal

    public void irq() {

    }

    // non maskable request

    public void nmi() {

    }

    public short fetch() {
        return 0x0;
    }

    public Instruction[] getInstructions() {
        return this.instructionLookup;
    }

    private Instruction unknown() {
        return instruction("???", new InvalidOpcode(), new Imp(this), 8);
    }

    private Instruction instruction(String name, Opcode opcode, AddressingMode addressingMode, int cycles) {
        return new Instruction(name, opcode, addressingMode, cycles);
    }
}
