package ru.otus.core.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.base.AbstractHibernateTest;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class DataTemplateHibernateTest extends AbstractHibernateTest {

    private final int COUNT_OF_INSERT_ON_CREATING_CLIENT_WITH_ONE_ADDRESS_AND_TWO_PHONES = 4;
    private final int COUNT_OF_UPDATE_ON_CREATING_CLIENT_WITH_ONE_ADDRESS_AND_TWO_PHONES = 0;

    @Test
    @DisplayName(" корректно сохраняет, изменяет и загружает клиента по заданному id")
    void shouldSaveAndFindCorrectClientById() {

        sessionFactory.getStatistics().setStatisticsEnabled(true);

        //given
        var client = new Client("Вася");
        var someAddress = new Address("The first street");
        someAddress.setClient(client);

        client.setAddress(someAddress);

        List<Phone> theFirstClientPhones = List.of(new Phone("1234455", client)
                                                   , new Phone("23421343", client));
        client.setPhones(theFirstClientPhones);

        //when
        var savedClient = transactionManager.doInTransaction(session -> {
            clientTemplate.insert(session, client);
            return client;
        });

        assertThat(sessionFactory
                .getStatistics()
                .getEntityUpdateCount())
                .isEqualTo(COUNT_OF_UPDATE_ON_CREATING_CLIENT_WITH_ONE_ADDRESS_AND_TWO_PHONES);

        assertThat(sessionFactory
                .getStatistics()
                .getEntityInsertCount())
                .isEqualTo(COUNT_OF_INSERT_ON_CREATING_CLIENT_WITH_ONE_ADDRESS_AND_TWO_PHONES);

        //then
        assertThat(savedClient.getId()).isNotNull();
        assertThat(savedClient.getName()).isEqualTo(client.getName());
        assertThat(savedClient).usingRecursiveComparison().isEqualTo(client);

        //when
        var loadedSavedClient = transactionManager.doInReadOnlyTransaction(session ->
                clientTemplate.findById(session, savedClient.getId())
        );

        //then
        assertThat(loadedSavedClient).isPresent().get().usingRecursiveComparison().isEqualTo(savedClient);

        //when
        var updatedClient = savedClient.clone();
        updatedClient.setName("updatedName");
        transactionManager.doInTransaction(session -> {
            clientTemplate.update(session, updatedClient);
            return null;
        });

        //then
        var loadedClient = transactionManager.doInReadOnlyTransaction(session ->
                clientTemplate.findById(session, updatedClient.getId())
        );

        assertThat(loadedClient)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(updatedClient);

        //when
        var clientList = transactionManager.doInReadOnlyTransaction(session ->
                clientTemplate.findAll(session)
        );

        //then
        assertThat(clientList.size()).isEqualTo(1);
        assertThat(clientList.get(0))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("address.client")
                .isEqualTo(updatedClient);


        //when
        clientList = transactionManager.doInReadOnlyTransaction(session ->
                clientTemplate.findByEntityField(session, "name", "updatedName")
        );

        //then
        assertThat(clientList.size()).isEqualTo(1);
        assertThat(clientList.get(0))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("address.client")
                .isEqualTo(updatedClient);
    }
}
