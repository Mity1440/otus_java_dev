package strategy;

import core.MethodCallOrder;
import core.TestClassContext;
import exceptions.AfterMethodPerformingException;
import exceptions.BeforeMethodPerformingException;
import exceptions.MethodPerformingException;
import exceptions.TestMethodPerformingException;
import statistics.TestStatisticAggregator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainTestStartegyImpl implements TestStrategy {

    private TestClassContext context;
    private TestStatisticAggregator statisticAggregator;

    public MainTestStartegyImpl() {
        this.context = new TestClassContext();
        this.statisticAggregator = new TestStatisticAggregator();
    }

    public void init(Class<?> clazz) {
        initializeContext(clazz);
    }

    @Override
    public void showStatistics() {
        this.statisticAggregator.show();
    }

    private void initializeContext(Class<?> clazz) {
        this.context.initialize(clazz);
    }

    @Override
    public void test() {

        ArrayList<Method> testMethods = this.context.getTestMethods(MethodCallOrder.TEST);
        if (testMethods == null) {
            return;
        }

        for (Method method : testMethods) {
            try {
                perfomTestIteration(method);
                statisticAggregator.addSuccesTest(this.context.getTestClass(), method);
            } catch (Exception e) {
                statisticAggregator.addFaledTest(this.context.getTestClass(), e, method);
            }
        }

    }

    private String getStackTraceAsAString(Exception e) {

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        return sw.toString();

    }

    private void perfomTestIteration(Method testMethod) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, MethodPerformingException {

        Object instance = this.context.getDefaultObjectInstance();

        ArrayList<Method> beforeMethods = this.context.getTestMethods(MethodCallOrder.BEFORE);
        ArrayList<Method> afterMethods = this.context.getTestMethods(MethodCallOrder.AFTER);

        if (beforeMethods != null) {
            for (Method beforeMethod : beforeMethods) {
                try {
                    beforeMethod.invoke(instance);
                } catch (Exception e) {
                    throw new BeforeMethodPerformingException(getStackTraceAsAString(e),
                            beforeMethod.getName());

                }
            }
        }

        try {
            testMethod.invoke(instance);
        } catch (Exception e) {
            throw new TestMethodPerformingException(getStackTraceAsAString(e), testMethod.getName());
        }

        if (afterMethods != null) {
            for (Method afterMethod : afterMethods) {
                try {
                    afterMethod.invoke(instance);
                } catch (Exception e) {
                    throw new AfterMethodPerformingException(getStackTraceAsAString(e),
                            afterMethod.getName());
                }
            }
        }

    }
}
