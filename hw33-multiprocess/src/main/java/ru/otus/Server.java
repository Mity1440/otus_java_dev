package ru.otus;

import io.grpc.ServerBuilder;
import ru.otus.service.RemoteDBServiceImpl;
import ru.otus.service.SequenceCreatableImpl;

import java.io.IOException;

public class Server {

    public static final int SERVER_PORT = 8290;

    public static void main(String[] args) throws IOException, InterruptedException {

        var service = new SequenceCreatableImpl();
        var remoteDBService = new RemoteDBServiceImpl(service);

        var server = ServerBuilder
                .forPort(SERVER_PORT)
                .addService(remoteDBService)
                .build();

        server.start();
        System.out.println("server waiting for client connections...");
        server.awaitTermination();

    }

}
