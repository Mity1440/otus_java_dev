package runners;

import exceptions.BeforeMethodPerformingException;
import helpers.ConsoleHelper;
import strategy.TestStrategy;

public class TestRunnerImpl implements TestRunner{

    TestStrategy strategy;

    public TestRunnerImpl() {
        strategy = TestStrategy.getTestStrategy(null);
    }

    @Override
    public void testRun() {
        strategy.test();
    }

    @Override
    public void init(Class<?> clazz) {
        strategy.init(clazz);
    }

    @Override
    public void showStatistics(){
        strategy.showStatistics();
    }

}
