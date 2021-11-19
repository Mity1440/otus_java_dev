package ru.otus.homework.proxy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.otus.homework.annotations.Log;
import ru.otus.homework.core.Calculatable;
import ru.otus.homework.core.CalculatableImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;


class IocTest {

    private static final String EOL = System.lineSeparator();

    private static final String TEXT_TO_PRINT1 = "executed method: calculate params: null"
                                                 + EOL
                                                 + "calculate()"
                                                 + EOL;

   private static final String TEXT_TO_PRINT2 = "executed method: calculate params: [1, 3]"
                                                + EOL
                                                + "calculate(int a, int b): a + b = 4"
                                                + EOL;
    private PrintStream backup;
    private ByteArrayOutputStream bos;

    @BeforeEach
    void setUp() {
        System.out.println(Thread.currentThread().getName());
        backup = System.out;
        bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
    }

    @AfterEach
    void tearDown() {
        System.setOut(backup);
    }

    @DisplayName("должно логировать вызов метода без параметров (null)")
    @Test
    void shouldPrintMethodWithoutParams(){

        var CalculatableImpl = new CalculatableImpl();
        var CalculatableImplProxy = (Calculatable)Ioc.getDecaratedClass(CalculatableImpl, Log.class);
        CalculatableImplProxy.calculate();
        assertThat(bos.toString()).isEqualTo(TEXT_TO_PRINT1);

    }

    @DisplayName("должно логировать вызов метода с двумя произвольными параметрами")
    @Test
    void shouldPrintMethodWithParams(){

        var CalculatableImpl = new CalculatableImpl();
        var CalculatableImplProxy = (Calculatable)Ioc.getDecaratedClass(CalculatableImpl, Log.class);
        CalculatableImplProxy.calculate(1, 3);
        assertThat(bos.toString()).isEqualTo(TEXT_TO_PRINT2);

    }

}