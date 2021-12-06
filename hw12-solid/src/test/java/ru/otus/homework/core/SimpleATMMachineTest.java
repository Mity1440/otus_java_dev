package ru.otus.homework.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.otus.homework.core.interfaces.ATMMachine;
import ru.otus.homework.core.interfaces.ATMMachineCell;
import ru.otus.homework.core.manipulators.PutManipulator;
import ru.otus.homework.core.manipulators.ServiceManipulator;
import ru.otus.homework.core.manipulators.WithdrawalManipulator;
import ru.otus.homework.core.units.CurrencyUnit;
import ru.otus.homework.core.units.NominalValue;
import ru.otus.homework.core.units.SimpleATMMachineCell;
import ru.otus.homework.exceptions.ATMBasicException;
import ru.otus.homework.exceptions.ATMMachineInsufficientFundsException;
import ru.otus.homework.exceptions.ATMPutManagerNotRecognizeUnit;
import ru.otus.homework.exceptions.ATMWithdrawalManipulatorDebitingMoneyException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class SimpleATMMachineTest {

    private WithdrawalManipulator withdrawalManipulator;
    private PutManipulator putManipulator;
    private ServiceManipulator serviceManipulator;

    @BeforeEach
    public void setUp() {

        withdrawalManipulator = Mockito.mock(WithdrawalManipulator.class);
        putManipulator = Mockito.mock(PutManipulator.class);
        serviceManipulator = Mockito.mock(ServiceManipulator.class);

    }

    @Test
    @DisplayName("Создать экземпляр банкоманта, проверить что не потреялись ячейки и деньги на них")
    void builder() {

        SimpleATMMachine.SimpleATMMachineBuilder builder = SimpleATMMachine.builder(withdrawalManipulator,
                                                                                    putManipulator,
                                                                                    serviceManipulator);

        builder.addCell(new SimpleATMMachineCell(NominalValue._50, 50, 20));
        builder.addCell(new SimpleATMMachineCell(NominalValue._100, 30, 17));
        builder.addCell(new SimpleATMMachineCell(NominalValue._200, 45, 5));
        builder.addCell(new SimpleATMMachineCell(NominalValue._50, 50, 0));

        ATMMachine simpleATMMachine = builder.build();

        Map<NominalValue, List<ATMMachineCell>> cell = null;
        try {

            Field field = simpleATMMachine.getClass().getDeclaredField("cells");
            field.setAccessible(true);
            cell = (Map<NominalValue, List<ATMMachineCell>>)field.get(simpleATMMachine);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        assertEquals(cell.entrySet().size(),
                3,
                "Ожидали, что в банкомате содержится три номинала, но это не так");

        assertEquals(cell.get(NominalValue._50).size(),
                2,
                "Ожидали, что будет две ячейки с номиналом пятьдесят но это не так");

    }

    @Test
    @DisplayName("Создать экземпляр банкоманта без ячеек и проверить что нельзя списать ничего в минус")
    void withdraw() {

        SimpleATMMachine.SimpleATMMachineBuilder builder = SimpleATMMachine.builder(withdrawalManipulator,
                                                                                    putManipulator,
                                                                                    serviceManipulator);

        ATMMachine machine = builder.build();

       Assertions.assertThrows(ATMMachineInsufficientFundsException.class, () -> machine.withdraw(100));


    }

    @Test
    @DisplayName("Создать экземпляр банкоманта и проверить что верно считается баланс в начальной расстановке")
    void balance() {

        SimpleATMMachine.SimpleATMMachineBuilder builder = SimpleATMMachine.builder(withdrawalManipulator,
                                                                                    putManipulator,
                                                                                    new ServiceManipulator());

        builder.addCell(new SimpleATMMachineCell(NominalValue._50, 50, 20));
        builder.addCell(new SimpleATMMachineCell(NominalValue._100, 30, 17));
        builder.addCell(new SimpleATMMachineCell(NominalValue._200, 45, 5));
        builder.addCell(new SimpleATMMachineCell(NominalValue._50, 50, 0));

        ATMMachine simpleATMMachine = builder.build();

        assertThat(simpleATMMachine.balance()).isEqualTo(3700);

    }

    @Test
    @DisplayName("Проверить внесение наличных насчет. Ожидаем, что 3500 + 400 = 3900")
    void simpleDeposit() {

        CurrencyUnit[] moneys = new CurrencyUnit[4];
        moneys[0] = new CurrencyUnit(NominalValue._50);
        moneys[1] = new CurrencyUnit(NominalValue._50);
        moneys[2] = new CurrencyUnit(NominalValue._100);
        moneys[3] = new CurrencyUnit(NominalValue._200);

        SimpleATMMachine.SimpleATMMachineBuilder builder = SimpleATMMachine.builder(withdrawalManipulator,
                                                                                    new PutManipulator(),
                                                                                    new ServiceManipulator());

        builder.addCell(new SimpleATMMachineCell(NominalValue._50, 50, 10));
        builder.addCell(new SimpleATMMachineCell(NominalValue._100, 50, 10));
        builder.addCell(new SimpleATMMachineCell(NominalValue._200, 50, 10));

        ATMMachine simpleATMMachine = builder.build();

        assertThat(simpleATMMachine.balance()).isEqualTo(3500);

        simpleATMMachine.deposit(moneys);

        assertThat(simpleATMMachine.balance()).isEqualTo(3900);

    }

    @Test
    @DisplayName("Проверить, что нельзя внести нераспознанные купюры (null в массиве)")
    void depositWithNotRecognizeUnits() {

        CurrencyUnit[] moneys = new CurrencyUnit[4];
        moneys[0] = new CurrencyUnit(NominalValue._50);

        SimpleATMMachine.SimpleATMMachineBuilder builder = SimpleATMMachine.builder(withdrawalManipulator,
                new PutManipulator(),
                new ServiceManipulator());

        builder.addCell(new SimpleATMMachineCell(NominalValue._50, 50, 10));
        ATMMachine simpleATMMachine = builder.build();

        assertThrows(ATMPutManagerNotRecognizeUnit.class, ()->{
            simpleATMMachine.deposit(moneys);
        });

    }

    @Test
    @DisplayName("Простая имитация работы: инициализация, внесение и снятие наличных. Всегда котроль баланса")
    void simpleWorkLifeCycle(){

        SimpleATMMachine.SimpleATMMachineBuilder builder = SimpleATMMachine.builder(new WithdrawalManipulator(),
                                                                                    new PutManipulator(),
                                                                                    new ServiceManipulator());

        builder.addCell(new SimpleATMMachineCell(NominalValue._50, 50, 10)); // 500
        builder.addCell(new SimpleATMMachineCell(NominalValue._200, 50, 10)); // 2000
        builder.addCell(new SimpleATMMachineCell(NominalValue._100, 50, 10)); // 1000
        builder.addCell(new SimpleATMMachineCell(NominalValue._100, 50, 10)); // 1000
        builder.addCell(new SimpleATMMachineCell(NominalValue._200, 50, 10)); // 2000

        ATMMachine simpleATMMachine = builder.build();


        assertEquals(6500,
                     simpleATMMachine.balance(),
                     "Ожидали, что баланс будет 6500 но это не так");


        CurrencyUnit[] moneys = new CurrencyUnit[5];
        moneys[0] = new CurrencyUnit(NominalValue._50);
        moneys[1] = new CurrencyUnit(NominalValue._100);
        moneys[2] = new CurrencyUnit(NominalValue._100);
        moneys[3] = new CurrencyUnit(NominalValue._200);
        moneys[4] = new CurrencyUnit(NominalValue._200);

        simpleATMMachine.deposit(moneys);

        assertEquals(7150,
                     simpleATMMachine.balance(),
                    "Ожидали, что баланс будет 6500 но это не так");


        List<CurrencyUnit> cash = simpleATMMachine.withdraw(3500);
        assertThat(cash).isNotNull().size().isEqualTo(18);

        assertThrows(ATMMachineInsufficientFundsException.class, ()->{
            simpleATMMachine.withdraw(20000);
        });

    }

    @Test
    @DisplayName("Проверить, что нельзя снять валюты больше, чем есть в банкомате")
    void checkWithdraw(){

        SimpleATMMachine.SimpleATMMachineBuilder builder = SimpleATMMachine.builder(new WithdrawalManipulator(),
                putManipulator,
                serviceManipulator);

        builder.addCell(new SimpleATMMachineCell(NominalValue._50, 50, 10)); // 500
        builder.addCell(new SimpleATMMachineCell(NominalValue._200, 50, 10)); // 2000
        builder.addCell(new SimpleATMMachineCell(NominalValue._100, 50, 10)); // 1000
        builder.addCell(new SimpleATMMachineCell(NominalValue._100, 50, 10)); // 1000
        builder.addCell(new SimpleATMMachineCell(NominalValue._200, 50, 10)); // 2000

        ATMMachine simpleATMMachine = builder.build();

        assertThrows(ATMMachineInsufficientFundsException.class, ()->{
            simpleATMMachine.withdraw(20000);
        });

    }
}