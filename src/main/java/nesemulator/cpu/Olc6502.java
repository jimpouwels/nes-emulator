package nesemulator.cpu;

import nesemulator.Bus;
import nesemulator.cpu.instruction.Asl;
import nesemulator.cpu.instruction.Bit;
import nesemulator.cpu.instruction.Brk;
import nesemulator.cpu.instruction.Cli;
import nesemulator.cpu.instruction.Clv;
import nesemulator.cpu.instruction.Cmp;
import nesemulator.cpu.instruction.Cpx;
import nesemulator.cpu.instruction.Cpy;
import nesemulator.cpu.instruction.Dec;
import nesemulator.cpu.instruction.Dex;
import nesemulator.cpu.instruction.Dey;
import nesemulator.cpu.instruction.Eor;
import nesemulator.cpu.instruction.Inc;
import nesemulator.cpu.instruction.Instruction;
import nesemulator.cpu.instruction.InvalidInstruction;
import nesemulator.cpu.instruction.Inx;
import nesemulator.cpu.instruction.Iny;
import nesemulator.cpu.instruction.Jmp;
import nesemulator.cpu.instruction.Jsr;
import nesemulator.cpu.instruction.Lda;
import nesemulator.cpu.instruction.Ldx;
import nesemulator.cpu.instruction.Ldy;
import nesemulator.cpu.instruction.Lsr;
import nesemulator.cpu.instruction.Nop;
import nesemulator.cpu.instruction.Ora;
import nesemulator.cpu.instruction.Php;
import nesemulator.cpu.instruction.Plp;
import nesemulator.cpu.instruction.Rol;
import nesemulator.cpu.instruction.Ror;
import nesemulator.cpu.instruction.Rti;
import nesemulator.cpu.instruction.Rts;
import nesemulator.cpu.instruction.Sec;
import nesemulator.cpu.instruction.Sed;
import nesemulator.cpu.instruction.Sei;
import nesemulator.cpu.instruction.Sta;
import nesemulator.cpu.instruction.Stx;
import nesemulator.cpu.instruction.Sty;
import nesemulator.cpu.instruction.Tax;
import nesemulator.cpu.instruction.Tay;
import nesemulator.cpu.instruction.Tsx;
import nesemulator.cpu.instruction.Txa;
import nesemulator.cpu.instruction.Txs;
import nesemulator.cpu.instruction.Tya;

import static nesemulator.utils.ByteUtilities.widenIgnoreSigning;

public class Olc6502 {

