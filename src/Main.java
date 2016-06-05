public class Main {
    private static final String challengeFile = "/Users/nicholas/Downloads/synacor-challenge/challenge.bin";

    public static void main(String[] args) {
        VM scvm = new VM();
        scvm.loadProgram(challengeFile);


        scvm.run();
    }
}
