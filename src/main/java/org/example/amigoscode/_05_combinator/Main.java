package org.example.amigoscode._05_combinator;

import java.time.LocalDate;

import static org.example.amigoscode._05_combinator.CustomerRegistrationValidator.*;

/**
 * Guía Didáctica: Demostración del Patrón Combinator
 * -------------------------------------------------------------
 * En esta clase se contrasta la ejecución entre:
 * 1. El servicio imperativo tradicional (CustomerValidatorService)
 * 2. El pipeline declarativo con el Patrón Combinator (CustomerRegistrationValidator)
 */
public class Main {
    static void main() {
        Customer customer1 = new Customer(
                "Alice",
                "+0549846546",
                "alice@gmail.com",
                LocalDate.of(2000, 1, 1)
        );

        // =========================================================================
        // 1. ENFOQUE TRADICIONAL (Servicio Imperativo)
        // =========================================================================
        System.out.println("=== 1. Enfoque Tradicional ===");
        boolean isValidTraditional = new CustomerValidatorService().isValid(customer1);
        System.out.println("¿Cliente válido según servicio?: " + isValidTraditional);
        // Si fuera false, no tendríamos información del campo infractor.

        // =========================================================================
        // 2. ENFOQUE FUNCIONAL: PATRÓN COMBINATOR
        // =========================================================================
        System.out.println("\n=== 2. Enfoque con Patrón Combinator ===");
        // Construimos una canalización de reglas encadenadas con .and()
        // y la disparamos perezosamente pasando el cliente en .apply(customer1).
        ValidationResult result = isEmailValid()
                .and(isPhoneNumberValid())
                .and(isAnAdult())
                .apply(customer1);

        System.out.println("Resultado de validación: " + result);

        // =========================================================================
        // 3. MANEJO DE RESULTADOS DE NEGOCIO Y EXCEPCIONES CONTROLADAS
        // =========================================================================
        try {
            if (result != ValidationResult.SUCCESS) {
                // El enum proporciona el motivo exacto del fallo para auditoría o respuesta API
                throw new IllegalStateException("Error de validación: " + result.name());
            }
            System.out.println("-> Cliente validado con éxito. Guardando en base de datos...");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
