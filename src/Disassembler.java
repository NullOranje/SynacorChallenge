import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;

class Disassembler {
    private final String[] OPERANDS = {"halt", "set", "push", "pop", "eq", "gt", "jmp", "jt", "jf", "add", "mult", "mod", "and", "or", "not", "rmem", "wmem", "call", "ret", "out", "in", "noop"};
    private final int[] OP_LENGTH = {0, 2, 1, 1, 3, 3, 1, 2, 2, 3, 3, 3, 3, 3, 2, 2, 2, 1, 0, 1, 1, 0};
    private FileInputStream fis;

    Disassembler(String theBinary) {
        File theFile = new File(theBinary);
        try {
            fis = new FileInputStream(theFile);
        } catch (java.io.FileNotFoundException fnfe) {
            System.err.println("%-FILE-NOT-FOUND: " + fnfe);
            System.exit(404);
        }
    }

    void printOperation(int[] a) {
        System.out.print(OPERANDS[a[0]] + ": ");
        for (int i = 1; i < a.length; i++)
            System.out.print(a[i] + " ");
        System.out.println();
    }

    int[] getNextOpcode() {
        int code = readNext();
        int[] returnCodes = new int[OP_LENGTH[code] + 1];
        returnCodes[0] = code;

        for (int i = 1; i < returnCodes.length; i++)
            returnCodes[i] = readNext();

        return returnCodes;
    }

    int[] loadProgram(String theProgram) {
        LinkedList<Integer> llProgram = new LinkedList<>();
        File theFile = new File(theProgram);

        try {
            fis = new FileInputStream(theFile);
            int b;
            while ((b = readNext()) >= 0) {
                llProgram.add(b);
                // b = readNext();
            }
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("%-FILE-NOT-FOUND: " + fnfe);
            System.exit(404);
        }

        int[] theReturn = new int[32776];
        for (int i = 0; i < llProgram.size(); i++)
            theReturn[i] = llProgram.get(i);

        return theReturn;
    }

    private int readNext() {
        int theShort;

        try {
            int low = fis.read();
            int high = fis.read();
            high = high << 8;

            theShort = high + low;
        } catch (java.io.IOException e) {
            //System.err.println("IO Error: " + e);
            // System.exit(253);
            return -1;
        }
        return theShort;
    }


}
