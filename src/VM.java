import java.io.IOException;

class VM {
    // Useful constants
    private final int B16 = 0xFFFF;
    private final int B15 = 0x7FFF;
    private final String[] OPERANDS = {"halt", "set", "push", "pop", "eq", "gt", "jmp", "jt", "jf", "add", "mult", "mod", "and", "or", "not", "rmem", "wmem", "call", "ret", "out", "in", "noop"};
    private final int[] OP_LENGTH = {0, 2, 1, 1, 3, 3, 1, 2, 2, 3, 3, 3, 3, 3, 2, 2, 2, 1, 0, 1, 1, 0};

    // Program Counter and Stack Pointer
    private int PC;
    private int SP;

    // The memory + registers; the stack
    private int[] memory;
    private int[] stack;

    // RUN flag
    private boolean RUN;

    // The file parser
    private Disassembler DVM;

    VM() {
        // Memory runs from 0 to 32767, registers from 32768 to 32775
        memory = new int[32776];
        stack = new int[1];

        // TODO: Ensure the memory is populated somewhere.  Right now, I'm only reading the binary directly
        for (int i = 0; i < memory.length; i++) {
            memory[i] = 0;
        }

        // Set RUN flag to true;
        RUN = true;

        // Set initial registers
        SP = 0;
        PC = 0;
    }

    void loadProgram(String theProgram) {
        DVM = new Disassembler(theProgram);
        memory = DVM.loadProgram(theProgram);
        PC = 0; // Per the spec, set the program counter to 0
    }

    void step() {
        executeCommand(getNextOpcode());
    }

    int[] getNextOpcode() {
        int code = memory[PC++];
        int[] returnCodes = new int[OP_LENGTH[code] + 1];
        returnCodes[0] = code;

        for (int i = 1; i < returnCodes.length; i++)
            returnCodes[i] = memory[PC++];

        return returnCodes;
    }

    private void executeCommand(int[] opr) {
        if (opr[0] == 0)
            halt();
        else if (opr[0] == 1)
            set(opr[1], opr[2]);
        else if (opr[0] == 2)
            push(opr[1]);
        else if (opr[0] == 3)
            pop(opr[1]);
        else if (opr[0] == 4)
            eq(opr[1], opr[2], opr[3]);
        else if (opr[0] == 5)
            gt(opr[1], opr[2], opr[3]);
        else if (opr[0] == 6)
            jmp(opr[1]);
        else if (opr[0] == 7)
            jt(opr[1], opr[2]);
        else if (opr[0] == 8)
            jf(opr[1], opr[2]);
        else if (opr[0] == 9)
            add(opr[1], opr[2], opr[3]);
        else if (opr[0] == 10)
            mult(opr[1], opr[2], opr[3]);
        else if (opr[0] == 11)
            mod(opr[1], opr[2], opr[3]);
        else if (opr[0] == 12)
            and(opr[1], opr[2], opr[3]);
        else if (opr[0] == 13)
            or(opr[1], opr[2], opr[3]);
        else if (opr[0] == 14)
            not(opr[1], opr[2]);
        else if (opr[0] == 15)
            rmem(opr[1], opr[2]);
        else if (opr[0] == 16)
            wmem(opr[1], opr[2]);
        else if (opr[0] == 17)
            call(opr[1]);
        else if (opr[0] == 18)
            ret();
        else if (opr[0] == 19)
            out(opr[1]);
        else if (opr[0] == 20)
            in(opr[1]);
        else if (opr[0] == 21)
            noop();
    }

    public void enableRun() {
        RUN = true;
    }

    // All the operands
    // opcode 0: halt
    private void halt() {
        RUN = false;
        // advancePC(0);
        coreDump();
        System.exit(0);
    }

    // opcode 1: set a, b
    private void set(int a, int b) {
        memory[a] = b;
        // advancePC2);
    }

    // opcode 2: push a
    private void push(int a) {
        stack[SP++] = a;
        // advancePC1);
    }

