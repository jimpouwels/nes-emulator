package nl.pouwels.nes.cpu;

import nl.pouwels.nes.Bus;
import nl.pouwels.nes.cpu.instruction.Instruction;
import nl.pouwels.nes.utils.ByteUtilities;

import java.util.ArrayList;
import java.util.List;

import static nl.pouwels.nes.utils.PrintUtilities.printAsHex;

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
            operation("BRK", new Brk(), new Imm(), 7, 1), operation("ORA", new Ora(), new Izx(), 6, 2), unknown(), unknown(), operation("*NOP", new Nop(), new Imm(), 3, 2), operation("ORA", new Ora(), new Zp0(), 3, 2), operation("ASL", new Asl(), new Zp0(), 5, 2), unknown(), operation("PHP", new Php(), new Imp(), 3), operation("ORA", new Ora(), new Imm(), 2, 2), operation("ASL", new Asl(), new Imp(), 2, 1), unknown(), operation("*NOP", new Nop(), new Imm(), 4, 3), operation("ORA", new Ora(), new Abs(), 6, 3), operation("ASL", new Asl(), new Abs(), 6, 3), unknown(),
            operation("BPL", new Bpl(), new Rel(), 2, 2), operation("ORA", new Ora(), new Izy(), 5, 2), unknown(), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("ORA", new Ora(), new Zpx(), 4, 2), operation("ASL", new Asl(), new Zpx(), 6, 2), unknown(), operation("CLC", new Clc(), new Imp(), 2), operation("ORA", new Ora(), new Aby(), 4, 3), operation("*NOP", new Nop(), new Imp(), 2, 1), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("ORA", new Ora(), new Abx(), 4, 3), operation("ASL", new Asl(), new Abx(), 7, 3), unknown(),
            operation("JSR", new Jsr(), new Abs(), 6, 3), operation("AND", new And(), new Izx(), 6, 2), unknown(), unknown(), operation("BIT", new Bit(), new Zp0(), 3, 2), operation("AND", new And(), new Zp0(), 3, 2), operation("ROL", new Rol(), new Zp0(), 5, 2), unknown(), operation("PLP", new Plp(), new Imp(), 4, 1), operation("AND", new And(), new Imm(), 2, 2), operation("ROL", new Rol(), new Imp(), 2, 1), unknown(), operation("BIT", new Bit(), new Abs(), 4, 3), operation("AND", new And(), new Abs(), 4, 3), operation("ROL", new Rol(), new Abs(), 6, 3), unknown(),
            operation("BMI", new Bmi(), new Rel(), 2, 2), operation("AND", new And(), new Izy(), 5, 2), unknown(), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("AND", new And(), new Zpx(), 4, 2), operation("ROL", new Rol(), new Zpx(), 6, 2), unknown(), operation("SEC", new Sec(), new Imp(), 2, 1), operation("AND", new And(), new Aby(), 4, 3), unknown(), unknown(), unknown(), operation("AND", new And(), new Abx(), 4, 3), operation("ROL", new Rol(), new Abx(), 7, 3), unknown(),
            operation("RTI", new Rti(), new Imp(), 6), operation("EOR", new Eor(), new Izx(), 6, 2), unknown(), unknown(), operation("*NOP", new Nop(), new Imm(), 3, 2), operation("EOR", new Eor(), new Zp0(), 3, 2), operation("LSR", new Lsr(), new Zp0(), 5, 2), unknown(), operation("PHA", new Pha(), new Imp(), 3), operation("EOR", new Eor(), new Imm(), 2, 2), operation("LSR", new Lsr(), new Imp(), 2, 1), unknown(), operation("JMP", new Jmp(), new Abs(), 3, 3), operation("EOR", new Eor(), new Abs(), 4, 3), operation("LSR", new Lsr(), new Abs(), 6, 3), unknown(),
            operation("BVC", new Bvc(), new Rel(), 2, 2), operation("EOR", new Eor(), new Izy(), 5, 2), unknown(), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("EOR", new Eor(), new Zpx(), 4, 2), operation("LSR", new Lsr(), new Zpx(), 6, 2), unknown(), operation("CLI", new Cli(), new Imp(), 2), operation("EOR", new Eor(), new Aby(), 4, 3), operation("*NOP", new Nop(), new Imp(), 2, 1), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("EOR", new Eor(), new Abx(), 4, 3), operation("LSR", new Lsr(), new Abx(), 7, 3), unknown(),
            operation("RTS", new Rts(), new Imp(), 6, 1), operation("ADC", new Adc(), new Izx(), 6, 2), unknown(), unknown(), operation("*NOP", new Nop(), new Imm(), 3, 2), operation("ADC", new Adc(), new Zp0(), 3, 2), operation("ROR", new Ror(), new Zp0(), 5, 2), unknown(), operation("PLA", new Pla(), new Imp(), 4), operation("ADC", new Adc(), new Imm(), 2, 2), operation("ROR", new Ror(), new Imp(), 2, 1), unknown(), operation("JMP", new Jmp(), new Ind(), 5, 3), operation("ADC", new Adc(), new Abs(), 4, 3), operation("ROR", new Ror(), new Abs(), 6, 3), unknown(),
            operation("BVS", new Bvs(), new Rel(), 2, 2), operation("ADC", new Adc(), new Izy(), 5, 2), unknown(), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("ADC", new Adc(), new Zpx(), 4, 2), operation("ROR", new Ror(), new Zpx(), 6, 2), unknown(), operation("SEI", new Sei(), new Imp(), 2), operation("ADC", new Adc(), new Aby(), 4, 3), operation("*NOP", new Nop(), new Imp(), 2, 1), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("ADC", new Adc(), new Abx(), 4, 3), operation("ROR", new Ror(), new Abx(), 7, 3), unknown(),
            unknown(), operation("STA", new Sta(), new Izx(), 6, 2), unknown(), unknown(), operation("STY", new Sty(), new Zp0(), 3, 2), operation("STA", new Sta(), new Zp0(), 3, 2), operation("STX", new Stx(), new Zp0(), 3, 2), unknown(), operation("DEY", new Dey(), new Imp(), 2), operation("*NOP", new Nop(), new Imp(), 2, 1), operation("TXA", new Txa(), new Imp(), 2), unknown(), operation("STY", new Sty(), new Abs(), 4, 3), operation("STA", new Sta(), new Abs(), 4, 3), operation("STX", new Stx(), new Abs(), 4, 3), unknown(),
            operation("BCC", new Bcc(), new Rel(), 2, 2), operation("STA", new Sta(), new Izy(), 6, 2), operation("*NOP", new Nop(), new Imp(), 2, 1), unknown(), operation("STY", new Sty(), new Zpx(), 4, 2), operation("STA", new Sta(), new Zpx(), 4, 2), operation("STX", new Stx(), new Zpy(), 4, 2), unknown(), operation("TYA", new Tya(), new Imp(), 2), operation("STA", new Sta(), new Aby(), 5, 3), operation("TXS", new Txs(), new Imp(), 2), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("STA", new Sta(), new Abx(), 5, 3), unknown(), unknown(),
            operation("LDY", new Ldy(), new Imm(), 2, 2), operation("LDA", new Lda(), new Izx(), 6, 2), operation("LDX", new Ldx(), new Imm(), 2, 2), unknown(), operation("LDY", new Ldy(), new Zp0(), 3, 2), operation("LDA", new Lda(), new Zp0(), 3, 2), operation("LDX", new Ldx(), new Zp0(), 3, 2), unknown(), operation("TAY", new Tay(), new Imp(), 2), operation("LDA", new Lda(), new Imm(), 2, 2), operation("TAX", new Tax(), new Imp(), 2), unknown(), operation("LDY", new Ldy(), new Abs(), 4, 3), operation("LDA", new Lda(), new Abs(), 4, 3), operation("LDX", new Ldx(), new Abs(), 4, 3), unknown(),
            operation("BCS", new Bcs(), new Rel(), 2, 2), operation("LDA", new Lda(), new Izy(), 5, 2), unknown(), unknown(), operation("LDY", new Ldy(), new Zpx(), 4, 2), operation("LDA", new Lda(), new Zpx(), 4, 2), operation("LDX", new Ldx(), new Zpy(), 4, 2), unknown(), operation("CLV", new Clv(), new Imp(), 2), operation("LDA", new Lda(), new Aby(), 4, 3), operation("TSX", new Tsx(), new Imp(), 2), unknown(), operation("LDY", new Ldy(), new Abx(), 4, 3), operation("LDA", new Lda(), new Abx(), 4, 3), operation("LDX", new Ldx(), new Aby(), 4, 3), unknown(),
            operation("CPY", new Cpy(), new Imm(), 2, 2), operation("CMP", new Cmp(), new Izx(), 6, 2), operation("*NOP", new Nop(), new Imp(), 4, 1), unknown(), operation("CPY", new Cpy(), new Zp0(), 3, 2), operation("CMP", new Cmp(), new Zp0(), 3, 2), operation("DEC", new Dec(), new Zp0(), 5, 2), unknown(), operation("INY", new Iny(), new Imp(), 2), operation("CMP", new Cmp(), new Imm(), 2, 2), operation("DEX", new Dex(), new Imp(), 2), unknown(), operation("CPY", new Cpy(), new Abs(), 4, 3), operation("CMP", new Cmp(), new Abs(), 4, 3), operation("DEC", new Dec(), new Abs(), 6, 3), unknown(),
            operation("BNE", new Bne(), new Rel(), 2, 2), operation("CMP", new Cmp(), new Izy(), 5, 2), unknown(), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("CMP", new Cmp(), new Zpx(), 4, 2), operation("DEC", new Dec(), new Zpx(), 6, 2), unknown(), operation("CLD", new Cld(), new Imp(), 2), operation("CMP", new Cmp(), new Aby(), 4, 3), operation("*NOP", new Nop(), new Imp(), 2, 1), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("CMP", new Cmp(), new Abx(), 4, 3), operation("DEC", new Dec(), new Abx(), 7, 3), unknown(),
            operation("CPX", new Cpx(), new Imm(), 2, 2), operation("SBC", new Sbc(), new Izx(), 6, 2), operation("*NOP", new Nop(), new Imp(), 2, 1), unknown(), operation("CPX", new Cpx(), new Zp0(), 3, 2), operation("SBC", new Sbc(), new Zp0(), 3, 2), operation("INC", new Inc(), new Zp0(), 5, 2), unknown(), operation("INX", new Inx(), new Imp(), 2), operation("SBC", new Sbc(), new Imm(), 2, 2), operation("NOP", new Nop(), new Imp(), 2), unknown(), operation("CPX", new Cpx(), new Abs(), 4, 3), operation("SBC", new Sbc(), new Abs(), 4, 3), operation("INC", new Inc(), new Abs(), 6, 3), unknown(),
            operation("BEQ", new Beq(), new Rel(), 2, 2), operation("SBC", new Sbc(), new Izy(), 5, 2), unknown(), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("SBC", new Sbc(), new Zpx(), 4, 2), operation("INC", new Inc(), new Zpx(), 6, 2), unknown(), operation("SED", new Sed(), new Imp(), 2), operation("SBC", new Sbc(), new Aby(), 4, 3), operation("*NOP", new Nop(), new Imp(), 2, 1), unknown(), operation("*NOP", new Nop(), new Imp(), 4, 1), operation("SBC", new Sbc(), new Abx(), 4, 3), operation("INC", new Inc(), new Abx(), 7, 3), unknown()
    };
    private EventHandler eventHandler;

    public Olc6502(EventPrinter eventHandler) {
        this.eventHandler = eventHandler;
    }

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
            eventHandler.onNewInstruction(operation, opcode_8, programCounter_16, readInstructionOperands(programCounter_16, operation.nrOfBytes), accumulatorRegister_8, xRegister_8, yRegister_8, status_8, stackPointer_8, clockCount);
            increaseProgramCounter();
            remainingCycles = operation.cycles;
            // the addressMode returns 1, if it requires an additional clockcycle because a memory page was crossed.
            int additionalCycle1 = operation.addressingMode.set();
            // the instruction returns 1, if one of its possible addressing modes needs an additional clockcycle.
            int additionalCycle2 = operation.instruction.execute();
            // if both require an additional cycle, add it to the remaining cycles.
            remainingCycles += additionalCycle1 & additionalCycle2;
        }
        clockCount++;
        remainingCycles--;
    }

    public boolean isInstructionCompleted() {
        return remainingCycles == 0;
    }

    public int getProgramCounter_16() {
        return programCounter_16;
    }

    public int getFlag(Flag flag) {
        return (status_8 & flag.value_8) > 0 ? 1 : 0;
    }

    public int getAccumolatorRegister() {
        return accumulatorRegister_8;
    }

    public int getXRegister() {
        return xRegister_8;
    }

    public int getYRegister() {
        return yRegister_8;
    }

    public int getStackPointer() {
        return stackPointer_8;
    }

    private int[] readInstructionOperands(int startAddress_16, int nrOfBytes) {
        if (nrOfBytes == -1) {
            return new int[0];
        }
        int[] bytes = new int[nrOfBytes - 1];
        for (int i = 1; i < nrOfBytes; i++) {
            bytes[i - 1] = readByte(startAddress_16 + i);
        }
        return bytes;
    }

    public void reset(int programCounterStart_16) {
        accumulatorRegister_8 = 0x00;
        xRegister_8 = 0x00;
        yRegister_8 = 0x00;
        stackPointer_8 = 0xFD;
        status_8 = 0x00;
        setFlag(Flag.UNUSED);
        setFlag(Flag.DISABLE_INTERRUPTS);

        programCounter_16 = programCounterStart_16;

        addrRel_16 = 0x0000;
        addrAbs_16 = 0x0000;
        fetched_8 = 0x00;

        remainingCycles = 8;
    }

    public void reset() {
        reset(read2Bytes(PROGRAM_COUNTER_ADDRESS));
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
        write2BytesToStack(programCounter_16);

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
        int lowByte = readByte(address_16);
        int highByte = readByte(address_16 + 1);
        return (highByte << 8) | lowByte;
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
        write(STACK_ADDRESS + stackPointer_8, data_8);
        if (stackPointer_8 == 0x00) {
            stackPointer_8 = 0xFF;
        } else {
            stackPointer_8--;
        }
    }

    private int pullByteFromStack() {
        if (stackPointer_8 == 0xFF) {
            stackPointer_8 = 0x00;
        } else {
            stackPointer_8++;
        }
        return readByte(STACK_ADDRESS + stackPointer_8);
    }

    private int pull2BytesFromStack() {
        int lowByte = pullByteFromStack();
        int highByte = pullByteFromStack();
        return lowByte | highByte << 8;
    }

    private int fetch() {
        if (!(operationLookup[opcode_8].addressingMode instanceof Imp)) {
            fetched_8 = readByte(addrAbs_16);
        }
        // FIXME: The addressing mode "IMP" sets the 'fetched' to the accumulator. Can't we just make that an else statement here? Try out later.
        return fetched_8; // FIXME: Do we need this as a field? Can't we just have a local variable? Try out later.
    }

    private void increaseProgramCounter() {
        if (programCounter_16 == 0xFFFF) {
            programCounter_16 = 0x0000;
        } else {
            programCounter_16++;
        }
    }

    //================================  ADDRESSING MODES ====================================

    public abstract class AddressingMode {

        public abstract int set();

        int read16BitAddressWithOffset(int offset) {
            int low_8 = readByte(programCounter_16);
            increaseProgramCounter();
            int high_8 = readByte(programCounter_16);
            increaseProgramCounter();

            addrAbs_16 = high_8 << 8 | low_8;
            addrAbs_16 = (addrAbs_16 + offset) & 0xFFFF; // mask if exceeds address range

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
            addrAbs_16 = programCounter_16;
            increaseProgramCounter();
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
            addrAbs_16 = readByte(programCounter_16);
            increaseProgramCounter();
            return 0;
        }

    }

    /**
     * Indexed (X) Zero Page Addressing.
     */
    private class Zpx extends AddressingMode {
        @Override
        public int set() {
            int value_8 = readByte(programCounter_16) + xRegister_8;
            increaseProgramCounter();
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
            int value_8 = readByte(programCounter_16) + yRegister_8;
            increaseProgramCounter();
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
            int pointerLow_8 = readByte(programCounter_16);
            increaseProgramCounter();
            int pointerHigh_8 = readByte(programCounter_16);
            increaseProgramCounter();

            int pointer_16 = pointerHigh_8 << 8 | pointerLow_8;
            if (isHardwareBug(pointerLow_8)) {
                addrAbs_16 = (readByte(pointer_16 & 0xFF00) << 8) | readByte(pointer_16 + 0);
            } else {
                addrAbs_16 = (readByte(pointer_16 + 1) << 8) | readByte(pointer_16 + 0);
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
            int pointer_8 = readByte(programCounter_16);
            increaseProgramCounter();

            int low_8 = readByte((pointer_8 + xRegister_8) & 0x00FF);
            int high_8 = readByte((pointer_8 + xRegister_8 + 1) & 0x00FF);

            addrAbs_16 = (high_8 << 8) | low_8;
            return 0;
        }
    }

    /**
     * Indexed (Y) Indirect Addressing.
     */
    private class Izy extends AddressingMode {
        @Override
        public int set() {
            int pointer_8 = readByte(programCounter_16);
            increaseProgramCounter();

            int low_8 = readByte(pointer_8);
            int high_8 = readByte((pointer_8 + 1) & 0xFF);

            addrAbs_16 = (high_8 << 8) | low_8;
            addrAbs_16 = (addrAbs_16 + yRegister_8) & 0xFFFF;

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
            addrRel_16 = readByte(programCounter_16);
            increaseProgramCounter();
            if ((addrRel_16 & 0x80) > 0) {
                addrRel_16 |= 0xFFFFFF00;
            }
            return 0;
        }
    }

    private boolean isHardwareBug(int pointerLow_8) {
        return pointerLow_8 == 0xFF;
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
                int address_16 = programCounter_16 + addrRel_16;
                if ((address_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = address_16;
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
                int address_16 = programCounter_16 + addrRel_16;
                if ((address_16 & 0xFF00) != (programCounter_16 & 0xFF00)) {
                    remainingCycles++;
                }
                programCounter_16 = address_16;
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
            updateCarryBitToValueOfBit8(additionResult);
            additionResult = ByteUtilities.unsetBit(additionResult, 8);
            updateZeroFlag(additionResult);
            updateNegativeFlag(additionResult);
            accumulatorRegister_8 = additionResult;
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
            updateCarryBitToValueOfBit8(subtractionResult);
            subtractionResult = ByteUtilities.unsetBit(subtractionResult, 8);
            updateZeroFlag(subtractionResult);
            updateNegativeFlag(subtractionResult);
            accumulatorRegister_8 = subtractionResult;
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
            accumulatorRegister_8 = pullByteFromStack();
            updateZeroFlag(accumulatorRegister_8);
            updateNegativeFlag(accumulatorRegister_8);
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
            updateCarryBitToValueOfBit8(value);
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
            increaseProgramCounter();
            setFlag(Flag.DISABLE_INTERRUPTS);
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
            updateZeroFlag(value_8);
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
            updateZeroFlag(value_8);
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
            int decrementedValue_8 = 0xFF;
            if (fetched_8 != 0x00) {
                decrementedValue_8 = fetched_8 - 1;
            }
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
            if (xRegister_8 == 0x00) {
                xRegister_8 = 0xFF;
            } else {
                xRegister_8--;
            }
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
            if (yRegister_8 == 0x00) {
                yRegister_8 = 0xFF;
            } else {
                yRegister_8--;
            }
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
            int value_8 = 0x00;
            if (fetched_8 < 0xFF) {
                value_8 = fetched_8 + 1;
            }
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
            if (xRegister_8 == 0xFF) {
                xRegister_8 = 0x00;
            } else {
                xRegister_8++;
            }
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
            if (yRegister_8 == 0xFF) {
                yRegister_8 = 0;
            } else {
                yRegister_8++;
            }
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
            accumulatorRegister_8 = fetched_8 & 0xFF;
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
            switch (opcode_8) {
                case 0x1C:
                case 0x3C:
                case 0x5C:
                case 0x7C:
                case 0xDC:
                case 0xFC:
                    return 1;
            }
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
            writeByteToStack(status_8 | Flag.UNUSED.value_8 | Flag.BREAK.value_8);
            return 0;
        }
    }

    /**
     * Pull Processor Status from Stack.
     */
    private class Plp extends Instruction {
        @Override
        public int execute() {
            status_8 = pullByteFromStack() & 0xEF;
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
            int temp = (fetched_8 << 1) | getFlag(Flag.CARRY);
            updateCarryBitToValueOfBit8(temp);
            temp = ByteUtilities.unsetBit(temp, 8);
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
            updateCarryBitToValueOfBit0(fetched_8);
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
            programCounter_16 = pull2BytesFromStack();
            increaseProgramCounter();
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
            return -1;
        }

        @Override
        public String toString() {
            return "???";
        }

    }

    //================================  UTILITIES  ==========================================

    private void updateZeroFlag(int value) {
        if ((value & 0x00FF) == 0x00) {
            setFlag(Flag.ZERO);
        } else {
            clearFlag(Flag.ZERO);
        }
    }

    private int addAndUpdateOverflowFlag(int byteA, int byteB) {
        int sum = byteA + byteB + getFlag(Flag.CARRY);
        if ((byteA & 0x80) > 0 && (byteB & 0x80) > 0 && (sum & 0x80) == 0 ||
                (byteA & 0x80) == 0 && (byteB & 0x80) == 0 && (sum & 0x80) > 0) {
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

    private void updateCarryBitToValueOfBit8(int value) {
        if (value > 0xFF) {
            setFlag(Flag.CARRY);
        } else {
            clearFlag(Flag.CARRY);
        }
    }

    private void updateCarryBitToValueOfBit0(int value) {
        if ((value & 0x01) > 0) {
            setFlag(Flag.CARRY);
        } else {
            clearFlag(Flag.CARRY);
        }
    }

    private Operation unknown() {
        return operation("???", new InvalidInstruction(), new Imp(), 8, 1);
    }

    private Operation operation(String name, Instruction instruction, AddressingMode addressingMode, int cycles) {
        return operation(name, instruction, addressingMode, cycles, -1);
    }

    private Operation operation(String name, Instruction instruction, AddressingMode addressingMode, int cycles, int nrOfBytes) {
        return new Operation(name, instruction, addressingMode, cycles, nrOfBytes);
    }


    public List<InstructionAtAddress> disassemble(int nStart, int nStop) {
        int addr = nStart;
        int value = 0x00, lo = 0x00, hi = 0x00;
        List<InstructionAtAddress> mapLines = new ArrayList<>();
        int line_addr = 0;

        // Starting at the specified address we read an instruction
        // byte, which in turn yields information from the lookup table
        // as to how many additional bytes we need to read and what the
        // addressing mode is. I need this info to assemble human readable
        // syntax, which is different depending upon the addressing mode

        // As the instruction is decoded, a std::string is assembled
        // with the readable output
        while (addr <= nStop) {
            line_addr = addr;

            // Prefix line with instruction address
            String sInst = "$" + printAsHex(addr, 4) + ": ";

            // Read instruction, and get its readable name
            int opcode = readByte(addr);
            addr++;
            sInst += operationLookup[opcode].name + " ";

            // Get oprands from desired locations, and form the
            // instruction based upon its addressing mode. These
            // routines mimmick the actual fetch routine of the
            // 6502 in order to get accurate data as part of the
            // instruction
            if (operationLookup[opcode].addressingMode instanceof Imp) {
                sInst += " {IMP}";
            } else if (operationLookup[opcode].addressingMode instanceof Imm) {
                value = readByte(addr);
                addr++;
                sInst += "#$" + printAsHex(value, 2) + " {IMM}";
            } else if (operationLookup[opcode].addressingMode instanceof Zp0) {
                lo = readByte(addr);
                addr++;
                hi = 0x00;
                sInst += "$" + printAsHex(lo, 2) + " {ZP0}";
            } else if (operationLookup[opcode].addressingMode instanceof Zpx) {
                lo = readByte(addr);
                addr++;
                hi = 0x00;
                sInst += "$" + printAsHex(lo, 2) + ", X {ZPX}";
            } else if (operationLookup[opcode].addressingMode instanceof Zpy) {
                lo = readByte(addr);
                addr++;
                hi = 0x00;
                sInst += "$" + printAsHex(lo, 2) + ", Y {ZPY}";
            } else if (operationLookup[opcode].addressingMode instanceof Izx) {
                lo = readByte(addr);
                addr++;
                hi = 0x00;
                sInst += "($" + printAsHex(lo, 2) + ", X) {IZX}";
            } else if (operationLookup[opcode].addressingMode instanceof Izy) {
                lo = readByte(addr);
                addr++;
                hi = 0x00;
                sInst += "($" + printAsHex(lo, 2) + "), Y {IZY}";
            } else if (operationLookup[opcode].addressingMode instanceof Abs) {
                lo = readByte(addr);
                addr++;
                hi = readByte(addr);
                addr++;
                sInst += "$" + printAsHex((hi << 8) | lo, 4) + " {ABS}";
            } else if (operationLookup[opcode].addressingMode instanceof Abx) {
                lo = readByte(addr);
                addr++;
                hi = readByte(addr);
                addr++;
                sInst += "$" + printAsHex((hi << 8) | lo, 4) + ", X {ABX}";
            } else if (operationLookup[opcode].addressingMode instanceof Aby) {
                lo = readByte(addr);
                addr++;
                hi = readByte(addr);
                addr++;
                sInst += "$" + printAsHex((hi << 8) | lo, 4) + ", Y {ABY}";
            } else if (operationLookup[opcode].addressingMode instanceof Ind) {
                lo = readByte(addr);
                addr++;
                hi = readByte(addr);
                addr++;
                sInst += "($" + printAsHex((hi << 8) | lo, 4) + ") {IND}";
            } else if (operationLookup[opcode].addressingMode instanceof Rel) {
                value = readByte(addr);
                addr++;
                sInst += "$" + printAsHex(value, 2) + " [$" + printAsHex(addr + value, 4) + "] {REL}";
            }

            // Add the formed string to a std::map, using the instruction's
            // address as the key. This makes it convenient to look for later
            // as the instructions are variable in length, so a straight up
            // incremental index is not sufficient.
            mapLines.add(new InstructionAtAddress(line_addr, sInst));
        }

        return mapLines;
    }

    public class InstructionAtAddress {
        public int address;
        public String line;

        public InstructionAtAddress(int addr, String line) {
            this.address = addr;
            this.line = line;
        }
    }

}
