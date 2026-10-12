package org.example.amigoscode._05_combinator;

import java.time.LocalDate;

/**
 * Modelo de Dominio: Customer
 * -------------------------------------------------------------
 * Entidad inmutable que representa los datos de un cliente
 * sujetos a reglas de validación de negocio (email, teléfono, mayoría de edad).
 */
public class Customer {

    private final String name;
    private final String phoneNumber;
    private final String email;
    private final LocalDate dateOfBirth;

    public Customer(String name, String phoneNumber, String email, LocalDate dateOfBirth) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getName() {
        return name;
    }
}
