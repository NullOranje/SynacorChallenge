import java.io.File;
import java.util.Scanner;

public class VM {
    // Useful constants
    private final int B16 = 0xFFFF;
    private final int B15 = 0x7FFF;

    // Program Counter and Stack Pointer
    private int PC;
    private int SP;

    // The memory + registers; the stack
    private int[] memory;
    private int[] stack;

    // RUN flag
    private boolean RUN;

    public VM() {
        // Memory runs from 0 to 32767, registers from 32768 to 32775
        memory = new int[32776];
        stack = new int[1];

        for (int i = 0; i < memory.length; i++) {
            memory[i] = 0;
        }

        // Set RUN flag to true;
        RUN = true;

        // Set initial registers
        SP = 0;
        PC = 0;
    }

    public boolean loadProgram(File theProgram) {
        boolean returnCode = true;

        try {
            Scanner sc = new Scanner(theProgram);

            // Do stuff here

            sc.close();
        } catch (java.io.FileNotFoundException fnfe) {
            System.err.println("%-FILE-NOT-FOUND");
            System.exit(404);
        }

        return returnCode;
    }

    private int readWord() {

        return 0;
    }

    // All the operands
    // opcode 0: halt
    private void halt() {
        RUN = false;
        advancePC(0);
        coreDump();
        System.exit(0);
    }

    // opcode 1: set a, b
    private void set(int a, int b) {

    }

    // opcode 2: push a
    private void push(int a) {

    }

    // opcode 3: pop a
    private void pop(int a) {
        if (SP == 0) {
            System.err.println("%-STACK-UNDERFLOW");
            System.exit(100);
        }
    }

    // opcode 4: eq
    private void eq(int a, int b, int c) {

    }

    // opcode 5: gt
    private void gt(int a, int b, int c) {

    }

    // opcode 6: jmp
    private void jmp(int a) {
        PC = memory[a];
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
    }

    // opcode 10: mult
    private void mult(int a, int b, int c) {
        memory[a] = (b * c) % B16;
        advancePC(3);
    }

    // opcode 11:
    private void mod(int a, int b, int c) {
        memory[a] = (b % c);
        advancePC(3);
    }

    // opcode 12:
    private void and(int a, int b, int c) {
        memory[a] = (b & c);
        advancePC(3);
    }

    // opcode 13:
    private void or(int a, int b, int c) {
        memory[a] = (b | c);
        advancePC(3);
    }

    // opcode 14:
    private void not(int a, int b) {
        b = b & B15;
        memory[a] = b ^ B15;
        advancePC(2);
    }

    // opcode 15:
    private void rmem(int a, int b) {
        memory[a] = memory[b];
        advancePC(2);
    }

    // opcode 16:
    private void wmem(int a, int b) {
        rmem(a, b);
    }

    // opcode 17: call
    private void call(int a) {
        advancePC(1);
        PC = a;
    }

    // opcode 18:
    private void ret() {
        // Pop from stack
        PC = popFromStack();
    }

    // opcode 19:
    private void out(int a) {
        System.out.print(memory[a]);
        advancePC(1);
    }

    // opcode 20:
    private void in(int a) {
        advancePC(1);
    }

    // opcode 21: noop
    // Do nothing
    private void noop() {
        advancePC(0);
    }

    private void advancePC(int arguments) {
        PC += 1 + arguments;
    }

    private void coreDump() {
        System.out.println("PC: " + PC + " SP: " + SP);
        for (int i = 0; i < 8; i++) {
            System.out.print("r" + i + ": " + memory[32768 + i]);
        }
        System.out.println();
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
}
