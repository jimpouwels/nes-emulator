package nesemulator.cpu;

import nesemulator.Bus;
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

import java.util.Arrays;

public class Olc6502 {

    private static final int STACK_ADDRESS = 0x0100;
    private static final int PROGRAM_COUNTER_ADDRESS = 0xFFFC;
    private static final int PROGRAM_COUNTER_ADDRESS_POST_INTERRUPT = 0xFFFE;
    private static final int PROGRAM_COUNTER_ADDRESS_AFTER_NON_MASKABLE_INTERRUPT = 0xFFFA;
    private Bus bus;
    private int accumulatorRegister = 0x00;
    private int xRegister = 0x00;
    private int yRegister = 0x00;
    private int stackPointer = 0x00;
    private int programCounter = 0x0000;
    private int status = 0x00;
    private int fetched = 0x00;
    private int addrAbs = 0x0000;
    private int addrRel = 0x00;
    private int opcode = 0x00;
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

    public Olc6502(Bus bus, byte[] data) {
        this.bus = bus;
        // Write into the correct memory location (write twice while we don't have a mapper)
        bus.writeRomAt(0x8000, Arrays.copyOfRange(data, 0x0010, 0x4000));
        bus.writeRomAt(0xC000, Arrays.copyOfRange(data, 0x0010, 0x4000));
    }

