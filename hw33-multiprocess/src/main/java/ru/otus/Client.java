package ru.otus;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ru.otus.generated.Digit;
import ru.otus.generated.RemoteDBServiceGrpc;
import ru.otus.generated.Sequence;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {

    //private final Logger log = LoggerFactory.getLogger(Client.class);

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8290;

    public static void main(String[] args) throws InterruptedException {

        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                                                         .usePlaintext()
                                                         .build();

        var counter = new AtomicInteger();
        var latch = new CountDownLatch(1);
        var newStub = RemoteDBServiceGrpc.newStub(channel);

        newStub
                .getSequence(Sequence.newBuilder().setBegin(1).setEnd(30).build(),
                            new StreamObserver<Digit>() {

                            @Override
                            public void onNext(Digit um) {
                                counter.set(um.getValue());
                            }

                            @Override
                            public void onError(Throwable t) {
                                System.err.println(t);
                            }

                            @Override
                            public void onCompleted() {
                                latch.countDown();
                            }

                });

        processCounter(counter);

        latch.await();
        channel.shutdown();

    }

    private static void processCounter(AtomicInteger counter) throws InterruptedException {

        var current = 0;
        var fixCounter = 0;

        for (var idx = 0; idx < 50; idx++) {

            var serverValue = counter.get();
            current += (fixCounter == serverValue? 0: serverValue) + 1;

            System.out.println(
                    String.format("Server value: %d, Current: %d", serverValue, current)
            );

            fixCounter = serverValue;

            Thread.sleep(1000);

        }
    }

}
