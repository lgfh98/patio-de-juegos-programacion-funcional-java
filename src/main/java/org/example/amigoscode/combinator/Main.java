package org.example.amigoscode.combinator;

import java.time.LocalDate;

import static org.example.amigoscode.combinator.CustomerRegistrationValidator.*;

public class Main {
    static void main() {
        Customer customer1 = new Customer(
                "Alice",
                "+0549846546",
                "alice@gmail.com",
                LocalDate.of(2000, 1, 1)
        );

        //System.out.println(new CustomerValidatorService().isValid(customer1));

        // if valid we can save customer in db

        // Using combinator pattern
        ValidationResult result = isEmailValid()
                .and(isPhoneNumberValid())
                .and(isAnAdult())
                .apply(customer1);

        System.out.println(result);

        try {
            if (result != ValidationResult.SUCCESS) {
                throw new IllegalStateException(result.name());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}
