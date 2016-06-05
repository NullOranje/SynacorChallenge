import java.io.IOException;

class VM {
    // Useful constants
    // private final int B16 = 0xFFFF;     // 65535
    private final int B15 = 0x7FFF;     // 32767
    private final int B151 = 0x8000;
    // private final String[] OPERANDS = {"halt", "set", "push", "pop", "eq", "gt", "jmp", "jt", "jf", "add", "mult", "mod", "and", "or", "not", "rmem", "wmem", "call", "ret", "out", "in", "noop"};
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

    private long callCounter;

    VM() {
        // Memory runs from 0 to 32767, registers from 32768 to 32775
        memory = new int[32776];
        stack = new int[1];
        stack[0] = -1;

        for (int i = 0; i < memory.length; i++) {
            memory[i] = 0;
        }

        // Set RUN flag to true;
        RUN = true;

        // Set initial registers
        SP = 0;
        PC = 0;

        // DEBUGGING
        callCounter = 0;
    }

    void loadProgram(String theProgram) {
        DVM = new Disassembler(theProgram);
        memory = DVM.loadProgram(theProgram);
        PC = 0; // Per the spec, set the program counter to 0
    }

    void run() {
        while (RUN)
            executeCommand(getNextOpcode());
    }

    void step() {
        executeCommand(getNextOpcode());
    }

    private int[] getNextOpcode() {
        int code = memory[PC++];
        int[] returnCodes = new int[OP_LENGTH[code] + 1];
        returnCodes[0] = code;

        for (int i = 1; i < returnCodes.length; i++)
            if ((i == 1))
                returnCodes[i] = memory[PC++];
            else
                returnCodes[i] = indirect(memory[PC++]);

        // Debugging
        callCounter++;

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

    // All the operands
    // opcode 0: halt
    private void halt() {
        // RUN = false;
        coreDump();
        System.exit(0);
    }

    // opcode 1: set a, b
    private void set(int a, int b) {
        memory[a] = b;
    }

    // opcode 2: push a
    private void push(int a) {
        pushToStack(indirect(a));
    }

    // opcode 3: pop a
    private void pop(int a) {
        memory[a] = popFromStack();
    }

    // opcode 4: eq
    private void eq(int a, int b, int c) {
        if (b == c)
            memory[a] = 1;
        else
            memory[a] = 0;
    }

    // opcode 5: gt
    private void gt(int a, int b, int c) {
        if (b > c)
            memory[a] = 1;
        else
            memory[a] = 0;
    }

    // opcode 6: jmp
    private void jmp(int a) {
        PC = indirect(a);
    }

    // opcode 7: jt
    private void jt(int a, int b) {
        if (indirect(a) > 0)
            jmp(b);
    }

    // opcode 8: jf
    private void jf(int a, int b) {
        if (indirect(a) == 0)
            jmp(b);
    }

    // opcode 9: add
    private void add(int a, int b, int c) {
        memory[a] = (b + c) % B151;
    }

    // opcode 10: mult
    private void mult(int a, int b, int c) {
        memory[a] = (b * c) % B151;
    }

    // opcode 11:
    private void mod(int a, int b, int c) {
        memory[a] = (b % c);
    }

    // opcode 12:
    private void and(int a, int b, int c) {
        memory[a] = (b & c);
    }

    // opcode 13:
    private void or(int a, int b, int c) {
        memory[a] = (b | c);
    }

    // opcode 14:
    private void not(int a, int b) {
        b = b & B15;
        memory[a] = b ^ B15;
    }

    // opcode 15:
    private void rmem(int a, int b) {
        memory[a] = memory[b];
    }

    // opcode 16:
    private void wmem(int a, int b) {
        memory[memory[a]] = b;
    }

    // opcode 17: call
    private void call(int a) {
        pushToStack(PC);
        jmp(a);
    }

    // opcode 18:
    private void ret() {
        // Pop from stack
        jmp(popFromStack());
    }

    // opcode 19:
    private void out(int a) {
        System.out.print((char) a);
    }

    // opcode 20:
    private void in(int a) {
        try {
            memory[a] = System.in.read();
        } catch (IOException e) {
            System.err.println("%-IO-ERROR: " + e);
            System.exit(1010);
        }
    }

    // opcode 21: noop
    // Do nothing
    private void noop() {

    }

    private int indirect(int i) {
        if (i > B15)
            return memory[i];

        return i;
    }

    private void coreDump() {
        System.out.println("%-CORE-DUMP");
        System.out.println("PC: " + PC + " SP: " + SP);
        for (int i = 0; i < 8; i++) {
            System.out.print("r" + i + ": " + memory[B151 + i] + "  ");
        }
        System.out.println("\n%-END-CORE");
    }

    private void pushToStack(int i) {
        stack[SP++] = i;
        if (SP == stack.length) {
            int[] tempStack = new int[stack.length * 2];
            for (int s = 0; s < tempStack.length; s++)
                tempStack[s] = -1;
            System.arraycopy(stack, 0, tempStack, 0, stack.length);
            stack = tempStack;
        }
    }

    private int popFromStack() {
        if (SP == 0) {
            System.err.println("%-STACK-UNDERFLOW");
            System.err.println("PC : " + PC);
            System.exit(100);
        }

        int top = stack[--SP];
        stack[SP] = -1;

        // rebuild stack to optimize space
        if (SP > 0 && SP <= stack.length / 4) {
            int[] tempStack = new int[stack.length / 4];
            System.arraycopy(stack, 0, tempStack, 0, SP);
            stack = tempStack;
        }

        return top;
    }
}
