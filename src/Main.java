public class Main {
    private static final String challengeFile = "/Users/nicholas/Downloads/synacor-challenge/challenge.bin";

    public static void main(String[] args) {
        Disassembler VM = new Disassembler(challengeFile);

        for (int i = 0; i < 32; i++)
            System.out.print(VM.readNext() + " ");

        System.out.println();

    }
}
