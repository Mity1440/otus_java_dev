package ru.otus.crm.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;

@Table("phones")
public class Phone implements Cloneable{

    @Id
    private Long id;

    @Nonnull
    private String number;

    @Nonnull
    private final Long clientId;

    public Phone(@Nonnull String number, @Nonnull Long clientId){
        this(null, number, clientId);
    }

    @PersistenceConstructor
    public Phone(Long id, @Nonnull String number, @Nonnull Long clientId) {
        this.id = id;
        this.number = number;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nonnull
    public String getNumber() {
        return number;
    }

    public void setNumber(@Nonnull String number) {
        this.number = number;
    }

    @Nonnull
    public Long getClientId() {
        return clientId;
    }

}
