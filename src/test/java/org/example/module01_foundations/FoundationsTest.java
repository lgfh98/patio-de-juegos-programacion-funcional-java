package org.example.module01_foundations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FoundationsTest {

    @Test
    @DisplayName("Debe comparar el resultado idéntico entre enfoque imperativo y declarativo")
    void testImperativeVsDeclarative() {
        var users = List.of(
                new ImperativeVsDeclarative.User("Ana", 25, true),
                new ImperativeVsDeclarative.User("Carlos", 17, true),
                new ImperativeVsDeclarative.User("Beatriz", 30, false),
                new ImperativeVsDeclarative.User("David", 22, true)
        );

        List<String> imperativeResult = ImperativeVsDeclarative.getActiveAdultUserNamesImperative(users);
        List<String> declarativeResult = ImperativeVsDeclarative.getActiveAdultUserNamesDeclarative(users);

        assertThat(imperativeResult)
                .isEqualTo(declarativeResult)
                .containsExactly("ANA", "DAVID");
    }

    @Test
    @DisplayName("Debe garantizar la inmutabilidad ante intentos de mutación")
    void testImmutableCart() {
        var cart = new PureFunctionsAndImmutability.ImmutableCart("cust-123", List.of("Laptop", "Mouse"));

        assertThatThrownBy(() -> cart.items().add("Teclado"))
                .isInstanceOf(UnsupportedOperationException.class);

        var updatedCart = cart.withItem("Teclado");

        assertThat(cart.items()).hasSize(2);
        assertThat(updatedCart.items()).hasSize(3).containsExactly("Laptop", "Mouse", "Teclado");
    }

    @Test
    @DisplayName("Debe evaluar y componer interfaces funcionales estándar")
    void testFunctionalInterfacesCatalog() {
        // Test Predicate composition
        assertThat(FunctionalInterfacesCatalog.isCorporateEmail.test("dev@empresa.com")).isTrue();
        assertThat(FunctionalInterfacesCatalog.isCorporateEmail.test("dev@gmail.com")).isFalse();
        assertThat(FunctionalInterfacesCatalog.isNonCorporateEmail.test("dev@gmail.com")).isTrue();

        // Test Function composition (andThen)
        String formatted = FunctionalInterfacesCatalog.lengthFormatter.apply("Hola");
        assertThat(formatted).isEqualTo("Longitud calculada: 4");

        // Test UnaryOperator
        String sanitized = FunctionalInterfacesCatalog.sanitizeInput.apply("  STREAM MASTER  ");
        assertThat(sanitized).isEqualTo("stream master");

        // Test BinaryOperator
        Double discountedPrice = FunctionalInterfacesCatalog.calculateTotalWithDiscount.apply(100.0, 15.0);
        assertThat(discountedPrice).isEqualTo(85.0);
    }

    @Test
    @DisplayName("Debe ejecutar TriFunction y Validator personalizado con encadenamiento")
    void testCustomFunctionalInterfaces() {
        // TriFunction
        CustomFunctionalInterface.TriFunction<String, Integer, Double, String> invoiceGenerator =
                (client, qty, price) -> client + ": " + (qty * price);

        var invoiceWithTax = invoiceGenerator.andThen(str -> str + " (IVA incluido)");
        String invoice = invoiceWithTax.apply("TechCorp", 2, 50.0);

        assertThat(invoice).isEqualTo("TechCorp: 100.0 (IVA incluido)");

        // Validator composition
        CustomFunctionalInterface.Validator<String> lengthValidator = str ->
                str.length() >= 5 ? CustomFunctionalInterface.ValidationResult.ok()
                                  : CustomFunctionalInterface.ValidationResult.error("Longitud mínima es 5");

        CustomFunctionalInterface.Validator<String> compositeValidator =
                CustomFunctionalInterface.Validator.<String>notNull("Usuario")
                        .and(lengthValidator);

        assertThat(compositeValidator.validate(null).isValid()).isFalse();
        assertThat(compositeValidator.validate(null).message()).contains("Usuario no puede ser nulo");
        assertThat(compositeValidator.validate("abc").isValid()).isFalse();
        assertThat(compositeValidator.validate("abcdef").isValid()).isTrue();
    }
}
