package ru.otus.services;

import org.springframework.stereotype.Service;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.view.ClientView;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final DBServiceClient dbServiceClient;

    public ClientServiceImpl(DBServiceClient clientRepository) {
        this.dbServiceClient = clientRepository;
    }

    @Override
    public List<ClientView> findAll() {
        var clientList =  dbServiceClient.findAll();

        return clientList
                .stream()
                .map(o->new ClientView(o))
                .collect(Collectors.toList());
    }

    @Override
    public Client save(ClientView client) {

        Client result = null;
        var savedClient = dbServiceClient.saveClient(Client.getClientFromClientView(client));

        String clientAddress = client.getAddress();

        Set<Phone> phones = null;
        if (client.getPhones() != null){
            String[] phonesInArray = client.getPhones().split(",");
            phones = Arrays
                    .stream(phonesInArray)
                    .map(o->new Phone(o, savedClient.getId()))
                    .collect(Collectors.toSet());
        }

        if (clientAddress ==null && phones == null){
            result = savedClient;
        } else{
            result = dbServiceClient.saveClient(new Client(savedClient.getId(),
                    savedClient.getName(),
                    new Address(clientAddress, savedClient.getId()),
                    phones));
        }

        return result;

    }
}
