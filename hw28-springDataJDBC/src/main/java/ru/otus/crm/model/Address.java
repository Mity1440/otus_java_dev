package ru.otus.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;

@Table("addresses")
public class Address implements Cloneable{

    @Id
    private Long id;

    private String street;

    @Column("client_id")
    private final Long clientId;

    public Address(@Nonnull String street, @Nonnull Long clientId) {
        this(null, street, clientId);
    }

    @PersistenceConstructor
    public Address(Long id, @Nonnull String street, @Nonnull Long clientId) {
        this.id = id;
        this.street = street;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Nonnull
    public Long getClientId() {
        return clientId;
    }

}
