package ru.otus.services.processors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SensorDataProcessorBufferedTest {

    public static final int BUFFER_SIZE = 2000;
    public static final String ANY_ROOM = "AnyRoom";

    @Mock
    private SensorDataBufferedWriter writer;

    @Captor
    private ArgumentCaptor<List<SensorData>> captor;

    private SensorDataProcessorBuffered processor;

    @BeforeEach
    void setUp() {
        processor = spy(new SensorDataProcessorBuffered(BUFFER_SIZE, writer));
    }

    @Test
    void shouldExecFlushWhenBufferOverFlow() {
        List<SensorData> sensorDataList = getSensorDataForTest(BUFFER_SIZE + BUFFER_SIZE / 2);

        sensorDataList.forEach(sensorData -> processor.process(sensorData));
        var outOfFirstBufferData = new SensorData(ANY_ROOM, 10500d);
        processor.process(outOfFirstBufferData);

        verify(processor, times(1)).flush();
        verify(writer).writeBufferedData(captor.capture());
        var flushedData = captor.getValue();

        assertThat(flushedData).hasSize(BUFFER_SIZE);
    }

    @Test
    void shouldFlushBufferDataSortedByTime() {
        List<SensorData> sensorDataList = getSensorDataForTest(BUFFER_SIZE - 1);
        var originalSensorDataList = List.copyOf(sensorDataList);
        Collections.shuffle(sensorDataList);

        sensorDataList.forEach(sensorData -> processor.process(sensorData));
        verify(processor, never()).flush();

        processor.flush();

        verify(writer).writeBufferedData(captor.capture());
        var flushedData = captor.getValue();

        assertThat(flushedData).containsExactlyElementsOf(originalSensorDataList);
    }

    @Test
    void shouldFlushTheRestOfTheBufferDataWhenOnProcessingEndFired() {
        List<SensorData> sensorDataList = getSensorDataForTest(BUFFER_SIZE + BUFFER_SIZE / 2);
        sensorDataList.forEach(sensorData -> processor.process(sensorData));

        reset(processor, writer);

        processor.onProcessingEnd();

        verify(processor, times(1)).flush();
        verify(writer).writeBufferedData(captor.capture());
        var flushedData = captor.getValue();

        assertThat(flushedData).hasSize(BUFFER_SIZE / 2);
    }

    @RepeatedTest(100)
//    @Test
    void shouldCorrectFlushDataFromManyThreads() {
        List<SensorData> sensorDataList = getSensorDataForTest(BUFFER_SIZE - 1);
        sensorDataList.forEach(sensorData -> processor.process(sensorData));

        reset(processor, writer);

        var numberOfThreads = 10;
        var threads = new ArrayList<Thread>();
        var latch = new CountDownLatch(1);
        for (int i = 0; i < numberOfThreads; i++) {
            var thread = new Thread(() -> {
                awaitLatch(latch);
                processor.flush();
            });
            thread.start();
            threads.add(thread);
        }
        latch.countDown();
        threads.forEach(this::joinThread);

        verify(processor, times(numberOfThreads)).flush();
        verify(writer, atLeastOnce()).writeBufferedData(captor.capture());
        var flushedData = captor.getAllValues();

        assertThat(flushedData).hasSize(1);
    }

    private List<SensorData> getSensorDataForTest(int limit) {
        var startTime = LocalDateTime.now();
        return DoubleStream.iterate(0.0, d -> d + 1)
                .limit(limit)
                .boxed()
                .map(d -> new SensorData(startTime.plusSeconds(d.longValue()), ANY_ROOM, d))
                .collect(Collectors.toList());
    }

    private void awaitLatch(CountDownLatch latch) {
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    private void joinThread(Thread thread) {
        try {
            thread.join(1);
        } catch (InterruptedException ignored) {
        }
    }

}