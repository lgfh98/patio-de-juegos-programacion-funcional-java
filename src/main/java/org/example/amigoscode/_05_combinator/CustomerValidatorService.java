package org.example.amigoscode._05_combinator;

import java.time.LocalDate;
import java.time.Period;

/**
 * Enfoque Tradicional: Validación basada en Servicio Imperativo
 * -------------------------------------------------------------
 * MENTAL MODEL:
 * - Una clase de servicio con métodos privados y un método agregador isValid(customer).
 *
 * LIMITACIONES Y PROBLEMAS DE ESTE ENFOQUE:
 * 1. Resultado opaco (boolean):
 *    - Si retorna false, el consumidor de la API NO sabe CUÁL regla falló
 *      (¿falló el email, el teléfono o la edad?).
 * 2. Acoplamiento rígido y falta de reutilización:
 *    - Si un flujo nuevo requiere validar únicamente el email y la edad, estamos forzados
 *      a crear otro método específico en el servicio (ej. isValidForPromotion).
 * 3. Viola el Principio Abierto/Cerrado (OCP):
 *    - Cada nueva regla exige modificar esta clase directamente en lugar de componer
 *      funciones independientes.
 *
 * Solución funcional: El Patrón Combinator (ver CustomerRegistrationValidator).
 */
public class CustomerValidatorService {

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.startsWith("+0");
    }

    private boolean isAdult(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears() > 18;
    }

    /**
     * Valida todas las condiciones obligatorias de un cliente.
     * Desventaja: Solo informa éxito o fracaso binario (true/false).
     */
    public boolean isValid(Customer customer) {
        return isEmailValid(customer.getEmail()) &&
                isPhoneNumberValid(customer.getPhoneNumber()) &&
                isAdult(customer.getDateOfBirth());
    }
}