    private static final int STACK_ADDRESS = 0x0100;
    private static final int PROGRAM_COUNTER_ADDRESS = 0xFFFC;
    private static final int PROGRAM_COUNTER_ADDRESS_POST_INTERRUPT = 0xFFFE;
    private static final int PROGRAM_COUNTER_ADDRESS_AFTER_NON_MASKABLE_INTERRUPT = 0xFFFA;
    private Bus bus;
    private byte accumulatorRegister = 0x00;
    private byte xRegister = 0x00;
    private byte yRegister = 0x00;
    private byte stackPointer = 0x00;
    private short programCounter = 0x0000;
    private byte status = 0x00;
    private byte fetched = 0x00;
    private short addrAbs = 0x0000;
    private short addrRel = 0x00;
    private byte opcode = 0x00;
    private int remainingCycles = 0;
    private Operation[] operationLookup = new Operation[]{
            operation("BRK", new Brk(), new Imp(), 7), operation("ORA", new Ora(), new Izx(), 6), unknown(), unknown(), unknown(), operation("ORA", new Ora(), new Zp0(), 3), operation("ASL", new Asl(), new Zp0(), 5), unknown(), operation("PHP", new Php(), new Imp(), 3), operation("ORA", new Ora(), new Imm(), 2), operation("ASL", new Asl(), new Imp(), 2), unknown(), unknown(), operation("ORA", new Ora(), new Abs(), 6), operation("ASL", new Asl(), new Abs(), 6), unknown(),
            operation("BPL", new Bpl(), new Rel(), 2), operation("ORA", new Ora(), new Izy(), 5), unknown(), unknown(), unknown(), operation("ORA", new Ora(), new Zpx(), 4), operation("ASL", new Ora(), new Zpx(), 6), unknown(), operation("CLC", new Clc(), new Imp(), 2), operation("ORA", new Ora(), new Aby(), 4), unknown(), unknown(), unknown(), operation("ORA", new Ora(), new Abx(), 4), operation("ASL", new Asl(), new Abx(), 7), unknown(),
            operation("JSR", new Jsr(), new Abs(), 6), operation("AND", new And(), new Izx(), 6), unknown(), unknown(), operation("BIT", new Bit(), new Zp0(), 3), operation("AND", new And(), new Zp0(), 3), operation("ROL", new Rol(), new Zp0(), 5), unknown(), operation("PLP", new Plp(), new Imp(), 4), operation("AND", new And(), new Imm(), 2), operation("ROL", new Rol(), new Imp(), 2), unknown(), operation("BIT", new Bit(), new Abs(), 4), operation("AND", new And(), new Abs(), 4), operation("ROL", new Rol(), new Abs(), 6), unknown(),
            operation("BMI", new Bmi(), new Rel(), 2), operation("AND", new And(), new Izy(), 5), unknown(), unknown(), unknown(), operation("AND", new And(), new Zpx(), 4), operation("ROL", new Rol(), new Zpx(), 6), unknown(), operation("SEC", new Sec(), new Imp(), 2), operation("AND", new And(), new Aby(), 4), unknown(), unknown(), unknown(), operation("AND", new And(), new Abx(), 4), operation("ROL", new Rol(), new Abx(), 7), unknown(),
            operation("RTI", new Rti(), new Imp(), 6), operation("EOR", new Eor(), new Izx(), 6), unknown(), unknown(), unknown(), operation("EOR", new Eor(), new Zp0(), 3), operation("LSR", new Lsr(), new Zp0(), 5), unknown(), operation("PHA", new Pha(), new Imp(), 3), operation("EOR", new Eor(), new Imm(), 2), operation("LSR", new Lsr(), new Imp(), 2), unknown(), operation("JMP", new Jmp(), new Abs(), 3), operation("EOR", new Eor(), new Abs(), 4), operation("LSR", new Lsr(), new Abs(), 6), unknown(),
            operation("BVC", new Bvc(), new Rel(), 2), operation("EOR", new Eor(), new Izy(), 5), unknown(), unknown(), unknown(), operation("EOR", new Eor(), new Zpx(), 4), operation("LSR", new Lsr(), new Zpx(), 6), unknown(), operation("CLI", new Cli(), new Imp(), 2), operation("EOR", new Eor(), new Aby(), 4), unknown(), unknown(), unknown(), operation("EOR", new Eor(), new Abx(), 4), operation("LSR", new Lsr(), new Abx(), 7), unknown(),
            operation("RTS", new Rts(), new Imp(), 6), operation("ADC", new Adc(), new Izx(), 6), unknown(), unknown(), unknown(), operation("ADC", new Adc(), new Zp0(), 3), operation("ROR", new Ror(), new Zp0(), 5), unknown(), operation("PLA", new Pla(), new Imp(), 4), operation("ADC", new Adc(), new Imm(), 2), operation("ROR", new Ror(), new Imp(), 2), unknown(), operation("JMP", new Jmp(), new Ind(), 5), operation("ADC", new Adc(), new Abs(), 4), operation("ROR", new Ror(), new Abs(), 6), unknown(),
            operation("BVS", new Bvs(), new Rel(), 2), operation("ADC", new Adc(), new Izy(), 5), unknown(), unknown(), unknown(), operation("ADC", new Adc(), new Zpx(), 4), operation("ROR", new Ror(), new Zpx(), 6), unknown(), operation("SEI", new Sei(), new Imp(), 2), operation("ADC", new Adc(), new Aby(), 4), unknown(), unknown(), unknown(), operation("ADC", new Adc(), new Abx(), 4), operation("ROR", new Ror(), new Abx(), 7), unknown(),
            unknown(), operation("STA", new Sta(), new Izx(), 6), unknown(), unknown(), operation("STY", new Sty(), new Zp0(), 3), operation("STA", new Sta(), new Zp0(), 3), operation("STX", new Stx(), new Zp0(), 3), unknown(), operation("DEY", new Dey(), new Imp(), 2), unknown(), operation("TXA", new Txa(), new Imp(), 2), unknown(), operation("STY", new Sty(), new Abs(), 4), operation("STA", new Sta(), new Abs(), 4), operation("STX", new Stx(), new Abs(), 4), unknown(),
            operation("BCC", new Bcc(), new Rel(), 2), operation("STA", new Sta(), new Izy(), 6), unknown(), unknown(), operation("STY", new Sty(), new Zpx(), 4), operation("STA", new Sta(), new Zpx(), 4), operation("STX", new Stx(), new Zpy(), 4), unknown(), operation("TYA", new Tya(), new Imp(), 2), operation("STA", new Sta(), new Aby(), 5), operation("TXS", new Txs(), new Imp(), 2), unknown(), unknown(), operation("STA", new Sta(), new Abx(), 5), unknown(), unknown(),
            operation("LDY", new Ldy(), new Imm(), 2), operation("LDA", new Lda(), new Izx(), 6), operation("LDX", new Ldx(), new Imm(), 2), unknown(), operation("LDY", new Ldy(), new Zp0(), 3), operation("LDA", new Lda(), new Zp0(), 3), operation("LDX", new Ldx(), new Zp0(), 3), unknown(), operation("TAY", new Tay(), new Imp(), 2), operation("LDA", new Lda(), new Imm(), 2), operation("TAX", new Tax(), new Imp(), 2), unknown(), operation("LDY", new Ldy(), new Abs(), 4), operation("LDA", new Lda(), new Abs(), 4), operation("LDX", new Ldx(), new Abs(), 4), unknown(),
            operation("BCS", new Bcs(), new Rel(), 2), operation("LDA", new Lda(), new Izy(), 5), unknown(), unknown(), operation("LDY", new Ldy(), new Zpx(), 4), operation("LDA", new Lda(), new Zpx(), 4), operation("LDX", new Ldx(), new Zpy(), 4), unknown(), operation("CLV", new Clv(), new Imp(), 2), operation("LDA", new Lda(), new Aby(), 4), operation("TSX", new Tsx(), new Imp(), 2), unknown(), operation("LDY", new Ldy(), new Abx(), 4), operation("LDA", new Lda(), new Abx(), 4), operation("LDX", new Ldx(), new Aby(), 4), unknown(),
            operation("CPY", new Cpy(), new Imm(), 2), operation("CMP", new Cmp(), new Izx(), 6), unknown(), unknown(), operation("CPY", new Cpy(), new Zp0(), 3), operation("CMP", new Cmp(), new Zp0(), 3), operation("DEC", new Dec(), new Zp0(), 5), unknown(), operation("INY", new Iny(), new Imp(), 2), operation("CMP", new Cmp(), new Imm(), 2), operation("DEX", new Dex(), new Imp(), 2), unknown(), operation("CPY", new Cpy(), new Abs(), 4), operation("CMP", new Cmp(), new Abs(), 4), operation("DEC", new Dec(), new Abs(), 6), unknown(),
            operation("BNE", new Bne(), new Rel(), 2), operation("CMP", new Cmp(), new Izy(), 5), unknown(), unknown(), unknown(), operation("CMP", new Cmp(), new Zpx(), 4), operation("DEC", new Dec(), new Zpx(), 6), unknown(), operation("CLD", new Cld(), new Imp(), 2), operation("CMP", new Cmp(), new Aby(), 4), unknown(), unknown(), unknown(), operation("CMP", new Cmp(), new Abx(), 4), operation("DEC", new Dec(), new Abx(), 7), unknown(),
            operation("CPX", new Cpx(), new Imm(), 2), operation("SBC", new Sbc(), new Izx(), 6), unknown(), unknown(), operation("CPX", new Cpx(), new Zp0(), 3), operation("SBC", new Sbc(), new Zp0(), 3), operation("INC", new Inc(), new Zp0(), 5), unknown(), operation("INX", new Inx(), new Imp(), 2), operation("SBC", new Sbc(), new Imm(), 2), operation("NOP", new Nop(), new Imp(), 2), unknown(), operation("CPX", new Cpx(), new Abs(), 4), operation("SBC", new Sbc(), new Abs(), 4), operation("INC", new Inc(), new Abs(), 6), unknown(),
            operation("BEQ", new Beq(), new Rel(), 2), operation("SBC", new Sbc(), new Izy(), 5), unknown(), unknown(), unknown(), operation("SBC", new Sbc(), new Zpx(), 4), operation("INC", new Inc(), new Zpx(), 6), unknown(), operation("SED", new Sed(), new Imp(), 2), operation("SBC", new Sbc(), new Aby(), 4), unknown(), unknown(), unknown(), operation("SBC", new Sbc(), new Abx(), 4), operation("INC", new Inc(), new Abx(), 7), unknown()
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

    public byte getFlag(Flag flag) {
        return (byte) (widenIgnoreSigning((byte) (status & flag.value)) > 0 ? 1 : 0);
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
            Operation operation = operationLookup[opcode];
            remainingCycles = operation.cycles;
            // the addressMode returns 1, if it requires an additional clockcycle because a memory page was crossed.
            short additionalCycle1 = widenIgnoreSigning(operation.addressingMode.set());
            // the instruction returns 1, if one of its possible addressing modes needs an additional clockcycle.
            short additionalCycle2 = widenIgnoreSigning(operation.instruction.execute());
            // if both require an additional cycle, add it to the remaining cycles.
            remainingCycles += additionalCycle1 & additionalCycle2;
        }
        remainingCycles--;
    }

