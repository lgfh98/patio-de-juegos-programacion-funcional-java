package org.example.amigoscode._02_functionalinterface;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Guía Didáctica: Consumer y BiConsumer en Java
 * -------------------------------------------------------------
 * MENTAL MODEL:
 * - Consumer<T>: Acepta un único valor de tipo T y NO retorna nada (Firma: T -> void).
 * - BiConsumer<T, U>: Acepta dos valores (T, U) y NO retorna nada (Firma: (T, U) -> void).
 *
 * PROPÓSITO:
 * - Se emplean para ejecutar EFECTOS SECUNDARIOS (Side Effects) controlados:
 *   escritura en I/O (System.out), logging, inserciones en base de datos,
 *   mutaciones de estado externo o emisión de eventos.
 * - Método de ejecución: .accept(valor) o .accept(arg1, arg2).
 * - Composición: Permite encadenar acciones secuenciales con .andThen(Consumer).
 */
public class _Consumer {
    static void main() {
        Customer luis = new Customer("Luis", "99999");

        // =========================================================================
        // 1. MÉTODO TRADICIONAL VS. CONSUMER<T> (Aridad 1)
        // =========================================================================

        // Enfoque tradicional:
        System.out.println("=== 1. Método tradicional ===");
        greetCustomer(luis);

        // Enfoque funcional: Ejecución mediante .accept()
        System.out.println("\n=== 2. Consumer<T> ===");
        greetCustomerConsumer.accept(luis);

        // =========================================================================
        // 2. BICONSUMER<T, U> (Aridad 2: dos parámetros de entrada -> void)
        // =========================================================================
        System.out.println("\n=== 3. BiConsumer<T, U> con bandera condicional ===");
        greetCustomerBiConsumer.accept(luis, true);   // Muestra teléfono
        greetCustomerBiConsumer.accept(luis, false);  // Enmascara teléfono

        System.out.println("\n=== Método tradicional condicional ===");
        greetCustomerConditional(luis, false);

        // =========================================================================
        // 3. COMPOSICIÓN DE CONSUMERS CON .andThen()
        // =========================================================================
        // Permite encadenar múltiples efectos secundarios en orden estricto:
        // Primero saluda y luego registra en log.
        Consumer<Customer> logCustomerAccess = customer ->
                System.out.println("[AUDIT LOG] Acceso registrado para: " + customer.customerName);

        System.out.println("\n=== 4. Composición con .andThen() ===");
        greetCustomerConsumer.andThen(logCustomerAccess).accept(luis);
    }

    // -----------------------------------------------------------------------------
    // DEFINICIONES DE MÉTODOS Y CONSUMIDORES
    // -----------------------------------------------------------------------------

    /** Método tradicional con efecto secundario directo en consola */
    static void greetCustomer(Customer customer) {
        System.out.printf("""
                Hello %s thanks for registering your phone number %s
                """, customer.customerName, customer.customerPhoneNumber);
    }

    /** Método tradicional con 2 parámetros */
    static void greetCustomerConditional(Customer customer, Boolean showPhoneNumber) {
        System.out.printf("""
                Hello %s thanks for registering your phone number %s
                """, customer.customerName, (showPhoneNumber ? customer.customerPhoneNumber : "*******"));
    }

    /**
     * Consumer<Customer>: Función de primera clase que consume un Customer y produce un saludo.
     * Firma: Customer -> void
     */
    static Consumer<Customer> greetCustomerConsumer = customer -> System.out.printf("""
            Hello %s thanks for registering your phone number %s
            """, customer.customerName, customer.customerPhoneNumber);

    /**
     * BiConsumer<Customer, Boolean>: Consume dos argumentos para aplicar lógica de enmascaramiento.
     * Firma: (Customer, Boolean) -> void
     */
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
