package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;

public class SensorDataProcessorErrors implements SensorDataProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SensorDataProcessorErrors.class);

    @Override
    public void process(SensorData data) {
        if (data == null || data.getValue() == null || !data.getValue().isNaN()) {
            return;
        }
        LOG.error("Обработка ошибочных данных: {}", data);
    }
}
