package ru.otus.service;

import io.grpc.stub.StreamObserver;
import ru.otus.generated.Digit;
import ru.otus.generated.RemoteDBServiceGrpc;
import ru.otus.generated.Sequence;

public class RemoteDBServiceImpl extends RemoteDBServiceGrpc.RemoteDBServiceImplBase {

    private final SequenceCreatable service;

    public RemoteDBServiceImpl(SequenceCreatable service) {
        this.service = service;
    }

    @Override
    public void getSequence(Sequence request, StreamObserver<Digit> responseObserver) {

        var digitSequence = service.generateSequence(request.getBegin(), request.getEnd());

        digitSequence.forEach(i -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            responseObserver.onNext(integer2Digit(i));
        });
        responseObserver.onCompleted();
    }

    private Digit integer2Digit(Integer integerDigit) {
        return Digit.newBuilder().setValue(integerDigit).build();
    }

}
