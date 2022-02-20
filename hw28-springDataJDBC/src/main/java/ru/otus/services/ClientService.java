package ru.otus.services;

import ru.otus.crm.model.Client;
import ru.otus.services.view.ClientView;

import java.util.List;

public interface ClientService {
    List<ClientView> findAll();
    Client save(ClientView client);
}