    public void start() {
        programCounter = 0xC000;
        try {
            while (programCounter < 0xC000 + (0x4000 - 0x0010)) {
                clock();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ERROR");
        }
        for (int i = 0; i < 0x0300; i++) {
            System.out.println(read(0x0200 + i));
        }
    }

    public void write(int addr, int data) {
        bus.write(addr, data);
    }

    public int read(int addr) {
        return bus.read(addr, false);
    }

    public int getFlag(Flag flag) {
        return (status & flag.value) > 0 ? 1 : 0;
    }

    public void setFlag(Flag flag) {
        status |= flag.value;
    }

    public void clearFlag(Flag flag) {
        status &= ~flag.value;
    }

    public void clock() {
        if (remainingCycles == 0) {
            int opcode = read(programCounter);
            programCounter++;
            Operation operation = operationLookup[opcode];
            System.out.println(operation.name);
            remainingCycles = operation.cycles;
            // the addressMode returns 1, if it requires an additional clockcycle because a memory page was crossed.
            int additionalCycle1 = operation.addressingMode.set();
            // the instruction returns 1, if one of its possible addressing modes needs an additional clockcycle.
            int additionalCycle2 = operation.instruction.execute();
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
            write16BitToStack(programCounter);

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
    public int rti() {
        status = pullFromStack();
        clearFlag(Flag.BREAK);
        clearFlag(Flag.UNUSED);
        programCounter = pullFromStack(); // FIXME: Can we use the readAndSetProgramCounter() method here?
        programCounter |= pullFromStack() << 8;
        return 0;
    }

    private int read16BitValueFrom(int address) {
        addrAbs = address; // FIXME: Do we really need to assign it to addrAbs here? Or is a local var sufficient? Try out later
        int lo = addrAbs;
        int hi = addrAbs + 1;
        return (hi << 8) | lo;
    }

    private void write16BitToStack(int data) {
        writeToStack((data >> 8) & 0x00FF);
        writeToStack(data & 0x00FF);
    }

    private void writeToStack(int data) {
        write(STACK_ADDRESS + stackPointer--, data); // FIXME: is the mask on 0x00FF needed? Probably not, try out later.
    }

    private int pullFromStack() {
        stackPointer++;
        return read(STACK_ADDRESS + stackPointer);
    }

    private int fetch() {
        if (!(operationLookup[opcode].addressingMode instanceof Imp)) {
            fetched = read(addrAbs);
        }
        // FIXME: The addressing mode "IMP" sets the 'fetched' to the accumulator. Can't we just make that an else statement here? Try out later.
        return fetched; // FIXME: Do we need this as a field? Can't we just have a local variable? Try out later.
    }

    public Operation[] getInstructions() {
        return this.operationLookup;
    }

    //================================  ADDRESSING MODES ====================================

    public abstract class AddressingMode {

        public abstract int set();

        int read16BitAddressWithOffset(int offset) {
            int hi = programCounter++;
            int lo = programCounter++;

            addrAbs = (short) (hi << 8 | lo);
            addrAbs += offset;

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
        public int set() {
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
        public int set() {
            fetched = accumulatorRegister; // FIXME: Can't we just do this in the fetch() method? As part of the else.
            return 0;
        }

    }

    /**
     * Zero Page Addressing.
     */
    private class Zp0 extends AddressingMode {
        @Override
        public int set() {
            addrAbs = programCounter++;
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
        public int set() {
            int value = programCounter++;
            addrAbs = value + xRegister;
            addrAbs &= 0x00FF;
            return 0;
        }
    }

    /**
     * Indexed (Y) Zero Page Addressing.
     */
    private class Zpy extends AddressingMode {
        @Override
        public int set() {
            int value = programCounter++;
            addrAbs = value + yRegister;
            addrAbs &= 0x00FF;
            return 0;
        }
    }

    /**
     * Absolute Addressing.
     */
    private class Abs extends AddressingMode {
        @Override
        public int set() {
            return read16BitAddressWithOffset((byte) 0);
        }
    }

    /**
     * Indexed (X) Absolute Addressing.
     */
    private class Abx extends AddressingMode {
        @Override
        public int set() {
            return read16BitAddressWithOffset(xRegister);
        }
    }

    /**
     * Indexed (Y) Absolute Addressing.
     */
    private class Aby extends AddressingMode {
        @Override
        public int set() {
            return read16BitAddressWithOffset(yRegister);
        }
    }

    /**
     * Absolute Indirect.
     */
    private class Ind extends AddressingMode {
        @Override
        public int set() {
            int pointerHi = programCounter++;
            int pointerLo = programCounter++;

            int pointer = pointerHi << 8 | pointerLo;
            if (isHardwareBug(pointerLo)) {
                int newHigh = pointer & 0xFF00;
                int newLo = pointer;
                addrAbs = newHigh << 8 | newLo;
            } else {
                int newHigh = pointer + 1;
                int newLo = pointer;
                addrAbs = newHigh << 8 | newLo;
            }
            return 0;
        }

        private boolean isHardwareBug(int pointerLo) {
            return pointerLo == 0x00FF;
        }
    }

    /**
     * Indexed (X) Indirect Addressing.
     */
    private class Izx extends IndirectWithOffsetAddressMode {

        @Override
        public int set() {
            return super.set(xRegister);
        }
    }

    /**
     * Indexed (Y) Indirect Addressing.
     */
    private class Izy extends IndirectWithOffsetAddressMode {
        @Override
        public int set() {
            return super.set(yRegister);
        }
    }

    /**
     * Relative Addressing.
     */
    private class Rel extends AddressingMode {
        @Override
        public int set() {
            int operand = read(programCounter++);
            addrRel = operand;
            if (operand < 0) {
                addrRel |= 0xFF00;
            }
            return 0;
        }
    }

    private abstract class IndirectWithOffsetAddressMode extends AddressingMode {
        private int set(int offset) {
            int pointer = programCounter++;

            int widenedOffset = offset;
            int lo = (pointer + widenedOffset) & 0x00FF;
            int hi = (pointer + widenedOffset + 1) & 0x00FF;

            addrAbs = (hi << 8) | lo;
            addrAbs += xRegister;

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
        public int execute() {
            fetch();
            accumulatorRegister &= fetched;
            updateZeroFlag(accumulatorRegister);
            updateNegativeFlag(accumulatorRegister);
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
                addrAbs = programCounter + addrRel;
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
        public int execute() {
            if (getFlag(Flag.CARRY) == 0) {
                remainingCycles++;
                addrAbs = programCounter + addrRel; // FIXME: Do we need the assignment to addrAbs here, couldn't it just be a local var? Try out later.
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
        public int execute() {
            if (getFlag(Flag.ZERO) == 1) {
                remainingCycles++;
                addrAbs = programCounter + addrRel; // FIXME: Do we need the assignment to addrAbs here, couldn't it just be a local var? Try out later.
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
        public int execute() {
            if (getFlag(Flag.ZERO) == 0) {
                remainingCycles++;
                addrAbs = programCounter + addrRel;
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
        public int execute() {
            if (getFlag(Flag.NEGATIVE) == 0) {
                remainingCycles++;
                addrAbs = programCounter + addrRel;
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
        public int execute() {
            if (getFlag(Flag.NEGATIVE) == 1) {
                remainingCycles++;
                addrAbs = programCounter + addrRel;
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
        public int execute() {
            if (getFlag(Flag.OVERFLOW) == 0) {
                remainingCycles++;
                addrAbs = programCounter + addrRel;
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
        public int execute() {
            if (getFlag(Flag.OVERFLOW) == 1) {
                remainingCycles++;
                addrAbs = programCounter + addrRel;
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
            int additionResult = addAndUpdateOverflowFlag(accumulatorRegister, fetched);
            updateCarryBit(additionResult);
            updateZeroFlag(additionResult);
            updateNegativeFlag(additionResult);
            accumulatorRegister = additionResult & 0xFF;
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
            int subtractionResult = subtractAndUpdateOverflowFlag(fetched);
            updateCarryBit(subtractionResult);
            updateZeroFlag(subtractionResult);
            updateNegativeFlag(subtractionResult);
            accumulatorRegister = subtractionResult & 0x00FF;
            return 1;
        }
    }

    /**
     * Push Accumulator on Stack.
     */
    private class Pha extends Instruction {
        @Override
        public int execute() {
            writeToStack(accumulatorRegister);
            return 0;
        }
    }

    /**
     * Pull Accumulator from Stack.
     */
    private class Pla extends Instruction {
        @Override
        public int execute() {
            int value = pullFromStack();
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
            int value = fetched << 1;
            updateCarryBit(value);
            updateZeroFlag(value);
            updateNegativeFlag(value);
            if (operationLookup[opcode].addressingMode instanceof Imp) { // FIXME: Can't we just pass the addressingMode on this method as a param? Try out later.
                accumulatorRegister = value & 0x00FF;
            } else {
                write(addrAbs, value & 0x00FF);
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
            updateZeroFlag(accumulatorRegister & fetched);
            if ((fetched & (1 << 6)) > 0) { // check if bit 6 is set
                setFlag(Flag.OVERFLOW);
            } else {
                clearFlag(Flag.OVERFLOW);
            }
            updateNegativeFlag(fetched);
            return 0;
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
        int temp = accumulatorRegister + value + getFlag(Flag.CARRY);
        if (((temp ^ accumulatorRegister) & (temp ^ value) & 0x0080) > 0) {
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

    private void updateCarryBit(int value) {
        if (value > 0xFF) {
            setFlag(Flag.CARRY);
        } else {
            clearFlag(Flag.CARRY);
        }
    }

    private Operation unknown() {
        return operation("???", new InvalidInstruction(), new Imp(), 8);
    }

    private Operation operation(String name, Instruction instruction, AddressingMode addressingMode, int cycles) {
        return new Operation(name, instruction, addressingMode, cycles);
    }
}
