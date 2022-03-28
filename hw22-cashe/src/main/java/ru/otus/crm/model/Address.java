package ru.otus.crm.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "address")
public class Address implements Cloneable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "street")
    private String street;

    @OneToOne(mappedBy = "address")
    private Client client;

    public Address() {
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj){
            return true;
        }

        if (Objects.isNull(obj)
                  || !obj.getClass().equals(getClass())){
            return false;
        }

        var objAddressLink = (Address) obj;

        return (Objects.equals(this.id, objAddressLink.id)
                    && Objects.equals(this.street, objAddressLink.street));

    }

    public Address(String street) {
        this.street = street;
    }

    public Address(Long id, String street) {
        this.id = id;
        this.street = street;
    }

    @Override
    public Address clone() {
        var adressClone =  new Address(id, street);
        return adressClone;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


}
