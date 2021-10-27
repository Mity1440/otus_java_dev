package runners;

import examples.FirstExample;

public interface TestRunner {

    static TestRunner getTestRunner(String runnerName) {
        return getDefaultTestRunner();
    }

    private static TestRunner getDefaultTestRunner() {
        return new TestRunnerImpl();
    }

    void testRun();

    void init(Class<?> clazz);

    void showStatistics();

}
