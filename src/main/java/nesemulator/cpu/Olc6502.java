package nesemulator.cpu;

import nesemulator.Bus;
import nesemulator.cpu.instruction.Instruction;

public class Olc6502 {

    private static final int STACK_ADDRESS = 0x0100;
    private static final int PROGRAM_COUNTER_ADDRESS = 0xFFFC;
    private static final int PROGRAM_COUNTER_ADDRESS_POST_INTERRUPT = 0xFFFE;
    private static final int PROGRAM_COUNTER_ADDRESS_AFTER_NON_MASKABLE_INTERRUPT = 0xFFFA;
    private Bus bus;
    private int accumulatorRegister_8 = 0x00;
    private int xRegister_8 = 0x00;
    private int yRegister_8 = 0x00;
    private int stackPointer_8 = 0x00;
    private int programCounter_16 = 0x0000;
    private int status_8 = 0x00;
    private int fetched_8 = 0x00;
    private int addrAbs_16 = 0x0000;
    private int addrRel_16 = 0x00;
    private int opcode_8 = 0x00;
    private int remainingCycles = 0;
    private int clockCount;
    private Operation[] operationLookup = new Operation[]{
            operation("BRK", new Brk(), new Imm(), 7), operation("ORA", new Ora(), new Izx(), 6), unknown(), unknown(), unknown(), operation("ORA", new Ora(), new Zp0(), 3), operation("ASL", new Asl(), new Zp0(), 5), unknown(), operation("PHP", new Php(), new Imp(), 3), operation("ORA", new Ora(), new Imm(), 2), operation("ASL", new Asl(), new Imp(), 2), unknown(), unknown(), operation("ORA", new Ora(), new Abs(), 6), operation("ASL", new Asl(), new Abs(), 6), unknown(),
            operation("BPL", new Bpl(), new Rel(), 2), operation("ORA", new Ora(), new Izy(), 5), unknown(), unknown(), unknown(), operation("ORA", new Ora(), new Zpx(), 4), operation("ASL", new Ora(), new Zpx(), 6), unknown(), operation("CLC", new Clc(), new Imp(), 2), operation("ORA", new Ora(), new Aby(), 4), unknown(), unknown(), unknown(), operation("ORA", new Ora(), new Abx(), 4), operation("ASL", new Asl(), new Abx(), 7), unknown(),
            operation("JSR", new Jsr(), new Abs(), 6), operation("AND", new And(), new Izx(), 6), unknown(), unknown(), operation("BIT", new Bit(), new Zp0(), 3), operation("AND", new And(), new Zp0(), 3), operation("ROL", new Rol(), new Zp0(), 5), unknown(), operation("PLP", new Plp(), new Imp(), 4), operation("AND", new And(), new Imm(), 2), operation("ROL", new Rol(), new Imp(), 2), unknown(), operation("BIT", new Bit(), new Abs(), 4), operation("AND", new And(), new Abs(), 4), operation("ROL", new Rol(), new Abs(), 6), unknown(),
            operation("BMI", new Bmi(), new Rel(), 2), operation("AND", new And(), new Izy(), 5), unknown(), unknown(), unknown(), operation("AND", new And(), new Zpx(), 4), operation("ROL", new Rol(), new Zpx(), 6), unknown(), operation("SEC", new Sec(), new Imp(), 2), operation("AND", new And(), new Aby(), 4), unknown(), unknown(), unknown(), operation("AND", new And(), new Abx(), 4), operation("ROL", new Rol(), new Abx(), 7), unknown(),
            operation("RTI", new Rti(), new Imp(), 6), operation("EOR", new Eor(), new Izx(), 6), unknown(), unknown(), unknown(), operation("EOR", new Eor(), new Zp0(), 3), operation("LSR", new Lsr(), new Zp0(), 5), unknown(), operation("PHA", new Pha(), new Imp(), 3), operation("EOR", new Eor(), new Imm(), 2), operation("LSR", new Lsr(), new Imp(), 2), unknown(), operation("JMP", new Jmp(), new Abs(), 3, 3), operation("EOR", new Eor(), new Abs(), 4), operation("LSR", new Lsr(), new Abs(), 6), unknown(),
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

    public void connectToBus(Bus bus) {
        this.bus = bus;
    }

    public void clock() {
        if (remainingCycles == 0) {
            opcode_8 = readByte(programCounter_16);
            Operation operation = operationLookup[opcode_8];
            if (operation.instruction instanceof InvalidInstruction) {
                throw new RuntimeException("Invalid instruction, opcode: " + opcode_8);
            }
            setFlag(Flag.UNUSED);
            int programCounterToLog = programCounter_16;
            programCounter_16++;
            remainingCycles = operation.cycles;
            // the addressMode returns 1, if it requires an additional clockcycle because a memory page was crossed.
            int additionalCycle1 = operation.addressingMode.set();
            // the instruction returns 1, if one of its possible addressing modes needs an additional clockcycle.
            int additionalCycle2 = operation.instruction.execute();
            // if both require an additional cycle, add it to the remaining cycles.
            remainingCycles += additionalCycle1 & additionalCycle2;
            System.out.println(
                    printAsHex(programCounterToLog) +
                            " " + printAsHex(opcode_8) +
                            printInstructionOperandBytes(operation, programCounterToLog) +
                            " " + printAsHex(addrAbs_16) +
                            " " + operation.name +
                            " A:" + printAsHex(accumulatorRegister_8) +
                            " X:" + printAsHex(xRegister_8) +
                            " Y:" + printAsHex(yRegister_8) +
                            " P:" + printAsHex(status_8) +
                            " SP:" + printAsHex(stackPointer_8)
            );
        }
        clockCount++;
        remainingCycles--;
    }

    private String printInstructionOperandBytes(Operation operation, int programCounter_16) {
        String result = "";
        for (int i = 1; i < operation.nrOfBytes; i++) {
            result += " " + printAsHex(readByte(programCounter_16 + i));
        }
        return result;
    }

    private String printAsHex(int value) {
        return String.format("%x", value).toUpperCase();
    }

    public void reset() {
        accumulatorRegister_8 = 0x00;
        xRegister_8 = 0x00;
        yRegister_8 = 0x00;
        stackPointer_8 = 0xFD; // FIXME: NesDev says common practice to start at 0xFF --> try out later
        status_8 = 0x00;
        setFlag(Flag.UNUSED);
        setFlag(Flag.DISABLE_INTERRUPTS);

        programCounter_16 = 0xC000;//read2Bytes(PROGRAM_COUNTER_ADDRESS);

        addrRel_16 = 0x0000;
        addrAbs_16 = 0x0000;
        fetched_8 = 0x00;

        remainingCycles = 8;
    }

    /**
     * Interrupt request.
     */
    public void irq() {
        if (getFlag(Flag.DISABLE_INTERRUPTS) == 0) {
            write2BytesToStack(programCounter_16);

            setFlag(Flag.BREAK);
            setFlag(Flag.UNUSED);
            setFlag(Flag.DISABLE_INTERRUPTS);
            writeByteToStack(status_8);

            programCounter_16 = read2Bytes(PROGRAM_COUNTER_ADDRESS_POST_INTERRUPT);

            remainingCycles = 7;
        }
    }

    /**
     * Non maskable interrupt request.
     */
    public void nmi() {
        writeByteToStack(programCounter_16);

        clearFlag(Flag.BREAK);
        setFlag(Flag.UNUSED);
        setFlag(Flag.DISABLE_INTERRUPTS);
        writeByteToStack(status_8);

        programCounter_16 = read2Bytes(PROGRAM_COUNTER_ADDRESS_AFTER_NON_MASKABLE_INTERRUPT);

        remainingCycles = 8;
    }

    /**
     * Return to program after interrupt
     */
    public int rti() {
        status_8 = pullByteFromStack();
        clearFlag(Flag.BREAK);
        clearFlag(Flag.UNUSED);
        programCounter_16 = pullByteFromStack(); // FIXME: Can we use the readAndSetProgramCounter() method here?
        programCounter_16 |= pullByteFromStack() << 8;
        return 0;
    }

    private void write(int address_16, int data_8) {
        bus.cpuWriteByte(address_16, data_8);
    }

    private int readByte(int address_16) {
        return bus.cpuReadByte(address_16, false);
    }

    private int read2Bytes(int address_16) {
        addrAbs_16 = address_16; // FIXME: Do we really need to assign it to addrAbs here? Or is a local var sufficient? Try out later
        int lo = readByte(addrAbs_16);
        int hi = readByte(addrAbs_16 + 1);
        return (hi << 8) | lo;
    }

    private int getFlag(Flag flag) {
        return (status_8 & flag.value_8) > 0 ? 1 : 0;
    }

    private void setFlag(Flag flag) {
        status_8 |= flag.value_8;
    }

    private void clearFlag(Flag flag) {
        status_8 &= ~flag.value_8;
    }

    private void write2BytesToStack(int data_16) {
        writeByteToStack((data_16 >> 8) & 0x00FF);
        writeByteToStack(data_16 & 0x00FF);
    }

    private void writeByteToStack(int data_8) {
        write(STACK_ADDRESS + stackPointer_8--, data_8); // FIXME: is the mask on 0x00FF needed? Probably not, try out later.
    }

    private int pullByteFromStack() {
        stackPointer_8++;
        return readByte(STACK_ADDRESS + stackPointer_8);
    }

    private int pull2BytesFromStack() {
        stackPointer_8++;
        int value = readByte(STACK_ADDRESS + stackPointer_8);
        stackPointer_8++;
        value |= read2Bytes(STACK_ADDRESS + stackPointer_8) << 8;
        return value;
    }

    private int fetch() {
        if (!(operationLookup[opcode_8].addressingMode instanceof Imp)) {
            fetched_8 = readByte(addrAbs_16);
        }
        // FIXME: The addressing mode "IMP" sets the 'fetched' to the accumulator. Can't we just make that an else statement here? Try out later.
        return fetched_8; // FIXME: Do we need this as a field? Can't we just have a local variable? Try out later.
    }

    public Operation[] getInstructions() {
        return this.operationLookup;
    }

    //================================  ADDRESSING MODES ====================================

    public abstract class AddressingMode {

        public abstract int set();

        int read16BitAddressWithOffset(int offset) {
            int low_8 = readByte(programCounter_16++);
            int high_8 = readByte(programCounter_16++);

            addrAbs_16 = high_8 << 8 | low_8;
            addrAbs_16 += offset;

            if ((addrAbs_16 & 0xFF00) != (high_8 << 8)) {
                return 1;
            } else {
                return 0;
            }
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
        public int set() {
            addrAbs_16 = programCounter_16++;
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
        public int set() {
            fetched_8 = accumulatorRegister_8; // FIXME: Can't we just do this in the fetch() method? As part of the else.
            return 0;
        }

    }

    /**
     * Zero Page Addressing.
     */
    private class Zp0 extends AddressingMode {
        @Override
        public int set() {
            addrAbs_16 = readByte(programCounter_16++);
            addrAbs_16 &= 0x00FF; // FIXME: Is this mask really needed? It seems like the left-byte of the address is already 0x00, since the read() returns an 8-bit byte.
            return 0;
        }

    }

    /**
     * Indexed (X) Zero Page Addressing.
     */
    private class Zpx extends AddressingMode {
        @Override
        public int set() {
            int value_8 = readByte(programCounter_16++) + xRegister_8;
            addrAbs_16 = value_8 & 0x00FF;
            return 0;
        }
    }

    /**
     * Indexed (Y) Zero Page Addressing.
     */
    private class Zpy extends AddressingMode {
        @Override
        public int set() {
            int value_8 = readByte(programCounter_16++) + yRegister_8;
            addrAbs_16 = value_8 & 0x00FF;
            return 0;
        }
    }

    /**
     * Absolute Addressing.
     */
    private class Abs extends AddressingMode {
        @Override
        public int set() {
            return read16BitAddressWithOffset(0);
        }
    }

    /**
     * Indexed (X) Absolute Addressing.
     */
    private class Abx extends AddressingMode {
        @Override
        public int set() {
            return read16BitAddressWithOffset(xRegister_8);
        }
    }

    /**
     * Indexed (Y) Absolute Addressing.
     */
    private class Aby extends AddressingMode {
        @Override
        public int set() {
            return read16BitAddressWithOffset(yRegister_8);
        }
    }

    /**
     * Absolute Indirect.
     */
    private class Ind extends AddressingMode {
        @Override
        public int set() {
            int pointerHigh_8 = readByte(programCounter_16++);
            int pointerLow_8 = readByte(programCounter_16++);

            int pointer_16 = pointerHigh_8 << 8 | pointerLow_8;
            if (isHardwareBug(pointerLow_8)) {
                int newHigh_8 = readByte(pointer_16 & 0xFF00);
                int newLow_8 = readByte(pointer_16);
                addrAbs_16 = newHigh_8 << 8 | newLow_8;
            } else {
                int newHigh_8 = readByte(pointer_16 + 1);
                int newLow_8 = readByte(pointer_16);
                addrAbs_16 = newHigh_8 << 8 | newLow_8;
            }
            return 0;
        }
    }

    /**
     * Indexed (X) Indirect Addressing.
     */
    private class Izx extends AddressingMode {
        @Override
        public int set() {
            int pointer_8 = readByte(programCounter_16++);

            int low_8 = readByte((pointer_8 + xRegister_8) & 0x00FF);
            int high_8 = readByte((pointer_8 + xRegister_8 + 1) & 0x00FF);

            addrAbs_16 = (high_8 << 8) | low_8;
            addrAbs_16 += xRegister_8;

            return 0;
        }
    }

    /**
     * Indexed (Y) Indirect Addressing.
     */
    private class Izy extends AddressingMode {
        @Override
        public int set() {
            int pointer_8 = readByte(programCounter_16++);

            int low_8 = readByte((pointer_8 + yRegister_8) & 0x00FF);
            int high_8 = readByte((pointer_8 + yRegister_8 + 1) & 0x00FF);

            addrAbs_16 = (high_8 << 8) | low_8;
            addrAbs_16 += xRegister_8;

            if ((addrAbs_16 & 0xFF00) != (high_8 << 8)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * Relative Addressing.
     */
    private class Rel extends AddressingMode {
        @Override
        public int set() {
            int operand_8 = readByte(programCounter_16++);
            addrRel_16 = operand_8;
            if ((operand_8 & 0x80) > 0) {
                addrRel_16 |= 0xFF00;
            }
            return 0;
        }
    }

    private boolean isHardwareBug(int pointerLow_8) {
        return pointerLow_8 == 0x00FF; // FIXME: Can the mask be 0xFF? Try out later.
    }

    //================================  INSTRUCTIONS  =======================================

    /**
     * "AND" Memory with Accumulator.
     */
    private class And extends Instruction {
        @Override
        public int execute() {
            fetch();
            accumulatorRegister_8 &= fetched_8;
            updateZeroFlag(accumulatorRegister_8);
            updateNegativeFlag(accumulatorRegister_8);
            return 1;
        }
    }

    /**
     * Branch on Carry Set.
     */
    private class Bcs extends Instruction {
        @Override
        public int execute() {
            if (getFlag(Flag.CARRY) == 1) {
                remainingCycles++;
                addrAbs_16 = programCounter_16 + addrRel_16;
                if ((addrAbs_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = addrAbs_16;
            }
            return 0;
        }
    }

    /**
     * Branch on Carry Clear.
     */
    private class Bcc extends Instruction {
        @Override
        public int execute() {
            if (getFlag(Flag.CARRY) == 0) {
                remainingCycles++;
                addrAbs_16 = programCounter_16 + addrRel_16; // FIXME: Do we need the assignment to addrAbs here, couldn't it just be a local var? Try out later.
                if ((addrAbs_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = addrAbs_16;
            }
            return 0;
        }
    }

    /**
     * Branch on Result Zero.
     */
    private class Beq extends Instruction {
        @Override
        public int execute() {
            if (getFlag(Flag.ZERO) == 1) {
                remainingCycles++;
                addrAbs_16 = programCounter_16 + addrRel_16; // FIXME: Do we need the assignment to addrAbs here, couldn't it just be a local var? Try out later.
                if ((addrAbs_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = addrAbs_16;
            }
            return 0;
        }
    }

    /**
     * Branch on Result not Zero.
     */
    private class Bne extends Instruction {
        @Override
        public int execute() {
            if (getFlag(Flag.ZERO) == 0) {
                remainingCycles++;
                addrAbs_16 = programCounter_16 + addrRel_16;
                if ((addrAbs_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = addrAbs_16;
            }
            return 0;
        }
    }

    /**
     * Branch on Result Plus.
     */
    private class Bpl extends Instruction {
        @Override
        public int execute() {
            if (getFlag(Flag.NEGATIVE) == 0) {
                remainingCycles++;
                addrAbs_16 = programCounter_16 + addrRel_16;
                if ((addrAbs_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = addrAbs_16;
            }
            return 0;
        }
    }

    /**
     * Branch on Result Minus.
     */
    private class Bmi extends Instruction {
        @Override
        public int execute() {
            if (getFlag(Flag.NEGATIVE) == 1) {
                remainingCycles++;
                addrAbs_16 = programCounter_16 + addrRel_16;
                if ((addrAbs_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = addrAbs_16;
            }
            return 0;
        }
    }

    /**
     * Branch on Overflow Clear.
     */
    private class Bvc extends Instruction {
        @Override
        public int execute() {
            if (getFlag(Flag.OVERFLOW) == 0) {
                remainingCycles++;
                addrAbs_16 = programCounter_16 + addrRel_16;
                if ((addrAbs_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = addrAbs_16;
            }
            return 0;
        }
    }

    /**
     * Branch on Overflow Set.
     */
    private class Bvs extends Instruction {
        @Override
        public int execute() {
            if (getFlag(Flag.OVERFLOW) == 1) {
                remainingCycles++;
                addrAbs_16 = programCounter_16 + addrRel_16;
                if ((addrAbs_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = addrAbs_16;
            }
            return 0;
        }
    }

    /**
     * Clear Carry Flag.
     */
    private class Clc extends Instruction {
        @Override
        public int execute() {
            clearFlag(Flag.CARRY);
            return 0;
        }
    }

    /**
     * Clear Decimal Mode.
     */
    public class Cld extends Instruction {
        @Override
        public int execute() {
            clearFlag(Flag.DECIMAL_MODE);
            return 0;
        }
    }

    /**
     * Add Memory to Accumulator with Carry.
     */
    private class Adc extends Instruction {
        @Override
        public int execute() {
            fetch();
            int additionResult = addAndUpdateOverflowFlag(accumulatorRegister_8, fetched_8);
            updateCarryBitToValueOfBit7(additionResult);
            updateZeroFlag(additionResult);
            updateNegativeFlag(additionResult);
            accumulatorRegister_8 = additionResult & 0xFF;
            return 1;
        }
    }

    /**
     * Subtract Memory from Accumulator with Borrow.
     */
    private class Sbc extends Instruction {
        @Override
        public int execute() {
            fetch();
            int subtractionResult = subtractAndUpdateOverflowFlag(fetched_8);
            updateCarryBitToValueOfBit7(subtractionResult);
            updateZeroFlag(subtractionResult);
            updateNegativeFlag(subtractionResult);
            accumulatorRegister_8 = subtractionResult & 0x00FF;
            return 1;
        }
    }

    /**
     * Push Accumulator on Stack.
     */
    private class Pha extends Instruction {
        @Override
        public int execute() {
            writeByteToStack(accumulatorRegister_8);
            return 0;
        }
    }

    /**
     * Pull Accumulator from Stack.
     */
    private class Pla extends Instruction {
        @Override
        public int execute() {
            int value = pullByteFromStack();
            updateZeroFlag(value);
            updateNegativeFlag(value);
            return 0;
        }
    }

    /**
     * Shift Left One Bit (Memory or Accumulator).
     */
    private class Asl extends Instruction {
        @Override
        public int execute() {
            fetch();
            int value = fetched_8 << 1;
            updateCarryBitToValueOfBit7(value);
            updateZeroFlag(value);
            updateNegativeFlag(value);
            if (operationLookup[opcode_8].addressingMode instanceof Imp) { // FIXME: Can't we just pass the addressingMode on this method as a param? Try out later.
                accumulatorRegister_8 = value & 0x00FF;
            } else {
                write(addrAbs_16, value & 0x00FF);
            }
            return 0;
        }
    }

    /**
     * Test Bits in Memory with Accumulator.
     */
    private class Bit extends Instruction {
        @Override
        public int execute() {
            fetch();
            updateZeroFlag(accumulatorRegister_8 & fetched_8);
            if ((fetched_8 & (1 << 6)) > 0) { // check if bit 6 is set
                setFlag(Flag.OVERFLOW);
            } else {
                clearFlag(Flag.OVERFLOW);
            }
            updateNegativeFlag(fetched_8);
            return 0;
        }
    }

    /**
     * Jump to New Location.
     */
    public class Jmp extends Instruction {
        @Override
        public int execute() {
            programCounter_16 = addrAbs_16;
            return 0;
        }
    }

    /**
     * Force Break.
     */
    private class Brk extends Instruction {
        @Override
        public int execute() {
            programCounter_16++;
            write2BytesToStack(programCounter_16);
            setFlag(Flag.BREAK);
            writeByteToStack(status_8);
            clearFlag(Flag.BREAK);
            programCounter_16 = read2Bytes(PROGRAM_COUNTER_ADDRESS_POST_INTERRUPT);
            return 0;
        }
    }

    /**
     * Clear Interrupt Disable Bit.
     */
    private class Cli extends Instruction {
        @Override
        public int execute() {
            clearFlag(Flag.DISABLE_INTERRUPTS);
            return 0;
        }
    }

    /**
     * Clear Overflow Flag.
     */
    private class Clv extends Instruction {
        @Override
        public int execute() {
            clearFlag(Flag.OVERFLOW);
            return 0;
        }
    }

    /**
     * Compare Memory and Accumulator.
     */
    private class Cmp extends Instruction {
        @Override
        public int execute() {
            fetch();
            int value_8 = accumulatorRegister_8 - fetched_8;
            if (accumulatorRegister_8 >= fetched_8) {
                setFlag(Flag.CARRY);
            } else {
                clearFlag(Flag.CARRY);
            }
            updateNegativeFlag(value_8);
            updateZeroFlag(value_8);
            return 1;
        }
    }

    /**
     * Compare Memory and Index X.
     */
    private class Cpx extends Instruction {
        @Override
        public int execute() {
            fetch();
            int value_8 = xRegister_8 - fetched_8;
            if (xRegister_8 >= fetched_8) {
                setFlag(Flag.CARRY);
            } else {
                clearFlag(Flag.CARRY);
            }
            updateZeroFlag(value_8); // FIXME: http://obelisk.me.uk/6502/reference.html#CPX --> Says zero flag is set if X = M?
            updateNegativeFlag(value_8);
            return 0;
        }
    }

    /**
     * Compare Memory and Index Y.
     */
    private class Cpy extends Instruction {
        @Override
        public int execute() {
            fetch();
            int value_8 = yRegister_8 - fetched_8;
            if (yRegister_8 >= fetched_8) {
                setFlag(Flag.CARRY);
            } else {
                clearFlag(Flag.CARRY);
            }
            updateZeroFlag(value_8); // FIXME: http://obelisk.me.uk/6502/reference.html#CPY --> Says zero flag is set if X = M?
            updateNegativeFlag(value_8);
            return 0;
        }
    }

    /**
     * Decrement Memory by One.
     */
    private class Dec extends Instruction {
        @Override
        public int execute() {
            fetch();
            int decrementedValue_8 = fetched_8 - 1;
            write(addrAbs_16, decrementedValue_8);
            updateZeroFlag(decrementedValue_8);
            updateNegativeFlag(decrementedValue_8);
            return 0;
        }
    }

    /**
     * Decrement Index X by One.
     */
    private class Dex extends Instruction {
        @Override
        public int execute() {
            xRegister_8--;
            updateNegativeFlag(xRegister_8);
            updateZeroFlag(xRegister_8);
            return 0;
        }
    }

    /**
     * Decrement Index Y by One
     */
    private class Dey extends Instruction {
        @Override
        public int execute() {
            yRegister_8--;
            updateNegativeFlag(yRegister_8);
            updateZeroFlag(yRegister_8);
            return 0;
        }
    }

    /**
     * "Exclusive-OR" Memory with Accumulator.
     */
    private class Eor extends Instruction {
        @Override
        public int execute() {
            fetch();
            accumulatorRegister_8 = accumulatorRegister_8 ^ fetched_8;
            updateZeroFlag(accumulatorRegister_8);
            updateNegativeFlag(accumulatorRegister_8);
            return 1;
        }
    }

    /**
     * Increment Memory by One.
     */
    private class Inc extends Instruction {
        @Override
        public int execute() {
            fetch();
            int value_8 = fetched_8 + 1;
            write(addrAbs_16, value_8);
            updateZeroFlag(value_8);
            updateNegativeFlag(value_8);
            return 0;
        }
    }

    /**
     * Increment Index X by One.
     */
    private class Inx extends Instruction {
        @Override
        public int execute() {
            xRegister_8++;
            updateZeroFlag(xRegister_8);
            updateNegativeFlag(xRegister_8);
            return 0;
        }
    }

    /**
     * Increment Index Y by One.
     */
    private class Iny extends Instruction {
        @Override
        public int execute() {
            xRegister_8++;
            updateZeroFlag(yRegister_8);
            updateNegativeFlag(yRegister_8);
            return 0;
        }
    }

    /**
     * Jump to New Location Saving Return Address.
     */
    private class Jsr extends Instruction {
        @Override
        public int execute() {
            programCounter_16--;
            write2BytesToStack(programCounter_16);
            programCounter_16 = addrAbs_16;
            return 0;
        }
    }

    /**
     * Load Accumulator with Memory.
     */
    private class Lda extends Instruction {
        @Override
        public int execute() {
            fetch();
            accumulatorRegister_8 = fetched_8;
            updateNegativeFlag(accumulatorRegister_8);
            updateZeroFlag(accumulatorRegister_8);
            return 1;
        }
    }

    /**
     * Load Index X with Memory.
     */
    private class Ldx extends Instruction {
        @Override
        public int execute() {
            fetch();
            xRegister_8 = fetched_8;
            updateNegativeFlag(xRegister_8);
            updateZeroFlag(xRegister_8);
            return 1;
        }
    }

    /**
     * Load Index Y with Memory.
     */
    private class Ldy extends Instruction {
        @Override
        public int execute() {
            fetch();
            yRegister_8 = fetched_8;
            updateNegativeFlag(yRegister_8);
            updateZeroFlag(yRegister_8);
            return 1;
        }
    }

    /**
     * Shift One Bit Right (Memory or Accumulator).
     */
    private class Lsr extends Instruction {
        @Override
        public int execute() {
            fetch();
            if ((fetched_8 & 0x0001) > 0) {
                setFlag(Flag.CARRY);
            } else {
                clearFlag(Flag.CARRY);
            }
            int temp = fetched_8 >> 1;
            updateZeroFlag(temp);
            updateNegativeFlag(temp);
            if (operationLookup[opcode_8].addressingMode instanceof Imp) {
                accumulatorRegister_8 = temp;
            } else {
                write(addrAbs_16, temp);
            }
            return 0;
        }
    }

    /**
     * No Operation.
     */
    private class Nop extends Instruction {
        @Override
        public int execute() {
            return 0;
        }
    }

    /**
     * "OR" Memory with Accumulator.
     */
    private class Ora extends Instruction {
        @Override
        public int execute() {
            fetch();
            accumulatorRegister_8 |= fetched_8;
            updateZeroFlag(accumulatorRegister_8);
            updateNegativeFlag(accumulatorRegister_8);
            return 1;
        }
    }

    /**
     * Push Processor Status on Stack.
     */
    private class Php extends Instruction {
        @Override
        public int execute() {
            writeByteToStack(status_8 | Flag.BREAK.value_8 | Flag.UNUSED.value_8);
            clearFlag(Flag.BREAK); // FIXME: NEEDED?
            clearFlag(Flag.UNUSED); // FIXME: NEEDED?
            return 0;
        }
    }

    /**
     * Pull Processor Status from Stack.
     */
    private class Plp extends Instruction {
        @Override
        public int execute() {
            status_8 = pullByteFromStack();
            setFlag(Flag.UNUSED);
            return 0;
        }
    }

    /**
     * Rotate One Bit Left (Memory or Accumulator).
     */
    private class Rol extends Instruction {
        @Override
        public int execute() {
            fetch();
            if ((fetched_8 & 0xFF) > 0) {
                setFlag(Flag.CARRY);
            } else {
                clearFlag(Flag.CARRY); // FIXME: Javidx9 is doing "SetFlag(C, temp & 0xFF00);" here... after the temp assignment, is that correct/better?
            }
            int temp = (fetched_8 << 1 | getFlag(Flag.CARRY)) & 0xFF;
            updateNegativeFlag(temp);
            updateZeroFlag(temp);
            if (operationLookup[opcode_8].addressingMode instanceof Imp) {
                accumulatorRegister_8 = temp;
            } else {
                write(addrAbs_16, temp);
            }
            return 0;
        }
    }

    /**
     * Rotate One Bit Right (Memory or Accumulator).
     */
    private class Ror extends Instruction {
        @Override
        public int execute() {
            fetch();
            int temp = (getFlag(Flag.CARRY) << 7) | (fetched_8 >> 1);
            updateCarryBitToValueOfBit1(temp);
            updateZeroFlag(temp);
            updateNegativeFlag(temp);
            if (operationLookup[opcode_8].addressingMode instanceof Imp) {
                accumulatorRegister_8 = temp;
            } else {
                write(addrAbs_16, temp);
            }
            return 0;
        }
    }

    /**
     * Return from Interrupt.
     */
    private class Rti extends Instruction {
        @Override
        public int execute() {
            status_8 = pullByteFromStack();
            clearFlag(Flag.UNUSED);
            clearFlag(Flag.BREAK);
            programCounter_16 = pull2BytesFromStack();
            return 0;
        }
    }

    /**
     * Return from Subroutine.
     */
    private class Rts extends Instruction {
        @Override
        public int execute() {
            status_8 = pullByteFromStack();
            clearFlag(Flag.BREAK);
            clearFlag(Flag.UNUSED);

            programCounter_16 = pull2BytesFromStack();
            return 0;
        }
    }

    /**
     * Set Carry Flag.
     */
    private class Sec extends Instruction {
        @Override
        public int execute() {
            setFlag(Flag.CARRY);
            return 0;
        }
    }

    /**
     * Set Decimal Mode.
     */
    private class Sed extends Instruction {
        @Override
        public int execute() {
            setFlag(Flag.DECIMAL_MODE);
            return 0;
        }
    }

    /**
     * Set Interrupt Disable Status.
     */
    private class Sei extends Instruction {
        @Override
        public int execute() {
            setFlag(Flag.DISABLE_INTERRUPTS);
            return 0;
        }
    }

    /**
     * Store Accumulator in Memory.
     */
    private class Sta extends Instruction {
        @Override
        public int execute() {
            write(addrAbs_16, accumulatorRegister_8);
            return 0;
        }
    }

    /**
     * Store Index X in Memory.
     */
    private class Stx extends Instruction {
        @Override
        public int execute() {
            write(addrAbs_16, xRegister_8);
            return 0;
        }
    }

    /**
     * Store Index Y in Memory.
     */
    private class Sty extends Instruction {
        @Override
        public int execute() {
            write(addrAbs_16, yRegister_8);
            return 0;
        }
    }

    /**
     * Transfer Accumulator to Index X.
     */
    private class Tax extends Instruction {
        @Override
        public int execute() {
            xRegister_8 = accumulatorRegister_8;
            updateZeroFlag(xRegister_8);
            updateNegativeFlag(xRegister_8);
            return 0;
        }
    }

    /**
     * Transfer Accumulator to Index Y.
     */
    private class Tay extends Instruction {
        @Override
        public int execute() {
            yRegister_8 = accumulatorRegister_8;
            updateZeroFlag(yRegister_8);
            updateNegativeFlag(yRegister_8);
            return 0;
        }
    }

    /**
     * Transfer Stack Pointer to Index X.
     */
    private class Tsx extends Instruction {
        @Override
        public int execute() {
            xRegister_8 = stackPointer_8;
            updateZeroFlag(xRegister_8);
            updateNegativeFlag(xRegister_8);
            return 0;
        }
    }

    /**
     * Transfer Index X to Accumulator.
     */
    private class Txa extends Instruction {
        @Override
        public int execute() {
            accumulatorRegister_8 = xRegister_8;
            updateZeroFlag(accumulatorRegister_8);
            updateNegativeFlag(accumulatorRegister_8);
            return 0;
        }
    }

    /**
     * Transfer Index X to Stack Register.
     */
    private class Txs extends Instruction {
        @Override
        public int execute() {
            stackPointer_8 = xRegister_8;
            return 0;
        }
    }

    /**
     * Transfer Index Y to Accumulator.
     */
    private class Tya extends Instruction {
        @Override
        public int execute() {
            accumulatorRegister_8 = yRegister_8;
            updateZeroFlag(accumulatorRegister_8);
            updateNegativeFlag(accumulatorRegister_8);
            return 0;
        }
    }

    /**
     * Invalid Opcode.
     */
    private class InvalidInstruction extends Instruction {
        @Override
        public int execute() {
            return 0;
        }

        @Override
        public String toString() {
            return "???";
        }
    }

    //================================  UTILITIES  ==========================================

    private void updateZeroFlag(int value) {
        if (value == 0x00) {
            setFlag(Flag.ZERO);
        } else {
            clearFlag(Flag.ZERO);
        }
    }

    private int addAndUpdateOverflowFlag(int byteA, int byteB) {
        int sum = byteA + byteB + getFlag(Flag.CARRY);
        if ((byteA & 0x80) > 0 && (byteB & 0x80) > 0 && (sum & 0x80) > 0 ||
                (byteA & 0x80) == 0 && (byteB & 0x80) == 0 && (sum & 0x80) == 0) {
            setFlag(Flag.OVERFLOW);
        } else {
            clearFlag(Flag.OVERFLOW);
        }
        return sum;
    }

    private int subtractAndUpdateOverflowFlag(int valueToSubtract) {
        int value = valueToSubtract ^ 0x00FF;
        int temp = accumulatorRegister_8 + value + getFlag(Flag.CARRY);
        if (((temp ^ accumulatorRegister_8) & (temp ^ value) & 0x0080) > 0) {
            setFlag(Flag.OVERFLOW);
        } else {
            clearFlag(Flag.OVERFLOW);
        }
        return temp;
    }

    private void updateNegativeFlag(int value) {
        if ((value & 0x80) > 0) {
            setFlag(Flag.NEGATIVE);
        } else {
            clearFlag(Flag.NEGATIVE);
        }
    }

    private void updateCarryBitToValueOfBit7(int value) {
        if (value > 0xFF) {
            setFlag(Flag.CARRY);
        } else {
            clearFlag(Flag.CARRY);
        }
    }

    private void updateCarryBitToValueOfBit1(int value) {
        if ((value & 0x01) > 0) {
            setFlag(Flag.CARRY);
        } else {
            clearFlag(Flag.CARRY);
        }
    }

    private Operation unknown() {
        return operation("???", new InvalidInstruction(), new Imp(), 8);
    }

    private Operation operation(String name, Instruction instruction, AddressingMode addressingMode, int cycles) {
        return operation(name, instruction, addressingMode, cycles, -1);
    }

    private Operation operation(String name, Instruction instruction, AddressingMode addressingMode, int cycles, int nrOfBytes) {
        return new Operation(name, instruction, addressingMode, cycles, nrOfBytes);
    }
}
