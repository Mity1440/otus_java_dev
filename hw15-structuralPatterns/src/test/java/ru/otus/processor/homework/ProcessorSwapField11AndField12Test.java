package ru.otus.processor.homework;

import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.Processor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProcessorSwapField11AndField12Test {

    @Test
    void process() {

        Message.Builder builder = new Message.Builder(1l);
        Message message = builder.field11("11").field12("12").build();

        Processor processor = new ProcessorSwapField11AndField12();
        Message message1 = processor.process(message);

        assertThat(message.getField11()).isEqualTo("11");
        assertThat(message.getField12()).isEqualTo("12");

        assertThat(message1.getField12()).isEqualTo("11");
        assertThat(message1.getField11()).isEqualTo("12");

    }
}