    public void reset() {
        accumulatorRegister = 0x00;
        xRegister = 0x00;
        yRegister = 0x00;
        stackPointer = (byte) 0xFD; // FIXME: NesDev says common practice to start at 0xFF --> try out later
        status = getFlag(Flag.UNUSED);

        programCounter = read16BitValueFrom((short) PROGRAM_COUNTER_ADDRESS);

        addrRel = 0x0000;
        addrAbs = 0x0000;
        fetched = 0x00;

        remainingCycles = 8;
    }

    /**
     * Interrupt request.
     */
    public void irq() {
        if (getFlag(Flag.DISABLE_INTERRUPTS) == 0) {
            writeToStack(programCounter);

            clearFlag(Flag.BREAK);
            setFlag(Flag.UNUSED);
            setFlag(Flag.DISABLE_INTERRUPTS);
            writeToStack(status);

            programCounter = read16BitValueFrom((short) PROGRAM_COUNTER_ADDRESS_POST_INTERRUPT);

            remainingCycles = 7;
        }
    }

    /**
     * Non maskable interrupt request.
     */
    public void nmi() {
        writeToStack(programCounter);

        clearFlag(Flag.BREAK);
        setFlag(Flag.UNUSED);
        setFlag(Flag.DISABLE_INTERRUPTS);
        writeToStack(status);

        programCounter = read16BitValueFrom((short) PROGRAM_COUNTER_ADDRESS_AFTER_NON_MASKABLE_INTERRUPT);

        remainingCycles = 8;
    }

