package ru.otus.processor.homework;

import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.Processor;
import ru.otus.processor.ProcessorUpperField10;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class ProcessorExceptionInEvenSecondTest {

    @Test
    void process() {

        Message.Builder builder = new Message.Builder(1l);
        var message = builder.field1("OK").build();

        var processor = new ProcessorExceptionInEvenSecond(new Processor() {
            @Override
            public Message process(Message message) {
                return message;
            }
        }, new Supplier<LocalTime>() {
            @Override
            public LocalTime get() {
                return LocalTime.of(10, 10, 12);
            }
        });

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> processor.process(message))
                .withMessage("Even second");


        var processor1 = new ProcessorExceptionInEvenSecond(new Processor() {
            @Override
            public Message process(Message message) {
                return message;
            }
        }, new Supplier<LocalTime>() {
            @Override
            public LocalTime get() {
                return LocalTime.of(10, 10, 13);
            }
        });

        processor1.process(message);

    }
}