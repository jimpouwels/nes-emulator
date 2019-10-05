package nesemulator.cpu;

import nesemulator.Bus;
import nesemulator.cpu.opcodes.*;

import static nesemulator.utils.ByteUtilities.widenIgnoreSigning;

public class Olc6502 {

    private Bus bus;
    private byte accumulatorRegister = 0x00;
    private byte xRegister = 0x00;
    private byte yRegister = 0x00;
    private byte stackPointer = 0x00;
    private short programCounter = 0x0000;
    private byte status = 0x00;
    private byte fetched = 0x00;
    private short addrAbs = 0x0000;
    public short addrRel = 0x00;
    public byte opcode = 0x00;
    private int remainingCycles = 0;
    private Instruction[] instructionLookup = new Instruction[]{
            instruction("BRK", new Brk(), new Imp(), 7), instruction("ORA", new Ora(), new Izx(), 6), unknown(), unknown(), unknown(), instruction("ORA", new Ora(), new Zp0(), 3), instruction("ASL", new Asl(), new Zp0(), 5), unknown(), instruction("PHP", new Php(), new Imp(), 3), instruction("ORA", new Ora(), new Imm(), 2), instruction("ASL", new Asl(), new Imp(), 2), unknown(), unknown(), instruction("ORA", new Ora(), new Abs(), 6), instruction("ASL", new Asl(), new Abs(), 6), unknown(),
            instruction("BPL", new Bpl(), new Rel(), 2), instruction("ORA", new Ora(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("ORA", new Ora(), new Zpx(), 4), instruction("ASL", new Ora(), new Zpx(), 6), unknown(), instruction("CLC", new Clc(), new Imp(), 2), instruction("ORA", new Ora(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("ORA", new Ora(), new Abx(), 4), instruction("ASL", new Asl(), new Abx(), 7), unknown(),
            instruction("JSR", new Jsr(), new Abs(), 6), instruction("AND", new And(), new Izx(), 6), unknown(), unknown(), instruction("BIT", new Bit(), new Zp0(), 3), instruction("AND", new And(), new Zp0(), 3), instruction("ROL", new Rol(), new Zp0(), 5), unknown(), instruction("PLP", new Plp(), new Imp(), 4), instruction("AND", new And(), new Imm(), 2), instruction("ROL", new Rol(), new Imp(), 2), unknown(), instruction("BIT", new Bit(), new Abs(), 4), instruction("AND", new And(), new Abs(), 4), instruction("ROL", new Rol(), new Abs(), 6), unknown(),
            instruction("BMI", new Bmi(), new Rel(), 2), instruction("AND", new And(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("AND", new And(), new Zpx(), 4), instruction("ROL", new Rol(), new Zpx(), 6), unknown(), instruction("SEC", new Sec(), new Imp(), 2), instruction("AND", new And(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("AND", new And(), new Abx(), 4), instruction("ROL", new Rol(), new Abx(), 7), unknown(),
            instruction("RTI", new Rti(), new Imp(), 6), instruction("EOR", new Eor(), new Izx(), 6), unknown(), unknown(), unknown(), instruction("EOR", new Eor(), new Zp0(), 3), instruction("LSR", new Lsr(), new Zp0(), 5), unknown(), instruction("PHA", new Pha(), new Imp(), 3), instruction("EOR", new Eor(), new Imm(), 2), instruction("LSR", new Lsr(), new Imp(), 2), unknown(), instruction("JMP", new Jmp(), new Abs(), 3), instruction("EOR", new Eor(), new Abs(), 4), instruction("LSR", new Lsr(), new Abs(), 6), unknown(),
            instruction("BVC", new Bvc(), new Rel(), 2), instruction("EOR", new Eor(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("EOR", new Eor(), new Zpx(), 4), instruction("LSR", new Lsr(), new Zpx(), 6), unknown(), instruction("CLI", new Cli(), new Imp(), 2), instruction("EOR", new Eor(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("EOR", new Eor(), new Abx(), 4), instruction("LSR", new Lsr(), new Abx(), 7), unknown(),
            instruction("RTS", new Rts(), new Imp(), 6), instruction("ADC", new Adc(), new Izx(), 6), unknown(), unknown(), unknown(), instruction("ADC", new Adc(), new Zp0(), 3), instruction("ROR", new Ror(), new Zp0(), 5), unknown(), instruction("PLA", new Pla(), new Imp(), 4), instruction("ADC", new Adc(), new Imm(), 2), instruction("ROR", new Ror(), new Imp(), 2), unknown(), instruction("JMP", new Jmp(), new Ind(), 5), instruction("ADC", new Adc(), new Abs(), 4), instruction("ROR", new Ror(), new Abs(), 6), unknown(),
            instruction("BVS", new Bvs(), new Rel(), 2), instruction("ADC", new Adc(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("ADC", new Adc(), new Zpx(), 4), instruction("ROR", new Ror(), new Zpx(), 6), unknown(), instruction("SEI", new Sei(), new Imp(), 2), instruction("ADC", new Adc(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("ADC", new Adc(), new Abx(), 4), instruction("ROR", new Ror(), new Abx(), 7), unknown(),
            unknown(), instruction("STA", new Sta(), new Izx(), 6), unknown(), unknown(), instruction("STY", new Sty(), new Zp0(), 3), instruction("STA", new Sta(), new Zp0(), 3), instruction("STX", new Stx(), new Zp0(), 3), unknown(), instruction("DEY", new Dey(), new Imp(), 2), unknown(), instruction("TXA", new Txa(), new Imp(), 2), unknown(), instruction("STY", new Sty(), new Abs(), 4), instruction("STA", new Sta(), new Abs(), 4), instruction("STX", new Stx(), new Abs(), 4), unknown(),
            instruction("BCC", new Bcc(), new Rel(), 2), instruction("STA", new Sta(), new Izy(), 6), unknown(), unknown(), instruction("STY", new Sty(), new Zpx(), 4), instruction("STA", new Sta(), new Zpx(), 4), instruction("STX", new Stx(), new Zpy(), 4), unknown(), instruction("TYA", new Tya(), new Imp(), 2), instruction("STA", new Sta(), new Aby(), 5), instruction("TXS", new Txs(), new Imp(), 2), unknown(), unknown(), instruction("STA", new Sta(), new Abx(), 5), unknown(), unknown(),
            instruction("LDY", new Ldy(), new Imm(), 2), instruction("LDA", new Lda(), new Izx(), 6), instruction("LDX", new Ldx(), new Imm(), 2), unknown(), instruction("LDY", new Ldy(), new Zp0(), 3), instruction("LDA", new Lda(), new Zp0(), 3), instruction("LDX", new Ldx(), new Zp0(), 3), unknown(), instruction("TAY", new Tay(), new Imp(), 2), instruction("LDA", new Lda(), new Imm(), 2), instruction("TAX", new Tax(), new Imp(), 2), unknown(), instruction("LDY", new Ldy(), new Abs(), 4), instruction("LDA", new Lda(), new Abs(), 4), instruction("LDX", new Ldx(), new Abs(), 4), unknown(),
            instruction("BCS", new Bcs(), new Rel(), 2), instruction("LDA", new Lda(), new Izy(), 5), unknown(), unknown(), instruction("LDY", new Ldy(), new Zpx(), 4), instruction("LDA", new Lda(), new Zpx(), 4), instruction("LDX", new Ldx(), new Zpy(), 4), unknown(), instruction("CLV", new Clv(), new Imp(), 2), instruction("LDA", new Lda(), new Aby(), 4), instruction("TSX", new Tsx(), new Imp(), 2), unknown(), instruction("LDY", new Ldy(), new Abx(), 4), instruction("LDA", new Lda(), new Abx(), 4), instruction("LDX", new Ldx(), new Aby(), 4), unknown(),
            instruction("CPY", new Cpy(), new Imm(), 2), instruction("CMP", new Cmp(), new Izx(), 6), unknown(), unknown(), instruction("CPY", new Cpy(), new Zp0(), 3), instruction("CMP", new Cmp(), new Zp0(), 3), instruction("DEC", new Dec(), new Zp0(), 5), unknown(), instruction("INY", new Iny(), new Imp(), 2), instruction("CMP", new Cmp(), new Imm(), 2), instruction("DEX", new Dex(), new Imp(), 2), unknown(), instruction("CPY", new Cpy(), new Abs(), 4), instruction("CMP", new Cmp(), new Abs(), 4), instruction("DEC", new Dec(), new Abs(), 6), unknown(),
            instruction("BNE", new Bne(), new Rel(), 2), instruction("CMP", new Cmp(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("CMP", new Cmp(), new Zpx(), 4), instruction("DEC", new Dec(), new Zpx(), 6), unknown(), instruction("CLD", new Cld(), new Imp(), 2), instruction("CMP", new Cmp(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("CMP", new Cmp(), new Abx(), 4), instruction("DEC", new Dec(), new Abx(), 7), unknown(),
            instruction("CPX", new Cpx(), new Imm(), 2), instruction("SBC", new Sbc(), new Izx(), 6), unknown(), unknown(), instruction("CPX", new Cpx(), new Zp0(), 3), instruction("SBC", new Sbc(), new Zp0(), 3), instruction("INC", new Inc(), new Zp0(), 5), unknown(), instruction("INX", new Inx(), new Imp(), 2), instruction("SBC", new Sbc(), new Imm(), 2), instruction("NOP", new Nop(), new Imp(), 2), unknown(), instruction("CPX", new Cpx(), new Abs(), 4), instruction("SBC", new Sbc(), new Abs(), 4), instruction("INC", new Inc(), new Abs(), 6), unknown(),
            instruction("BEQ", new Beq(), new Rel(), 2), instruction("SBC", new Sbc(), new Izy(), 5), unknown(), unknown(), unknown(), instruction("SBC", new Sbc(), new Zpx(), 4), instruction("INC", new Inc(), new Zpx(), 6), unknown(), instruction("SED", new Sed(), new Imp(), 2), instruction("SBC", new Sbc(), new Aby(), 4), unknown(), unknown(), unknown(), instruction("SBC", new Sbc(), new Abx(), 4), instruction("INC", new Inc(), new Abx(), 7), unknown()
    };

    public Olc6502(Bus bus) {
        this.bus = bus;
    }

    public void write(short addr, byte data) {
        bus.write(addr, data);
    }

    public byte read(short addr) {
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

    public void clock() {
        if (remainingCycles == 0) {
            byte opcode = read(programCounter);
            programCounter++;
            Instruction instruction = instructionLookup[opcode];
            remainingCycles = instruction.cycles;
            short additionalCycle1 = widenIgnoreSigning(instruction.addressingMode.set());
            short additionalCycle2 = widenIgnoreSigning(instruction.opcode.operate());
            remainingCycles += additionalCycle1 + additionalCycle2;
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


    //================================  ADDRESSING MODES ====================================

    public abstract class AddressingMode {

        public abstract byte set();

        short readWidened(short addr) {
            return widenIgnoreSigning(read(addr));
        }

        byte read16BitAddressWithOffset(byte offset) {
            short hi = readWidened(programCounter++);
            short lo = readWidened(programCounter++);

            addrAbs = (short) (hi << 8 | lo);
            addrAbs += widenIgnoreSigning(offset);

            if ((addrAbs & 0xFF00) != (hi << 8)) {
                return 1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    /**
     * Immediate Address.
     * The data of the instruction is in the second byte of the instruction.
     * Set the addressAbsolute of the cpu to the incremented programCounter value.
     */
    private class Imm extends AddressingMode {
        @Override
        public byte set() {
            addrAbs = programCounter++;
            return 0;
        }

    }

    /**
     * Implied Addressing.
     * <p>
     * Operand is implicitly stated in the instruction's opcode.
     * However, it could be operating on the accumulator, setting the fetched value to the accumulator register value.
     */
    private class Imp extends AddressingMode {
        @Override
        public byte set() {
            fetched = accumulatorRegister;
            return 0;
        }

    }

    /**
     * Zero Page Addressing.
     */
    private class Zp0 extends AddressingMode {
        @Override
        public byte set() {
            addrAbs = readWidened(programCounter++);
            programCounter += 1;
            addrAbs &= 0x00FF;
            return 0;
        }

    }

    /**
     * Indexed (X) Zero Page Addressing.
     */
    private class Zpx extends AddressingMode {
        @Override
        public byte set() {
            short value = readWidened(programCounter++);
            addrAbs = (short) (value + widenIgnoreSigning(xRegister));
            addrAbs &= 0x00FF;
            return 0;
        }
    }

    /**
     * Indexed (Y) Zero Page Addressing.
     */
    private class Zpy extends AddressingMode {
        @Override
        public byte set() {
            short value = readWidened(programCounter++);
            addrAbs = (short) (value + widenIgnoreSigning(yRegister));
            addrAbs &= 0x00FF;
            return 0;
        }
    }

    /**
     * Absolute Addressing.
     */
    private class Abs extends AddressingMode {
        @Override
        public byte set() {
            return read16BitAddressWithOffset((byte) 0);
        }
    }

    /**
     * Indexed (X) Absolute Addressing.
     */
    private class Abx extends AddressingMode {
        @Override
        public byte set() {
            return read16BitAddressWithOffset(xRegister);
        }
    }

    /**
     * Indexed (Y) Absolute Addressing.
     */
    private class Aby extends AddressingMode {
        @Override
        public byte set() {
            return read16BitAddressWithOffset(yRegister);
        }
    }

    /**
     * Absolute Indirect.
     */
    private class Ind extends AddressingMode {
        @Override
        public byte set() {
            short pointerHi = readWidened(programCounter++);
            short pointerLo = readWidened(programCounter++);

            short pointer = (short) (pointerHi << 8 | pointerLo);
            if (isHardwareBug(pointerLo)) {
                short newHigh = readWidened((short) (pointer & 0xFF00));
                short newLo = readWidened(pointer);
                addrAbs = (short) (newHigh << 8 | newLo);
            } else {
                short newHigh = readWidened((short) (pointer + 1));
                short newLo = readWidened(pointer);
                addrAbs = (short) (newHigh << 8 | newLo);
            }
            return 0;
        }

        private boolean isHardwareBug(short pointerLo) {
            return pointerLo == 0x00FF;
        }
    }

    /**
     * Indexed (X) Indirect Addressing.
     */
    private class Izx extends IndirectWithOffsetAddressMode {

        @Override
        public byte set() {
            return super.set(xRegister);
        }
    }

    /**
     * Indexed (Y) Indirect Addressing.
     */
    private class Izy extends IndirectWithOffsetAddressMode {
        @Override
        public byte set() {
            return super.set(yRegister);
        }
    }

    /**
     * Relative Addressing.
     */
    private class Rel extends AddressingMode {
        @Override
        public byte set() {
            byte operand = read(programCounter++);
            addrRel = widenIgnoreSigning(operand);
            if (operand < 0) {
                addrRel |= 0xFF00;
            }
            return 0;
        }
    }

    private abstract class IndirectWithOffsetAddressMode extends AddressingMode {
        private byte set(byte offset) {
            short pointer = readWidened(programCounter++);

            short widenedOffset = widenIgnoreSigning(offset);
            short lo = readWidened((short) ((pointer + widenedOffset) & 0x00FF));
            short hi = readWidened((short) ((pointer + widenedOffset + 1) & 0x00FF));

            addrAbs = (short) ((hi << 8) | lo);
            addrAbs += widenIgnoreSigning(xRegister);

            if ((addrAbs & 0xFF00) != (hi << 8)) {
                return 1;
            }
            return 0;
        }
    }

    //================================  OPCODES  ============================================


    //================================  UTILITIES  ==========================================

    private Instruction unknown() {
        return instruction("???", new InvalidOpcode(), new Imp(), 8);
    }

    private Instruction instruction(String name, Opcode opcode, AddressingMode addressingMode, int cycles) {
        return new Instruction(name, opcode, addressingMode, cycles);
    }
}
