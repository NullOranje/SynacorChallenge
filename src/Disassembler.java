import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Disassembler {
    private final String[] OPERANDS = {"halt", "set", "push", "pop", "eq", "gt", "jmp", "jt", "jf", "add", "mult", "mod", "and", "or", "not", "rmem", "wmem", "call", "ret", "out", "in", "noop"};
    private final int[] OP_LENGTH = {0, 2, 1, 1, 3, 3, 1, 2, 2, 3, 3, 3, 3, 3, 2, 2, 2, 1, 0, 1, 1, 0};
    private FileInputStream fis;

    public void printOperation(short[] a) {
        System.out.print(OPERANDS[a[0]] + ": ");
        for (int i = 1; i < a.length; i++)
            System.out.print(a[i] + " ");
        System.out.println();
    }

    public Disassembler(String theBinary) {
        File theFile = new File(theBinary);
        try {
            fis = new FileInputStream(theFile);
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(252);
        }
    }

    public int readNext() {
        int theShort = -1;

        try {
            int low = fis.read();
            int high = fis.read() << 8;

            theShort = high + low;
        } catch (java.io.IOException e) {
            System.err.println(e);
            System.exit(253);
        }
        return theShort;
    }

}
