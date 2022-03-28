package ru.otus.processor.homework;

import ru.otus.security.model.Message;
import ru.otus.processor.Processor;

import java.time.LocalTime;
import java.util.function.Supplier;

public class ProcessorExceptionInEvenSecond implements Processor, DateTimeProvider {

    private final Supplier<LocalTime> timeProvider;
    private final Processor processor;

    public ProcessorExceptionInEvenSecond(Processor processor, Supplier<LocalTime> timeProvider) {
        this.processor = processor;
        this.timeProvider = timeProvider;
    }

    @Override
    public Message process(Message message) {

        if (getDate().getSecond() % 2 == 0){
              throw new RuntimeException("Even second");
        }

        if (processor == null){
            return message;
        }

        return processor.process(message);

    }

    @Override
    public LocalTime getDate() {
        return this.timeProvider.get();
    }

}
