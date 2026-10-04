package org.example.module05_api_and_algorithms;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ESCENARIO 3: ALGORITMO CLÁSICO DE ENTREVISTA TÉCNICA - TOP K FREQUENT ELEMENTS
 * -----------------------------------------------------------------------------
 * Enunciado común en entrevistas (Google, Amazon, Meta, MercadoLibre):
 * "Dada una lista de palabras/tokens/logs de una API, retorna las 'k' palabras más frecuentes.
 * En caso de empate en frecuencia, ordénalas alfabéticamente."
 *
 * Implementación Imperativa:
 * Requiere HashMap manual, bucles anidados y una PriorityQueue (Min-Heap).
 *
 * Implementación Declarativa con Java Streams:
 * Se resuelve de forma limpia y legible en una sola expresión fluida:
 * 1. Sanitizar y tokenizar.
 * 2. groupingBy(palabra, counting()) -> Frecuencia.
 * 3. Ordenar por Entry.comparingByValue().reversed() luego comparingByKey().
 * 4. limit(k).
 * 5. Mapear a llave y recolectar.
 */
public class TopKFrequentElementsService {

    public record WordFrequency(String word, long count) {}

    public static List<WordFrequency> findTopKFrequent(List<String> rawTexts, int k) {
        if (rawTexts == null || rawTexts.isEmpty() || k <= 0) {
            return List.of();
        }

        return rawTexts.stream()
                // Aplanar oraciones en palabras individuales
                .flatMap(text -> Arrays.stream(text.split("[\\s,.;!?]+")))
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(w -> !w.isEmpty())
                // Agrupar por palabra y contar ocurrencias O(N)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                // Convertir mapa en stream de entradas
                .entrySet().stream()
                // Ordenar: primero mayor frecuencia (reversed), luego orden alfabético
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                // Tomar los primeros k
                .limit(k)
                // Mapear al DTO inmutable
                .map(entry -> new WordFrequency(entry.getKey(), entry.getValue()))
                .toList();
    }
}
