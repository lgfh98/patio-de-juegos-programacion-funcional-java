package org.example.module03_advanced_manipulation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * MÓDULO 03 - FLATMAP: APLANAMIENTO DE ESTRUCTURAS ANIDADAS
 * -----------------------------------------------------------------------------
 * En APIs reales, rara vez trabajamos con listas planas simples de tipo List<String>.
 * Casi siempre recibimos estructuras jerárquicas:
 * - Un Cliente tiene N Órdenes, cada Orden tiene N Líneas de Pedido (Items).
 * - O recibimos textos donde cada línea tiene N palabras.
 *
 * MAP vs FLATMAP:
 * - map(f)     : T -> R            (Produce un elemento por cada elemento: Stream<T> -> Stream<R>)
 *   Si f retorna una colección/Stream, map genera un Stream<Stream<R>> (¡Estructura anidada indeseada!).
 * - flatMap(f) : T -> Stream<R>    (Mapea cada elemento a un stream y luego APLANA todos los streams
 *                                   internos en un único Stream<R> de una sola dimensión).
 */
public class FlatMapExplained {

    public record OrderItem(String productSku, int quantity, double unitPrice) {}
    public record CustomerOrder(String orderId, String customerEmail, List<OrderItem> items) {}

    /**
     * Caso de Uso 1 de API:
     * Extraer todos los SKU únicos de productos comprados a lo largo de todas las órdenes de clientes.
     */
    public static List<String> extractUniquePurchasedSkus(List<CustomerOrder> orders) {
        if (orders == null) return List.of();

        return orders.stream()
                // Por cada orden, abrimos un stream con sus items: Order -> Stream<OrderItem>
                .flatMap(order -> order.items().stream()) // Aplana de List<List<OrderItem>> a Stream<OrderItem>
                .map(OrderItem::productSku)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Caso de Uso 2: Procesamiento de texto algorítmico.
     * Dividir una lista de oraciones o logs en palabras individuales únicas.
     */
    public static List<String> extractUniqueWords(List<String> sentences) {
        if (sentences == null) return List.of();

        return sentences.stream()
                .map(sentence -> sentence.split("\\s+")) // Stream<String[]>
                .flatMap(Arrays::stream)                 // Stream<String> (aplana el array)
                .map(String::toLowerCase)
                .distinct()
                .toList();
    }

    /**
     * Caso de Uso 3: Aplanamiento Monádico con Optional.
     * En lugar de anidar if (opt.isPresent()) if (opt.get().getOther().isPresent())...
     * flatMap desenvuelve el Optional anidado de forma limpia y segura.
     */
    public record Address(String city, Optional<String> postalCode) {}
    public record UserProfile(String id, Optional<Address> address) {}

    public static Optional<String> getPostalCodeSafely(UserProfile profile) {
        return Optional.ofNullable(profile)
                .flatMap(UserProfile::address)      // Optional<Address>
                .flatMap(Address::postalCode);      // Optional<String> (evita Optional<Optional<String>>)
    }
}
