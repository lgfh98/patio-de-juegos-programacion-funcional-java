# 📘 Guía de Repaso Incremental: Programación Funcional en Java (Amigoscode)

Esta guía documenta la evolución cronológica del código desarrollado en el paquete `org.example.amigoscode`. Está estructurada como un **repaso paso a paso**, explicando cómo cada tema construye sobre el anterior para pasar del paradigma imperativo tradicional al diseño funcional moderno en Java.

---

## 🧭 Mapa de Ruta Cronológico

```
[ 01. Imperativo vs Declarativo ]
              │
              ▼
[ 02. Las 4 Interfaces Funcionales Básicas ] ──► (Function, Consumer, Predicate, Supplier)
              │
              ▼
[ 03. Java Streams API ] ──────────────────────► (Pipelines declarativos, map, filter, matchers)
              │
              ▼
[ 04. Optional Mastery ] ──────────────────────► (Manejo de ausencia, orElseGet vs orElse, Java 9+)
              │
              ▼
[ 05. Patrón Combinator ] ─────────────────────► (Validaciones composables con cortocircuito)
              │
              ▼
[ 06. Sección Final ] ─────────────────────────► (Callbacks estilo JS y anatomía de Lambdas)
```

---

## 📑 Índice de Módulos

1. [Paso 1: Enfoque Imperativo vs. Declarativo (`_01_imperative`)](#1-paso-1-enfoque-imperativo-vs-declarativo-_01_imperative)
2. [Paso 2: Las 4 Interfaces Funcionales Básicas (`_02_functionalinterface`)](#2-paso-2-las-4-interfaces-funcionales-básicas-_02_functionalinterface)
   - [2.1 Function & BiFunction](#21-function-y-bifunction-transformación-pura)
   - [2.2 Consumer & BiConsumer](#22-consumer-y-biconsumer-efectos-secundarios)
   - [2.3 Predicate, BiPredicate y Currificación](#23-predicate-bipredicate-y-currificación-evaluación-lógica)
   - [2.4 Supplier](#24-supplier-evaluación-perezosa)
3. [Paso 3: Streams API (`_03_streams`)](#3-paso-3-streams-api-_03_streams)
4. [Paso 4: Optional (`_04_optionals`)](#4-paso-4-optional-manejo-de-ausencia-_04_optionals)
5. [Paso 5: Patrón de Diseño Combinator (`_05_combinator`)](#5-paso-5-patrón-combinator-_05_combinator)
6. [Paso 6: Callbacks y Anatomía de Lambdas (`_06_finalsection`)](#6-paso-6-callbacks-y-anatomía-de-lambdas-_06_finalsection)
7. [🧠 Matriz Rápida de Decisión (Cheat Sheet)](#-matriz-rápida-de-decisión-cheat-sheet)

---

## 1. Paso 1: Enfoque Imperativo vs. Declarativo (`_01_imperative`)

📁 **Código:** [`_01_imperative/Main.java`](_01_imperative/Main.java)

### 💡 El cambio de mentalidad
El punto de partida de la programación funcional es pasar de describir **cómo** resolver un problema paso a paso a declarar **qué** resultado se necesita.

| Aspecto | Enfoque Imperativo | Enfoque Declarativo (Funcional) |
|---|---|---|
| **Pregunta central** | ¿*Cómo* lo hago paso a paso? | ¿*Qué* datos quiero obtener? |
| **Control de flujo** | Bucles manuales (`for`, `while`), `if` anidados | Pipelines declarativos (`.filter()`, `.map()`) |
| **Estado** | Acumuladores mutables (`new ArrayList<>()`, `.add()`) | Flujos inmutables recolectados (`.toList()`) |
| **Paralelización** | Compleja (bloqueos, condiciones de carrera) | Trivial (`.parallelStream()`) |

```java
// ❌ IMPERATIVO: mutable, bucle manual, lógica acoplada
List<Person> females = new ArrayList<>();
for (Person person : people) {
    if (Gender.FEMALE.equals(person.gender)) {
        females.add(person);
    }
}

//  DECLARATIVO: sin variables intermedias mutables, intención explícita
List<Person> females = people.stream()
        .filter(person -> Gender.FEMALE.equals(person.gender))
        .toList();
```

---

## 2. Paso 2: Las 4 Interfaces Funcionales Básicas (`_02_functionalinterface`)

📁 **Carpeta:** [`_02_functionalinterface/`](_02_functionalinterface/)

En Java, una **Interfaz Funcional** es una interfaz con exactamente un único método abstracto (SAM: *Single Abstract Method*), anotada opcionalmente con `@FunctionalInterface`. Permiten tratar funciones como **objetos de primera clase** (guardar en variables, pasar como parámetros, retornar desde métodos).

### 2.1 Function y BiFunction: Transformación Pura
📁 [`_Function.java`](_02_functionalinterface/_Function.java)

- **`Function<T, R>`**: Toma 1 argumento `T` y retorna `R`. Método: `.apply(t)`.
- **`BiFunction<T, U, R>`**: Toma 2 argumentos `(T, U)` y retorna `R`. Método: `.apply(t, u)`.

#### Composición Funcional (`.andThen` vs `.compose`):
- `f.andThen(g)` $\rightarrow$ Ejecuta primero `f` y luego `g`: **$g(f(x))$**
- `f.compose(g)` $\rightarrow$ Ejecuta primero `g` y luego `f`: **$f(g(x))$**

```java
Function<Integer, Integer> incrementByOne = x -> x + 1;
Function<Integer, Integer> multiplyBy10 = x -> x * 10;

// (4 + 1) * 10 = 50
incrementByOne.andThen(multiplyBy10).apply(4);

// (4 * 10) + 1 = 41
incrementByOne.compose(multiplyBy10).apply(4);
```

---

### 2.2 Consumer y BiConsumer: Efectos Secundarios
📁 [`_Consumer.java`](_02_functionalinterface/_Consumer.java)

- **`Consumer<T>`**: Recibe 1 argumento `T` y **no retorna nada** (`void`). Método: `.accept(t)`.
- **`BiConsumer<T, U>`**: Recibe 2 argumentos `(T, U)` y retorna `void`. Método: `.accept(t, u)`.
- **Propósito:** Ejecutar acciones con **efectos secundarios** controlados: imprimir en consola, escribir en BD, enviar notificaciones o registrar logs.

```java
Consumer<Customer> greet = c -> System.out.println("Hola " + c.name);
Consumer<Customer> audit = c -> System.out.println("[AUDIT] Acceso de " + c.name);

// Encadenamiento con .andThen()
greet.andThen(audit).accept(luis);
```

---

### 2.3 Predicate, BiPredicate y Currificación: Evaluación Lógica
📁 [`_Predicate.java`](_02_functionalinterface/_Predicate.java)

- **`Predicate<T>`**: Recibe 1 argumento `T` y retorna un `boolean`. Método: `.test(t)`.
- **`BiPredicate<T, U>`**: Recibe 2 argumentos y retorna `boolean`. Método: `.test(t, u)`.
- **Composición lógica:** `.and()`, `.or()`, `.negate()`. Aplica evaluación en cortocircuito (*short-circuit*).

#### De BiPredicate a Currificación (Higher-Order Functions):
Cuando encadenamos `BiPredicate` con `.and()`, ambos están forzados a recibir los mismos parámetros. La **currificación** resuelve esto desacoplando la **configuración** de la regla respecto al **dato** final:

```java
// Firma: String -> (String -> boolean)
Function<String, Predicate<String>> startsWith = prefix -> phone -> phone.startsWith(prefix);
Function<String, Predicate<String>> contains   = digit  -> phone -> phone.contains(digit);

// Se configuran independientemente y se combinan como Predicate<String>
boolean valid = startsWith.apply("07")
        .and(contains.apply("5"))
        .test("07000500000"); // true
```

---

### 2.4 Supplier: Evaluación Perezosa
📁 [`_Supplier.java`](_02_functionalinterface/_Supplier.java)

- **`Supplier<T>`**: **No recibe argumentos** y retorna un valor `T`. Método: `.get()`.
- **Mental Model:** "Fábrica" o "Generador diferido".
- **Lazy Evaluation:** El código dentro del lambda **no se ejecuta** hasta que se llama explícitamente a `.get()`. Fundamental para:
  1. `Optional.orElseGet(Supplier)`: sólo calcula el valor de respaldo si está vacío.
  2. `CompletableFuture.supplyAsync(Supplier)`: cómputo asíncrono.
  3. Evitar operaciones costosas en logs o inicializaciones pesadas.

---

## 3. Paso 3: Streams API (`_03_streams`)

📁 **Código:** [`_03_streams/_Stream.java`](_03_streams/_Stream.java)

### 🌊 ¿Qué es un Stream?
Un `Stream` **no almacena datos**. Es un conducto de operaciones que procesa datos bajo demanda.

```
[ Fuente (Source) ] ───► [ Operaciones Intermedias (Lazy) ] ───► [ Operación Terminal (Eager) ]
  people.stream()              .map(...)       .filter(...)              .collect(...) / .toList()
```

### Operaciones clave cubiertas:
1. **Transformaciones y Recolección:**
   - `.map()`: Proyecta elementos de un tipo a otro ($A \rightarrow B$).
   - `.collect(Collectors.toSet())`: Recolecta asegurando unicidad.
   - `.toList()`: Lista inmutable compacta (Java 16+).
2. **Deconstrucción pedagógica:**
   - Ver cómo el pipeline conecta interfaces funcionales explícitas (`Function`, `Consumer`, `Predicate`).
3. **Predicados terminales con cortocircuito:**
   - `allMatch(Predicate)`: `true` si **todos** cumplen la condición.
   - `anyMatch(Predicate)`: `true` si **al menos uno** cumple la condición.
   - `noneMatch(Predicate)`: `true` si **ninguno** cumple la condición.

---

## 4. Paso 4: Optional (Manejo de Ausencia) (`_04_optionals`)

📁 **Archivos:**
- Resumen del curso: [`_04_optionals/_Optional.java`](_04_optionals/_Optional.java)
- Guía profunda y casos reales: [`_04_optionals/dedicatedvideo/_Optional.java`](_04_optionals/dedicatedvideo/_Optional.java)

### 🎯 Objetivo
Erradicar el temido `NullPointerException` modelando explícitamente la presencia o ausencia de un valor en el sistema de tipos.

### Métodos esenciales y diferencias críticas:

| Método | Tipo | Descripción y Cuándo Usarlo |
|---|---|---|
| `Optional.of(val)` | Creación | Espera valor no nulo; lanza `NullPointerException` si es null (fail-fast). |
| `Optional.ofNullable(val)` | Creación | Seguro: si recibe null produce `Optional.empty()`. |
| `orElse(defaultVal)` | Fallback | **Eager**: `defaultVal` se calcula **siempre**, incluso si hay valor presente. |
| `orElseGet(Supplier)` | Fallback | **Lazy**: El `Supplier` solo se ejecuta si el Optional está vacío (ideal para BD o cómputos pesados). |
| `orElseThrow(Supplier)` | Excepción | Lanza excepción de negocio de forma diferida si no hay valor. |
| `ifPresentOrElse(Consumer, Runnable)` | Flujo (Java 9+) | Sustituye el bloque `if-else`: ejecuta el `Consumer` si existe o el `Runnable` si está vacío. |

> [!WARNING]
> **Anti-patrón común:** Usar `if (opt.isPresent()) { opt.get(); }` replica el viejo `if (x != null)`. En su lugar, usa el pipeline funcional: `.map(...)`, `.filter(...)`, `.orElseGet(...)`.

---

## 5. Paso 5: Patrón Combinator (`_05_combinator`)

📁 **Carpeta:** [`_05_combinator/`](_05_combinator/)
- Entidad: [`Customer.java`](_05_combinator/Customer.java)
- Enfoque imperativo: [`CustomerValidatorService.java`](_05_combinator/CustomerValidatorService.java)
- Patrón Combinator: [`CustomerRegistrationValidator.java`](_05_combinator/CustomerRegistrationValidator.java)
- Demostración: [`Main.java`](_05_combinator/Main.java)

### 🧱 Problema del servicio tradicional
```java
// Devuelve boolean ciego: si falla, ¿cuál regla falló? ¿email? ¿edad? ¿teléfono?
boolean isValid = new CustomerValidatorService().isValid(customer);
```

### 🚀 Solución: Patrón Combinator
Una interfaz funcional que hereda de `Function<Customer, ValidationResult>` y define validadores atómicos y combinadores `default`:

```java
public interface CustomerRegistrationValidator extends Function<Customer, ValidationResult> {

    static CustomerRegistrationValidator isEmailValid() { ... }
    static CustomerRegistrationValidator isPhoneNumberValid() { ... }
    static CustomerRegistrationValidator isAnAdult() { ... }

    // El combinador: aplica cortocircuito funcional
    default CustomerRegistrationValidator and(CustomerRegistrationValidator other) {
        return customer -> {
            ValidationResult result = this.apply(customer);
            return result.equals(SUCCESS) ? other.apply(customer) : result;
        };
    }

    enum ValidationResult {
        SUCCESS, PHONE_NUMBER_NOT_VALID, EMAIL_NOT_VALID, IS_NOT_AN_ADULT
    }
}
```

#### Uso fluido y expresivo:
```java
ValidationResult result = isEmailValid()
        .and(isPhoneNumberValid())
        .and(isAnAdult())
        .apply(customer1);

if (result != ValidationResult.SUCCESS) {
    throw new IllegalStateException("Fallo de validación: " + result.name());
}
```

---

## 6. Paso 6: Callbacks y Anatomía de Lambdas (`_06_finalsection`)

📁 **Carpeta:** [`_06_finalsection/`](_06_finalsection/)

### 6.1 Callbacks estilo JavaScript en Java
📁 [`Callbacks.java`](_06_finalsection/Callbacks.java)

En JavaScript, cualquier función puede pasarse como callback. En Java moderno usamos interfaces funcionales:
- **`Consumer<T>`**: Callback con parámetro contextual $\rightarrow$ `callback.accept(data)`.
- **`Runnable`**: Callback sin parámetros $\rightarrow$ `callback.run()`.

```java
// Con Consumer (recibe dato)
static void hello(String name, String lastName, Consumer<String> callback) {
    if (lastName != null) System.out.println(lastName);
    else callback.accept(name);
}

// Con Runnable (sin parámetros)
static void hello2(String name, String lastName, Runnable callback) {
    if (lastName != null) System.out.println(lastName);
    else callback.run();
}
```

### 6.2 Anatomía de Lambdas: Puras vs. Impuras
📁 [`Lambdas.java`](_06_finalsection/Lambdas.java)

- **One-liner:** `(a, b) -> a + b` (retorno implícito, concisa).
- **Block body:** `(a, b) -> { // validaciones; return resultado; }` (requiere `return`).
- **Funciones Puras:** Mismos inputs producen mismos outputs, **sin efectos secundarios** (I/O, mutaciones).
- **Funciones Impuras:** Realizan I/O (`System.out.println`), mutan estado externo o dependen de reloj/red.

---

## 🧠 Matriz Rápida de Decisión (Cheat Sheet)

¿Tienes una tarea y no sabes qué interfaz funcional usar? Usa esta guía de decisión:

```
¿Qué necesitas que haga la función?
│
├── ¿Transformar un dato en otro? (Entrada -> Salida)
│    ├── 1 entrada: Function<T, R>          [.apply(t)]
│    └── 2 entradas: BiFunction<T, U, R>    [.apply(t, u)]
│
├── ¿Evaluar una condición booleana? (Entrada -> boolean)
│    ├── 1 entrada: Predicate<T>            [.test(t)]
│    └── 2 entradas: BiPredicate<T, U>      [.test(t, u)]
│
├── ¿Consumir un dato con efecto secundario? (Entrada -> void)
│    ├── 1 entrada: Consumer<T>             [.accept(t)]
│    └── 2 entradas: BiConsumer<T, U>       [.accept(t, u)]
│
├── ¿Proveer o generar un dato bajo demanda? (void -> Salida)
│    └── 0 entradas: Supplier<T>            [.get()]
│
└── ¿Ejecutar una acción simple sin entrada ni salida? (void -> void)
     └── Runnable                           [.run()]
```

---

> [!TIP]
> **Para repasar antes de una entrevista:**
> 1. Explica la diferencia entre `orElse` y `orElseGet`.
> 2. Explica cómo la **Currificación** desacopla parámetros en predicados complejos.
> 3. Diseña el patrón **Combinator** en pizarra: una interfaz funcional que extiende `Function<T, R>` con un método default `.and()`.
> 4. Explica por qué un Stream no almacena datos y qué significa *Lazy Evaluation*.