    /**
     * Return to program after interrupt
     */
    public byte rti() {
        status = pullFromStack();
        clearFlag(Flag.BREAK);
        clearFlag(Flag.UNUSED);
        programCounter = pullFromStack(); // FIXME: Can we use the readAndSetProgramCounter() method here?
        programCounter |= pullFromStack() << 8;
        return 0;
    }

    private short read16BitValueFrom(short address) {
        addrAbs = address; // FIXME: Do we really need to assign it to addrAbs here? Or is a local var sufficient? Try out later
        short lo = widenIgnoreSigning(read(addrAbs));
        short hi = widenIgnoreSigning(read((short) (addrAbs + 1)));
        return (short) ((hi << 8) | lo);
    }

    private void writeToStack(short data) {
        writeToStack((byte) ((data >> 8) & 0x00FF));
        writeToStack((byte) (data & 0x00FF));
    }

    private void writeToStack(byte data) {
        write((short) (STACK_ADDRESS + widenIgnoreSigning(stackPointer--)), data); // FIXME: is the mask on 0x00FF needed? Probably not, try out later.
    }

    private byte pullFromStack() {
        stackPointer++;
        return read((short) (STACK_ADDRESS + widenIgnoreSigning(stackPointer)));
    }

    private byte fetch() {
        if (!(operationLookup[opcode].addressingMode instanceof Imp)) {
            fetched = read(addrAbs);
        }
        return fetched;
    }