    // opcode 3: pop a
    private void pop(int a) {
        if (SP == 0) {
            System.err.println("%-STACK-UNDERFLOW");
            System.exit(100);
        }
        memory[a] = stack[--SP];

        // advancePC1);
    }

    // opcode 4: eq
    private void eq(int a, int b, int c) {
        if (b == c)
            memory[a] = 1;
        else
            memory[a] = 0;

        // advancePC3);
    }

    // opcode 5: gt
    private void gt(int a, int b, int c) {
        if (b > c)
            memory[a] = 1;
        else
            memory[a] = 0;

        // advancePC3);
    }

    // opcode 6: jmp
    private void jmp(int a) {
        PC = a;
    }

    // opcode 7: jt
    private void jt(int a, int b) {
        if (a > 0)
            jmp(b);
    }

    // opcode 8: jf
    private void jf(int a, int b) {
        if (a == 0)
            jmp(b);
    }

    // opcode 9: add
    private void add(int a, int b, int c) {
        memory[a] = (b + c) % B16;
        // advancePC3);
    }

    // opcode 10: mult
    private void mult(int a, int b, int c) {
        memory[a] = (b * c) % B16;
        // advancePC3);
    }

    // opcode 11:
    private void mod(int a, int b, int c) {
        memory[a] = (b % c);
        // advancePC3);
    }

    // opcode 12:
    private void and(int a, int b, int c) {
        memory[a] = (b & c);
        // advancePC3);
    }

    // opcode 13:
    private void or(int a, int b, int c) {
        memory[a] = (b | c);
        // advancePC3);
    }

    // opcode 14:
    private void not(int a, int b) {
        b = b & B15;
        memory[a] = b ^ B15;
        // advancePC2);
    }

    // opcode 15:
    private void rmem(int a, int b) {
        memory[a] = memory[b];
        // advancePC2);
    }

    // opcode 16:
    private void wmem(int a, int b) {
        rmem(a, b);
        // advancePC2);
    }

    // opcode 17: call
    private void call(int a) {
        // advancePC1);
        PC = a;
    }

    // opcode 18:
    private void ret() {
        // Pop from stack
        PC = popFromStack();
    }

    // opcode 19:
    private void out(int a) {
        System.out.print((char)a);
        // advancePC1);
    }

    // opcode 20:
    private void in(int a) {
        try {
            memory[a] = System.in.read();
            // advancePC1);
        } catch(IOException e) {
            System.err.println("%-IO-ERROR: " + e);
            System.exit(1010);
        }
    }

    // opcode 21: noop
    // Do nothing
    private void noop() {
        // advancePC0);
    }

    private void advancePC(int arguments) {
        PC += 1 + arguments;
    }

    private void coreDump() {
        System.out.println("%-CORE-DUMP");
        System.out.println("PC: " + PC + " SP: " + SP);
        for (int i = 0; i < 8; i++) {
            System.out.print("r" + i + ": " + memory[32768 + i] + "  ");
        }
        System.out.println("\n%-END-CORE");
    }

    private void pushToStack(int i) {
        stack[SP++] = i;
        if (SP == stack.length) {
            int[] tempStack = new int[stack.length * 2];
            System.arraycopy(stack, 0, tempStack, 0, stack.length);
            stack = tempStack;
        }
    }

    private int popFromStack() {
        if (SP == 0) {
            System.err.println("%-STACK-UNDERFLOW");
            System.exit(100);
        }

        int top = stack[--SP];

        // rebuild stack to optimize space
        if (SP <= stack.length / 4) {
            int[] tempStack = new int[stack.length / 4];
            System.arraycopy(stack, 0, tempStack, 0, SP);
            stack = tempStack;
        }

        return top;
    }

    public void debugMemory(int start, int length) {
        for (int i = start; i < length; i++) {
            System.out.print(memory[i] + " ");
            if ((i - start) % 16 == 15)
                System.out.println();
        }
    }
}
