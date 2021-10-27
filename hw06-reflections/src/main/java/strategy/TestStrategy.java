package strategy;

public interface TestStrategy {

    public static TestStrategy getTestStrategy(String strategyName){

        if (strategyName == null)
            return new MainTestStartegyImpl();

        return null;

    }

    private static TestStrategy getDefaultStrategy(){
        return new MainTestStartegyImpl();
    }

    void test();

    void init(Class<?> clazz);

    void showStatistics();
}
