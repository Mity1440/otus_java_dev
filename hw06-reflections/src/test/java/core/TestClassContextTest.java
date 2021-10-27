package core;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TestClassContextTest {

    @Test
    void initialize() {

        TestClassContext context = new TestClassContext();

        context.initialize(TestClass.class);

        assertThat(context.getTestMethods(MethodCallOrder.TEST).size()).isEqualTo(1);
        assertThat(context.getTestMethods(MethodCallOrder.BEFORE).size()).isEqualTo(1);
        assertThat(context.getTestMethods(MethodCallOrder.AFTER).size()).isEqualTo(1);

        assertThat(context.getTestClass()).isEqualTo(TestClass.class);

        context.initialize(TestClassTwo.class);

        assertThat(context.getTestMethods(MethodCallOrder.TEST).size()).isEqualTo(2);
        assertThat(context.getTestMethods(MethodCallOrder.BEFORE).size()).isEqualTo(3);
        assertThat(context.getTestMethods(MethodCallOrder.AFTER).size()).isEqualTo(2);

        assertThat(context.getTestClass()).isEqualTo(TestClassTwo.class);

    }

    @Test
    void getDefaultObjectInstance() {

        TestClassContext context = new TestClassContext();
        context.initialize(TestClass.class);

    }

    @Test
    void getTestMethods() {

        TestClassContext context = new TestClassContext();
        context.initialize(TestClass.class);

        assertThat(context.getTestMethods(MethodCallOrder.TEST).size()).isEqualTo(1);
        assertThat(context.getTestMethods(MethodCallOrder.BEFORE).size()).isEqualTo(1);
        assertThat(context.getTestMethods(MethodCallOrder.AFTER).size()).isEqualTo(1);

        Method testMethod = null;
        try {
            testMethod = TestClass.class.getMethod("testMethod");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException();
        }

        assertThat(context.getTestMethods(MethodCallOrder.TEST))
                .isInstanceOf(ArrayList.class)
                .asList()
                .isNotEmpty()
                .hasSize(1)
                .contains(testMethod);

        Method beforeMethod = null;
        try {
            beforeMethod = TestClass.class.getMethod("beforeTestMethod");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException();
        }

        assertThat(context.getTestMethods(MethodCallOrder.BEFORE))
                .isInstanceOf(ArrayList.class)
                .asList()
                .isNotEmpty()
                .hasSize(1)
                .contains(beforeMethod);

        Method afterMethod = null;
        try {
            afterMethod = TestClass.class.getMethod("afterTestMethod");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException();
        }

        assertThat(context.getTestMethods(MethodCallOrder.AFTER))
                .isInstanceOf(ArrayList.class)
                .asList()
                .isNotEmpty()
                .hasSize(1)
                .contains(afterMethod);

    }

    @Test
    void getTestingClassName() {

        TestClassContext context = new TestClassContext();
        context.initialize(TestClass.class);

        assertThat(context.getTestingClassName()).isEqualTo(TestClass.class.getName());

    }

    @Test
    void getTestClass() {

        TestClassContext context = new TestClassContext();
        context.initialize(TestClass.class);

        assertThat(context.getTestClass()).isEqualTo(TestClass.class);

    }
}