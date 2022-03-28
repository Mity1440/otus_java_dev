package ru.otus.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import ru.otus.services.view.ClientView;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

public class Client{

    @Id
    private final Long id;

    @Nonnull
    private final String name;

    @MappedCollection(idColumn = "client_id")
    private Address address;

    @MappedCollection(idColumn = "client_id")
    private Set<Phone> phones;

    @PersistenceConstructor
    public Client(Long id, @Nonnull String name, Address address, Set<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Phone> getPhones() {
        return phones;
    }

    public void setPhones(Set<Phone> phones) {
        this.phones = phones;
    }

    public Long getId() {
        return id;
    }

    public String getPhonesRepresentation(){
        if (phones.isEmpty()){
            return "";
        }

        return phones
                .stream()
                .map(Phone::getNumber)
                .collect(Collectors.joining(", "))
                .toString();

    }

    public static Client getClientFromClientView(ClientView clientView){
        return new Client(null, clientView.getName(),null, null);
    }

}
