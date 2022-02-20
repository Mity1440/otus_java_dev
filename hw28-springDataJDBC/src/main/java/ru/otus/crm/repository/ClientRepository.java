package ru.otus.crm.repository;

import org.springframework.data.repository.CrudRepository;
import ru.otus.crm.model.Client;

import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, Long> {

    Client findByName(String name);

}
