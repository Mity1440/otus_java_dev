package statistics;

import helpers.ConsoleHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestStatisticAggregator {

    Map<Class, TestResults> aggregation = new HashMap<>();

    public TestStatisticAggregator() {

    }

    public void addSuccesTest(Class<?> testClass, Method method) {

        TestResults result = getTestResultForClass(testClass);
        result.count++;
        result.addsuccessTest(method);

        aggregation.put(testClass, result);

   }

    public void addFaledTest(Class<?> testClass, Exception e, Method method) {

        TestResults result = getTestResultForClass(testClass);
        result.count++;
        result.addfaledTest(method, e.getMessage());

        aggregation.put(testClass, result);

    }

    private TestResults getTestResultForClass(Class<?> testClass){

        TestResults result = aggregation.get(testClass);
        if (result == null){
            result = new TestResults();
        }
        return result;

    }

    public void show() {

        ConsoleHelper.writeMainHeader(aggregation.keySet().stream().count());

        for (Map.Entry<Class, TestResults> pair: aggregation.entrySet()){

            ConsoleHelper.writeTestClassHeader(pair.getKey().getName());

            TestResults testResult = pair.getValue();

            ConsoleHelper.writeTotalClassInfo(testResult.count);
            ConsoleHelper.writeSuccesInfo(testResult.successTests.size());

            for (Method method: testResult.successTests){
                ConsoleHelper.writeMethodName(method.getName());

            }

            ConsoleHelper.write(String.format("Failed - %d", testResult.faledTests.size()));
            for (Map.Entry<Method, String> faledTestsPair: testResult.faledTests.entrySet()){

                Method faledMethod = faledTestsPair.getKey();

                ConsoleHelper.writeMethodName(faledMethod.getName());
                ConsoleHelper.writeStackTraceInfo(faledTestsPair.getValue());

            }


        }

    }

    private class TestResults{

        int count;
        Map<Method, String> faledTests = new HashMap<>();
        ArrayList<Method> successTests = new ArrayList<>();

        void addfaledTest(Method method, String message){
            faledTests.put(method, message);
        }

        void addsuccessTest(Method method){
            successTests.add(method);
        }

    }

}
