package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class ProcessorSwapField11AndField12 implements Processor {

    @Override
    public Message process(Message message) {

        String tempFiled11 = message.getField11();
        String tempField12 = message.getField12();

        return message
                .toBuilder()
                .field11(tempField12)
                .field12(tempFiled11)
                .build();

    }

}
