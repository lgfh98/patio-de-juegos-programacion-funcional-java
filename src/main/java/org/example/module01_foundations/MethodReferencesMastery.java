package org.example.module01_foundations;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * MÓDULO 01 - METHOD REFERENCES (::) EN DETALLE
 * ----------------------------------------------------------------------
 * Las referencias a métodos son azúcares sintácticos para lambdas que simplemente
 * delegan su invocación a un método ya existente.
 *
 * Hay exactamente 4 tipos en Java:
 *
 * | Tipo | Sintaxis | Ejemplo Lambda Equivalente |
 * |---|---|---|
 * | 1. Método Estático | ClassName::staticMethod | (x) -> Math.abs(x) -> Math::abs |
 * | 2. Método de Instancia Ligada (Bound) | instance::instanceMethod | (x) -> System.out.println(x) -> System.out::println |
 * | 3. Método de Instancia No Ligada (Unbound) | ClassName::instanceMethod | (str) -> str.toUpperCase() -> String::toUpperCase |
 * | 4. Constructor | ClassName::new | () -> new ArrayList<>() -> ArrayList::new |
 *
 * NOTA DE ENTREVISTA:
 * En el caso 3 (Unbound), el primer parámetro de la función receptora se convierte en el "this" (el objetivo)
 * sobre el cual se invoca el método. Por ejemplo:
 * BiPredicate<String, String> equalsIgnoreCase = String::equalsIgnoreCase;
 * Equivale a: (str1, str2) -> str1.equalsIgnoreCase(str2);
 */
public class MethodReferencesMastery {

    public record Product(String id, String name, double price) {}

    // 1. Método Estático
    public static boolean isExpensive(Product p) {
        return p.price() > 100.0;
    }

    // 2. Método de Instancia Ligada (Bound to a specific instance)
    public static class PriceFormatter {
        private final String currencySymbol;

        public PriceFormatter(String currencySymbol) {
            this.currencySymbol = currencySymbol;
        }

        public String format(double amount) {
            return String.format("%s%.2f", currencySymbol, amount);
        }
    }

    // Demostraciones de equivalencia funcional
    public static void showcase() {
        // Tipo 1: Estático
        // Lambda: (p) -> MethodReferencesMastery.isExpensive(p)
        Function<Product, Boolean> checkExpensive = MethodReferencesMastery::isExpensive;

        // Tipo 2: Instancia Ligada (Bound)
        PriceFormatter eurFormatter = new PriceFormatter("€");
        // Lambda: (amount) -> eurFormatter.format(amount)
        Function<Double, String> formatEuro = eurFormatter::format;

        // Tipo 3: Instancia No Ligada (Unbound)
        // Lambda: (p) -> p.name()
        Function<Product, String> getName = Product::name;
        // Lambda con 2 argumentos: (s1, s2) -> s1.compareToIgnoreCase(s2)
        BiPredicate<String, String> equalsIgnoreCase = String::equalsIgnoreCase;

        // Tipo 4: Constructor
        // Lambda: (id, name, price) -> new Product(id, name, price)
        Supplier<List<String>> listFactory = java.util.ArrayList::new;
    }
}
