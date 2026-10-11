package org.example.amigoscode.functionalinterface;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class _Consumer {
    static void main() {
        // Normal java function
        Customer luis = new Customer("Luis", "99999");
        greetCustomer(luis);

        // Consumer Functional interface
        greetCustomerConsumer.accept(luis);

        greetCustomerBiConsumer.accept(luis, true);
        greetCustomerBiConsumer.accept(luis, false);
        greetCustomerConditional(luis, false);
    }

    static void greetCustomer(Customer customer) {
        System.out.printf("""
                Hello %s thanks for registering your phone number %s
                """, customer.customerName, customer.customerPhoneNumber);
    }

    static void greetCustomerConditional(Customer customer, Boolean showPhoneNumber) {
        System.out.printf("""
                Hello %s thanks for registering your phone number %s
                """, customer.customerName, (showPhoneNumber ? customer.customerPhoneNumber : "*******"));
    }

    static Consumer<Customer> greetCustomerConsumer = customer -> System.out.printf("""
            Hello %s thanks for registering your phone number %s
            """, customer.customerName, customer.customerPhoneNumber);

    static BiConsumer<Customer, Boolean> greetCustomerBiConsumer = (customer, showPhoneNumber) -> System.out.printf("""
            Hello %s thanks for registering your phone number %s
            """, customer.customerName, (showPhoneNumber ? customer.customerPhoneNumber : "*******"));

    static class Customer {
        private final String customerName;
        private final String customerPhoneNumber;

        Customer(String customerName, String customerPhoneNumber) {
            this.customerName = customerName;
            this.customerPhoneNumber = customerPhoneNumber;
        }
    }
}
