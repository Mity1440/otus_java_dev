package ru.otus.services.view;

import org.springframework.stereotype.Component;
import ru.otus.crm.model.Client;

public class ClientView {

    private String name;
    private String address;
    private String phones;

    public ClientView(String name, String address, String phones) {
        this.name = name;
        this.address = address;
        this.phones = phones;
    }

    public ClientView(Client client) {
        this.name = client.getName();
        if (client.getAddress() != null){
            this.address = client.getAddress().getStreet();
        }
        this.phones = client.getPhonesRepresentation();
    }

    public ClientView() {
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhones() {
        return phones;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

}