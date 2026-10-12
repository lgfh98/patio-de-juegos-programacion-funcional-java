package org.example.amigoscode._05_combinator;

import java.time.LocalDate;
import java.time.Period;
import java.util.function.Function;

import static org.example.amigoscode._05_combinator.CustomerRegistrationValidator.*;
import static org.example.amigoscode._05_combinator.CustomerRegistrationValidator.ValidationResult.*;

/**
 * Guía Didáctica: Patrón Combinator en Java
 * -------------------------------------------------------------
 * MENTAL MODEL:
 * - El Patrón Combinator consiste en construir funciones complejas combinando
 *   funciones primitivas más simples mediante uno o más "combinadores" (métodos default).
 *
 * ¿POR QUÉ HEREDA DE Function<Customer, ValidationResult>?
 * - Al extender Function<Customer, ValidationResult>, cada validador es un objeto
 *   funcional de primera clase con firma: Customer -> ValidationResult.
 * - Su ejecución se dispara llamando a .apply(customer).
 *
 * ESTRUCTURA DEL PATRÓN:
 * 1. Validadores atómicos (Factory methods estáticos):
 *    - Cada método genera una regla independiente (isEmailValid, isPhoneNumberValid, isAnAdult).
 * 2. Combinador (.and()):
 *    - Método default que toma otra regla y retorna una regla compuesta.
 *    - Cortocircuito (Short-circuit): si la regla actual falla, detiene la cadena
 *      y propaga el error inmediatamente sin evaluar las siguientes.
 * 3. Dominio de resultados explícito (enum ValidationResult):
 *    - Reemplaza el boolean ciego con estados descriptivos de negocio (SUCCESS, EMAIL_NOT_VALID, etc.).
 */
public interface CustomerRegistrationValidator extends Function<Customer, ValidationResult> {

    /** Validador atómico: verifica formato básico de email */
    static CustomerRegistrationValidator isEmailValid() {
        return customer -> customer.getEmail().contains("@") ? SUCCESS : EMAIL_NOT_VALID;
    }

    /** Validador atómico: verifica prefijo de teléfono internacional */
    static CustomerRegistrationValidator isPhoneNumberValid() {
        return customer -> customer.getPhoneNumber().startsWith("+0") ? SUCCESS : PHONE_NUMBER_NOT_VALID;
    }

    /** Validador atómico: verifica edad mínima requerida */
    static CustomerRegistrationValidator isAnAdult() {
        return customer ->
                Period.between(customer.getDateOfBirth(), LocalDate.now()).getYears() > 16 ?
                        SUCCESS : IS_NOT_AN_ADULT;
    }

    /**
     * El Combinador funcional:
     * Combina la regla actual ('this') con otra regla ('other').
     * Si la regla actual es exitosa (SUCCESS), evalúa la siguiente;
     * de lo contrario, cortocircuita y retorna el fallo inmediatamente.
     */
    default CustomerRegistrationValidator and(CustomerRegistrationValidator other) {
        return customer -> {
            ValidationResult result = this.apply(customer);
            return result.equals(SUCCESS) ? other.apply(customer) : result;
        };
    }

    /**
     * Enum representativo del resultado de validación.
     * Permite mapear fácilmente a códigos de error HTTP o mensajes descriptivos en la UI.
     */
    enum ValidationResult {
        SUCCESS,
        PHONE_NUMBER_NOT_VALID,
        EMAIL_NOT_VALID,
        IS_NOT_AN_ADULT
    }
}
