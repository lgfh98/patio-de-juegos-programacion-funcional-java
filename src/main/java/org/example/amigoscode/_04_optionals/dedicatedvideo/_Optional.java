package org.example.amigoscode._04_optionals.dedicatedvideo;

import java.util.Optional;

public class _Optional {

    public static void main(String[] args) {

        // =========================================================================
        // 1. FORMAS DE CREAR UN OPTIONAL
        // =========================================================================

        // Optional.empty(): Representa explícitamente la ausencia de valor.
        // Optional<String> hello = Optional.empty();

        // Optional.of(valor): Espera SIEMPRE un valor no nulo.
        // Si el valor llega a ser null, falla de inmediato lanzando NullPointerException (fail-fast).
        // Optional<String> hello = Optional.of("hello");
        // Optional<String> hello = Optional.of(null); // -> Lanza NullPointerException en runtime

        // Optional.ofNullable(valor): La opción más segura cuando el origen es incierto.
        // Si recibe null, crea un Optional.empty(); si recibe un valor, crea un Optional con ese valor.
        Optional<String> hello = Optional.ofNullable(null);


        // =========================================================================
        // 2. VERIFICACIÓN DE ESTADO (isPresent vs isEmpty)
        // =========================================================================

        // isPresent(): Devuelve true si contiene un valor no nulo.
        System.out.println("¿Tiene valor?: " + hello.isPresent()); // false

        // isEmpty(): Introducido en Java 11. Equivalente a !isPresent(). Mejora la legibilidad.
        System.out.println("¿Está vacío?: " + hello.isEmpty());    // true


        // =========================================================================
        // 3. TRANSFORMACIÓN Y VALORES POR DEFECTO: orElse vs orElseGet
        // =========================================================================

        // map(): Si el Optional tiene valor, le aplica la función; si está vacío, propaga el vacío sin error.
        //
        // DIFERENCIA CRUCIAL ENTRE orElse() Y orElseGet():
        // - orElse(T other): Evaluación temprana (Eager). El valor por defecto se evalúa SIEMPRE,
        //   incluso si el Optional ya contenía un valor.
        // - orElseGet(Supplier<T> supplier): Evaluación perezosa (Lazy). La lambda solo se ejecuta
        //   SI Y SOLO SI el Optional está vacío. Ideal para llamadas a BD, cálculos o construcción de objetos pesados.
        String orElse = hello
                .map(String::toUpperCase)
                .orElseGet(() -> {
                    // Solo se ejecuta porque 'hello' es empty.
                    return "world";
                });

        System.out.println("Resultado con fallback: " + orElse);

        /*
         * CASO REAL EN ARQUITECTURA LIMPIA / WEB (Spring Boot):
         *
         * @GetMapping("/{id}")
         * public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
         *     return getProductUseCase.execute(new ProductId(id)) // Retorna Optional<Product>
         *             .map(productMapper::toResponse)             // Transforma si existe
         *             .map(ResponseEntity::ok)                    // Envuelve en HTTP 200 OK
         *             .orElseGet(() -> ResponseEntity.notFound().build()); // Retorna HTTP 404 de forma perezosa
         * }
         */


        // =========================================================================
        // 4. MANEJO DE AUSENCIA MEDIANTE EXCEPCIONES: orElseThrow
        // =========================================================================

        // orElseThrow(): Extrae el valor si existe, o lanza la excepción producida por el Supplier.
        // Nota: A partir de Java 10, orElseThrow() sin argumentos es el reemplazo preferido de get().
        try {
            String orElseThrow = hello
                    .map(String::toUpperCase)
                    .orElseThrow(() -> new IllegalStateException("El valor esperado no puede ser nulo"));
            System.out.println(orElseThrow);
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada como se esperaba: " + e.getMessage());
        }


        // =========================================================================
        // 5. CONSUMO DE VALORES CON EFECTOS SECUNDARIOS (Consumer / Runnable)
        // =========================================================================

        // ifPresent(Consumer): Ejecuta la acción SOLO si hay un valor presente. Si está vacío, no hace nada.
        hello.ifPresent(word -> System.out.println("Imprimiendo palabra: " + word));

        // ifPresentOrElse(Consumer, Runnable): Introducido en Java 9.
        // Permite manejar el flujo "if-else" funcionalmente:
        // - Primer parámetro: Qué hacer si existe el valor (Consumer).
        // - Segundo parámetro: Qué hacer si está ausente (Runnable).
        hello.ifPresentOrElse(
                word -> System.out.println("Valor presente: " + word),
                () -> System.out.println("Acción alternativa ejecutada: valor ausente")
        );


        // =========================================================================
        // 6. USO DE OPTIONAL EN MODELOS: ESTILO FUNCIONAL VS. ANTI-PATRÓN IMPERATIVO
        // =========================================================================

        Person james = new Person("James", "JAMES@gmail.com");

        // ENFOQUE RECOMENDADO (Declarativo / Funcional):
        // Flujo encadenado, seguro contra null y fácil de leer sin bloques condicionales anidados.
        String emailNotProvided = james.getEmail()
                .map(String::toLowerCase)
                .orElse("email not provided");

        System.out.println("Email formateado: " + emailNotProvided);

        // ANTI-PATRÓN COMÚN (Estilo Imperativo):
        // Usar isPresent() seguido de get() replica exactamente el viejo "if (x != null)",
        // perdiendo todas las ventajas de legibilidad y seguridad del API funcional de Optional.
        if (james.getEmail().isPresent()) {
            String email = james.getEmail().get(); // get() directo es desaconsejado: arriesga NoSuchElementException si se olvida el if
            System.out.println(email.toLowerCase());
        } else {
            System.out.println("email not provided");
        }
    }

    /**
     * BUENAS PRÁCTICAS DE DISEÑO CON OPTIONAL:
     * 1. No usar Optional como tipo de campo (field) de una entidad o DTO:
     * No implementa Serializable y agrega sobrecarga de memoria innecesaria.
     * 2. El tipo del campo debe ser String (nullable) y encapsular la opcionalidad en el Getter.
     * 3. No usar Optional como parámetro en constructores o métodos (usa sobrecarga en su lugar).
     */
    static class Person {
        private final String name;
        private final String email; // El campo se mantiene simple y serializable

        Person(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        // El getter comunica explícitamente al cliente de la API que este valor puede no existir.
        public Optional<String> getEmail() {
            return Optional.ofNullable(email);
        }
    }
}