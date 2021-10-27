import examples.FirstExample;
import examples.NullPointerExceptionExample;
import examples.SecondExample;
import runners.TestRunner;

public class Main {

    public static void main(String[] args) {

        TestRunner testRunner = TestRunner.getTestRunner(null);

        testRunner.init(FirstExample.class);
        testRunner.testRun();

        testRunner.init(SecondExample.class);
        testRunner.testRun();

        testRunner.init(NullPointerExceptionExample.class);
        testRunner.testRun();

        testRunner.showStatistics();

    }

}
