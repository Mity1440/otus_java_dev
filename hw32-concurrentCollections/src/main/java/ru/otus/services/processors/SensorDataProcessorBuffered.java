package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lib.SensorDataBufferedWriter;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;

import java.util.ArrayList;
import java.util.Comparator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;


// Этот класс нужно реализовать
public class SensorDataProcessorBuffered implements SensorDataProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final BlockingQueue<SensorData> dataBuffer;
    private final ReentrantLock lock = new ReentrantLock();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;

        this.dataBuffer = new PriorityBlockingQueue<SensorData>(bufferSize,
                Comparator.comparing(SensorData::getMeasurementTime));
    }

    @Override
    public void process(SensorData data) {

        lock.lock();
        if (dataBuffer.size() >= bufferSize) {
                flush();
        }
        lock.unlock();

        dataBuffer.offer(data);
    }

    public void flush() {

        lock.lock();
        flushDataBuffer();
        lock.unlock();

    }

    private void flushDataBuffer() {

        if (dataBuffer.isEmpty()){
            return;
        }

        try {
            var bufferedData = new ArrayList<SensorData>();
            dataBuffer.drainTo(bufferedData);
            writer.writeBufferedData(bufferedData);
        } catch (Exception e) {
            LOG.error("Ошибка в процессе записи буфера", e);
        };

    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
