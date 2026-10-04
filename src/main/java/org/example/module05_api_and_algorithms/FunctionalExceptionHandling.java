package org.example.module05_api_and_algorithms;

import org.example.module05_api_and_algorithms.model.Either;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ESCENARIO 4: MANEJO DE EXCEPCIONES EN STREAMS Y APIS (Patrón Either)
 * -----------------------------------------------------------------------------
 * Uno de los dolores más comunes de los desarrolladores en Java:
 * "Las lambdas de java.util.function no declaran 'throws Exception'.
 * Si dentro de un .map(...) llamo a un método que arroja una Checked Exception (ej. parseo JSON, URL, IO),
 * el código no compila a menos que meta un feo bloque try-catch dentro de la lambda."
 *
 * DOS SOLUCIONES PROFESIONALES:
 * 1. ThrowingFunction Wrapper: Convierte Checked Exceptions a Unchecked de forma controlada.
 * 2. Mónada Either: Captura tanto el fallo (Left) como el éxito (Right) sin detener el stream,
 *    permitiendo separar los resultados exitosos de los errores para retornar un reporte detallado en la API.
 */
public class FunctionalExceptionHandling {

    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;

        /**
         * Adapta una ThrowingFunction a un Function estándar de Java envolviendo en RuntimeException.
         */
        static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R, Exception> fn) {
            return t -> {
                try {
                    return fn.apply(t);
                } catch (Exception ex) {
                    throw new RuntimeException("Error en pipeline funcional: " + ex.getMessage(), ex);
                }
            };
        }

        /**
         * Adapta la función para retornar Either<String, R>, sin lanzar excepciones.
         */
        static <T, R> Function<T, Either<String, R>> liftToEither(ThrowingFunction<T, R, Exception> fn) {
            return t -> {
                try {
                    return Either.right(fn.apply(t));
                } catch (Exception ex) {
                    return Either.left("Fallo al procesar '" + t + "': " + ex.getMessage());
                }
            };
        }
    }

    /**
     * Ejemplo práctico en una API:
     * Validar y construir URIs a partir de un listado de strings.
     * Retorna una partición: URIs válidas por un lado, errores descriptivos por el otro.
     */
    public record BatchUriResult(List<URI> successfulUris, List<String> errorMessages) {}

    public static BatchUriResult parseUrisSafely(List<String> rawUris) {
        if (rawUris == null) return new BatchUriResult(List.of(), List.of());

        // Mapeamos a Either<String, URI>
        List<Either<String, URI>> parsedResults = rawUris.stream()
                .map(ThrowingFunction.liftToEither(raw -> {
                    URI uri = URI.create(raw);
                    if (uri.getHost() == null) {
                        throw new IllegalArgumentException("Host no válido");
                    }
                    return uri;
                }))
                .toList();

        // Separamos éxitos y fallas
        List<URI> successes = parsedResults.stream()
                .filter(Either::isRight)
                .flatMap(e -> e.getRight().stream())
                .toList();

        List<String> errors = parsedResults.stream()
                .filter(Either::isLeft)
                .flatMap(e -> e.getLeft().stream())
                .toList();

        return new BatchUriResult(successes, errors);
    }
}