    public Operation[] getInstructions() {
        return this.operationLookup;
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
            addrAbs &= 0x00FF; // FIXME: Is this mask really needed? It seems like the left-byte of the address is already 0x00, since the read() returns an 8-bit byte.
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

    //================================  INSTRUCTIONS  =======================================

    /**
     * "AND" Memory with Accumulator.
     */
    private class And extends Instruction {
        @Override
        public byte execute() {
            fetch();
            accumulatorRegister &= fetched;
            updateZeroFlag(widenIgnoreSigning(accumulatorRegister));
            updateNegativeFlag(accumulatorRegister);
            return 1;
        }
    }

    /**
     * Branch on Carry Set.
     */
    private class Bcs extends Instruction {
        @Override
        public byte execute() {
            if (getFlag(Flag.CARRY) == 1) {
                remainingCycles++;
                addrAbs = (short) (programCounter + addrRel);
                if ((addrAbs & 0xFF00) != (programCounter & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter = addrAbs;
            }
            return 0;
        }
    }

    /**
     * Branch on Carry Clear.
     */
    private class Bcc extends Instruction {
        @Override
        public byte execute() {
            if (getFlag(Flag.CARRY) == 0) {
                remainingCycles++;
                addrAbs = (short) (programCounter + addrRel); // FIXME: Do we need the assignment to addrAbs here, couldn't it just be a local var? Try out later.
                if ((addrAbs & 0xFF00) != (programCounter & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter = addrAbs;
            }
            return 0;
        }
    }

    /**
     * Branch on Result Zero.
     */
    private class Beq extends Instruction {
        @Override
        public byte execute() {
            if (getFlag(Flag.ZERO) == 1) {
                remainingCycles++;
                addrAbs = (short) (programCounter + addrRel); // FIXME: Do we need the assignment to addrAbs here, couldn't it just be a local var? Try out later.
                if ((addrAbs & 0xFF00) != (programCounter & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter = addrAbs;
            }
            return 0;
        }
    }

    /**
     * Branch on Result not Zero.
     */
    private class Bne extends Instruction {
        @Override
        public byte execute() {
            if (getFlag(Flag.ZERO) == 0) {
                remainingCycles++;
                addrAbs = (short) (programCounter + addrRel);
                if ((addrAbs & 0xFF00) != (programCounter & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter = addrAbs;
            }
            return 0;
        }
    }

    /**
     * Branch on Result Plus.
     */
    private class Bpl extends Instruction {
        @Override
        public byte execute() {
            if (getFlag(Flag.NEGATIVE) == 0) {
                remainingCycles++;
                addrAbs = (short) (programCounter + addrRel);
                if ((addrAbs & 0xFF00) != (programCounter & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter = addrAbs;
            }
            return 0;
        }
    }

    /**
     * Branch on Result Minus.
     */
    private class Bmi extends Instruction {
        @Override
        public byte execute() {
            if (getFlag(Flag.NEGATIVE) == 1) {
                remainingCycles++;
                addrAbs = (short) (programCounter + addrRel);
                if ((addrAbs & 0xFF00) != (programCounter & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter = addrAbs;
            }
            return 0;
        }
    }

    /**
     * Branch on Overflow Clear.
     */
    private class Bvc extends Instruction {
        @Override
        public byte execute() {
            if (getFlag(Flag.OVERFLOW) == 0) {
                remainingCycles++;
                addrAbs = (short) (programCounter + addrRel);
                if ((addrAbs & 0xFF00) != (programCounter & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter = addrAbs;
            }
            return 0;
        }
    }

    /**
     * Branch on Overflow Set.
     */
    private class Bvs extends Instruction {
        @Override
        public byte execute() {
            if (getFlag(Flag.OVERFLOW) == 1) {
                remainingCycles++;
                addrAbs = (short) (programCounter + addrRel);
                if ((addrAbs & 0xFF00) != (programCounter & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter = addrAbs;
            }
            return 0;
        }
    }

    /**
     * Clear Carry Flag.
     */
    private class Clc extends Instruction {
        @Override
        public byte execute() {
            clearFlag(Flag.CARRY);
            return 0;
        }
    }

    /**
     * Clear Decimal Mode.
     */
    public class Cld extends Instruction {
        @Override
        public byte execute() {
            clearFlag(Flag.DECIMAL_MODE);
            return 0;
        }
    }

    /**
     * Add Memory to Accumulator with Carry.
     */
    private class Adc extends Instruction {
        @Override
        public byte execute() {
            fetch();
            short additionResult = addAndUpdateOverflowFlag(accumulatorRegister, fetched);
            updateCarryBit(additionResult);
            updateZeroFlag(additionResult);
            updateNegativeFlag(additionResult);
            accumulatorRegister = (byte) (additionResult & 0xFF);
            return 1;
        }
    }

    /**
     * Subtract Memory from Accumulator with Borrow.
     */
    private class Sbc extends Instruction {
        @Override
        public byte execute() {
            fetch();
            short subtractionResult = subtractAndUpdateOverflowFlag();
            updateCarryBit(subtractionResult);
            updateZeroFlag(subtractionResult);
            updateNegativeFlag(subtractionResult);
            accumulatorRegister = (byte) (subtractionResult & 0x00FF);
            return 1;
        }
    }

    /**
     * Push Accumulator on Stack.
     */
    private class Pha extends Instruction {
        @Override
        public byte execute() {
            writeToStack(accumulatorRegister);
            return 0;
        }
    }

    /**
     * Pull Accumulator from Stack.
     */
    private class Pla extends Instruction {
        @Override
        public byte execute() {
            byte value = pullFromStack();
            updateZeroFlag(widenIgnoreSigning(value));
            updateNegativeFlag(value);
            return 0;
        }
    }

    private void updateZeroFlag(short value) {
        if (value == 0x00) {
            setFlag(Flag.ZERO);
        } else {
            clearFlag(Flag.ZERO);
        }
    }

    private void updateNegativeFlag(byte value) {
        if (value < 0) {
            setFlag(Flag.NEGATIVE);
        } else {
            clearFlag(Flag.NEGATIVE);
        }
    }

    private short addAndUpdateOverflowFlag(byte byteA, byte byteB) {
        short sum = (short) (widenIgnoreSigning(byteA) + widenIgnoreSigning(byteB) + widenIgnoreSigning(getFlag(Flag.CARRY)));
        if (byteA < 0 && byteB < 0 && (sum & 0x80) > 0 ||
                byteA > 0 && byteB > 0 && (sum & 0x80) == 0) {
            setFlag(Flag.OVERFLOW);
        } else {
            clearFlag(Flag.OVERFLOW);
        }
        return sum;
    }

    private short subtractAndUpdateOverflowFlag() {
        short value = (short) (widenIgnoreSigning(fetched) ^ 0x00FF);
        short temp = (short) (widenIgnoreSigning(accumulatorRegister) + value + widenIgnoreSigning(getFlag(Flag.CARRY)));
        if (((temp ^ widenIgnoreSigning(accumulatorRegister)) & (temp ^ value) & 0x0080) > 0) {
            setFlag(Flag.OVERFLOW);
        } else {
            clearFlag(Flag.OVERFLOW);
        }
        return temp;
    }

    private void updateNegativeFlag(short value) {
        if ((value & 0x80) > 0) {
            setFlag(Flag.NEGATIVE);
        } else {
            clearFlag(Flag.NEGATIVE);
        }
    }

    private void updateCarryBit(short value) {
        if (value > 0xFF) {
            setFlag(Flag.CARRY);
        } else {
            clearFlag(Flag.CARRY);
        }
    }

    //================================  UTILITIES  ==========================================

    private Operation unknown() {
        return operation("???", new InvalidInstruction(), new Imp(), 8);
    }

    private Operation operation(String name, Instruction instruction, AddressingMode addressingMode, int cycles) {
        return new Operation(name, instruction, addressingMode, cycles);
    }
}
