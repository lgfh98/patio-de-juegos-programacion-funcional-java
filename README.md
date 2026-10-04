# 🚀 Guía Definitiva y Patio de Juegos: Programación Funcional y Streams en Java

Bienvenido a este laboratorio práctico diseñado para dominar la **Programación Funcional (FP)** y el API de **Streams en Java** desde los fundamentos teóricos hasta su aplicación en **APIs empresariales, endpoints algorítmicos y entrevistas técnicas senior**.

---

## 🧭 Mapa de Ruta y Arquitectura del Repositorio

El proyecto está organizado en 6 módulos incrementales con código de producción y tests unitarios automatizados con **JUnit 5** y **AssertJ**:

```
src/
├── main/java/org/example/
│   ├── Main.java                                    # Dashboard interactivo ejecutable
│   │
│   ├── module01_foundations/                        # De Imperativo a Funcional
│   │   ├── ImperativeVsDeclarative.java             # Cambio de mentalidad: 'Cómo' vs 'Qué'
│   │   ├── PureFunctionsAndImmutability.java        # Funciones puras, side-effects y Records inmutables
│   │   ├── FunctionalInterfacesCatalog.java         # Los 4 pilares: Predicate, Function, Consumer, Supplier
│   │   ├── MethodReferencesMastery.java             # Los 4 tipos de referencias a métodos (::)
│   │   └── CustomFunctionalInterface.java           # SAM interfaces personalizadas (TriFunction, Validator)
│   │
│   ├── module02_streams_core/                       # Anatomía interna de Streams
│   │   ├── StreamLifecycleAndPipelines.java         # Lazy evaluation, Loop Fusion y consumo único
│   │   ├── StatelessVsStatefulOps.java              # O(1) memoria vs Buffers (sorted, distinct, limit)
│   │   ├── ShortCircuitOperations.java              # anyMatch, findFirst vs findAny, Streams infinitos
│   │   └── PrimitiveStreamsPerformance.java         # IntStream, LongStream, costo de Boxing y summaryStatistics
│   │
│   ├── module03_advanced_manipulation/              # Transformaciones complejas de datos
│   │   ├── FlatMapExplained.java                    # Aplanamiento de colecciones 1-a-muchos y Optional flatMap
│   │   ├── ReduceVsCollect.java                     # Plegado inmutable vs Acumulación en contenedor mutable
│   │   └── CollectorsMastery.java                   # groupingBy downstream, partitioningBy, toMap seguro y teeing
│   │
│   ├── module04_optional_mastery/                   # Manejo idiomático de ausencia de valor
│   │   ├── OptionalIdiomaticUsage.java              # map, filter, flatMap y composición monádica
│   │   └── OptionalInterviewTraps.java              # Trampa orElse vs orElseGet y anti-patrones en APIs
│   │
│   ├── module05_api_and_algorithms/                 # Escenarios reales de APIs y algoritmos
│   │   ├── model/                                   # Records inmutables de dominio y Mónada Either
│   │   ├── AnomalyDetectorService.java              # Detección de fraude/anomalías con Z-Score estadístico
│   │   ├── OrderSettlementService.java              # Liquidación marketplace multi-vendedor
│   │   ├── TopKFrequentElementsService.java         # Algoritmo clásico de entrevista: Top-K con desempate léxico
│   │   └── FunctionalExceptionHandling.java         # Manejo limpio de Checked Exceptions con Either
│   │
│   └── module06_parallel_and_performance/           # Rendimiento y Concurrencia
│       └── ParallelStreamsWhenAndWhyNot.java        # ForkJoinPool, Modelo N*Q, y el peligro de estado compartido
│
└── test/java/org/example/                           # 23 Pruebas unitarias que verifican cada concepto
```

---

## 🧠 Modelos Mentales Clave

### 1. ¿Qué es realmente un Stream?
Un `Stream` **NO es una estructura de datos**. No almacena información en memoria. Es un conducto o tubería (pipeline) declarativo que opera sobre una fuente (colección, arreglo, generador, I/O) aplicando transformaciones bajo demanda (**Lazy Evaluation**).

```
[ Fuente (Source) ] ---> [ Operaciones Intermedias (Lazy) ] ---> [ Operación Terminal (Eager) ]
  List.of(1, 2, 3)          .filter(...)       .map(...)                .toList()
```

### 2. Los 4 Pilares de `java.util.function`

| Interfaz Funcional | Firma SAM | Intención Principal | Ejemplo Típico |
|---|---|---|---|
| `Predicate<T>` | `T -> boolean` | Filtrar, validar, consultar condiciones | `.filter(User::active)` |
| `Function<T, R>` | `T -> R` | Transformar, proyectar de un tipo a otro | `.map(User::name)` |
| `Consumer<T>` | `T -> void` | Efecto secundario controlado (logs, métricas) | `.forEach(System.out::println)` |
| `Supplier<T>` | `() -> T` | Provisión perezosa (Lazy), factoría | `Optional.orElseGet(factory::create)` |

### 3. La Matriz de Method References (`::`)

Una de las preguntas favoritas de los entrevistadores:

| Tipo | Sintaxis | Equivalente Lambda | Ejemplo |
|---|---|---|---|
| **1. Estático** | `ClassName::staticMethod` | `(x) -> Math.abs(x)` | `Math::abs` |
| **2. Instancia Ligada (Bound)** | `instance::instanceMethod` | `(x) -> logger.info(x)` | `logger::info` |
| **3. Instancia No Ligada (Unbound)** | `ClassName::instanceMethod` | `(user) -> user.getName()` | `User::getName` |
| **4. Constructor** | `ClassName::new` | `() -> new ArrayList<>()` | `ArrayList::new` |

> 💡 **En el tipo 3 (Unbound)**, el primer parámetro de la función actúa como el objeto objetivo (`this`) sobre el cual se invoca el método.

---

## ⚡ Operaciones Intermedias: Stateless vs Stateful

- **Stateless (Sin Estado)**: `filter()`, `map()`, `peek()`, `flatMap()`.
  - Cada elemento se procesa de forma aislada.
  - Memoria constante $O(1)$ por elemento.
  - Pueden procesar streams de datos infinitos sin saturar la memoria.
- **Stateful (Con Estado)**: `sorted()`, `distinct()`, `limit()`, `skip()`.
  - Deben almacenar elementos previos o buffers en memoria.
  - **Peligro en entrevista**: Invocar `.sorted()` sobre un stream infinito (`Stream.iterate`) provocará un `OutOfMemoryError` porque necesita ver todos los elementos antes de emitir el primero.

---

## 🛠️ Herramientas Avanzadas: `flatMap`, `reduce` y `Collectors`

### Map vs FlatMap
- `map(T -> R)`: 1 a 1. Si tu función retorna una lista, obtienes `Stream<List<R>>`.
- `flatMap(T -> Stream<R>)`: 1 a muchos. Aplana todas las sub-listas o streams anidados en un único stream continuo.

### Reduce vs Collect
- `reduce()`: Pensado para **plegado inmutable** (números, strings inmutables).
- `collect()`: Pensado para **acumulación en contenedores mutables** (Listas, Sets, Mapas). Usar `reduce` para acumular en listas crea copias innecesarias con costo cuadrático $O(N^2)$.

### El bug de `Collectors.toMap` en producción
```java
// ❌ PELIGROSO: Lanza IllegalStateException si hay llaves repetidas
list.stream().collect(Collectors.toMap(User::role, User::name));

// ✅ CORRECTO: Provee función de resolución de colisiones
list.stream().collect(Collectors.toMap(
    User::role,
    User::name,
    (existing, replacement) -> existing // Merge function
));
```

---

## 🎯 Escenarios de Endpoints Algorítmicos (Módulo 05)

1. **Detección de Anomalías Financieras (`AnomalyDetectorService`)**:
   - Agrupa transacciones aprobadas por cuenta.
   - Calcula media aritmética y desviación estándar en una pasada funcional.
   - Detecta outliers evaluando el puntaje Z ($Z\text{-score} > 1.5$) y genera alertas de auditoría.

2. **Liquidación Multi-Vendedor en Marketplace (`OrderSettlementService`)**:
   - Aplana órdenes anidadas (`flatMap`).
   - Aplica comisiones escalonadas por categoría mediante reglas declarativas.
   - Agrupa por vendedor y consolida montos brutos, retenciones fiscales, comisiones y pagos netos.

3. **Top-K Elementos Más Frecuentes (`TopKFrequentElementsService`)**:
   - Resuelve el clásico reto técnico de algoritmos con streams limpios: `groupingBy(identity, counting())`, ordenamiento compuesto (`comparingByValue().reversed().thenComparing(getKey())`) y corte con `limit(k)`.

4. **Manejo Funcional de Excepciones con Either (`FunctionalExceptionHandling`)**:
   - Evita ensuciar las lambdas con bloques `try-catch`.
   - Modela éxitos (`Right`) y fallas (`Left`) permitiendo que el stream continúe y retorne una respuesta detallada con los errores detectados.

---

## ⚠️ Las 10 Preguntas y Trampas Clásicas de Entrevista Técnica

1. **¿Qué imprime un Stream que solo tiene operaciones intermedias y no tiene operación terminal?**
   - *Respuesta*: No imprime nada ni ejecuta ninguna operación. Los Streams son perezosos (Lazy).
2. **¿Se puede reutilizar un Stream ya consumido?**
   - *Respuesta*: No. Lanza `IllegalStateException: stream has already been operated upon or closed`. Si necesitas reutilizar la lógica, almacena una `Supplier<Stream<T>>` o la colección base.
3. **¿Cuál es la diferencia crítica entre `Optional.orElse()` y `Optional.orElseGet()`?**
   - *Respuesta*: `orElse(val)` evalúa su argumento **siempre de forma anticipada (eager)**. Si llama a una base de datos o servicio externo, se ejecutará incluso si el Optional tiene valor. `orElseGet(Supplier)` es **perezoso (lazy)** y solo se ejecuta si el Optional está vacío.
4. **¿Por qué `Optional` no debería usarse como parámetro de métodos o campo de entidad JPA?**
   - *Respuesta*: Como parámetro genera sobrecarga y obliga al cliente a envolver en `Optional.ofNullable`. En JPA/DTOs no implementa `Serializable` y añade sobrecarga de memoria innecesaria.
5. **¿Qué diferencia hay entre `findFirst()` y `findAny()`?**
   - *Respuesta*: `findFirst()` garantiza el orden de encuentro (encounter order). `findAny()` es no-determinista y está optimizado para streams paralelos donde el primer hilo que termine retorna el resultado.
6. **¿Por qué `Stream<Integer>` es ineficiente para operaciones matemáticas intensivas?**
   - *Respuesta*: Por el costo de auto-boxing y auto-unboxing entre primitivo `int` y objeto `Integer`. Se debe usar `IntStream`, `LongStream` o `DoubleStream`.
7. **¿Qué sucede al ejecutar `unsafeList.add(x)` dentro de un `.parallelStream()`?**
   - *Respuesta*: `ArrayList` no es thread-safe. Produce `ArrayIndexOutOfBoundsException`, corrupción de datos o elementos nulos. Se debe usar `.collect(toList())`.
8. **¿En qué casos NO se deben usar Parallel Streams?**
   - *Respuesta*: Operaciones I/O bloqueantes (HTTP, BD), mutación de estado compartido, colecciones difíciles de dividir (como `LinkedList`), o cuando el costo computacional total $N \times Q < 10,000$.
9. **¿Qué hace `Collectors.teeing()` introducido en Java 12?**
   - *Respuesta*: Permite ejecutar dos Collectors simultáneamente sobre el mismo stream y combinar sus resultados con una función finalizadora (ej. calcular min y max en un solo recorrido).
10. **¿Cómo romper un stream infinito de forma segura?**
   - *Respuesta*: Con operaciones de corto circuito como `.limit(n)` o en Java 9+ con `.takeWhile(predicate)`.

---

## 💻 Cómo Ejecutar el Proyecto

### 1. Ejecutar el Dashboard Interactivo de Demostración:
```bash
./gradlew run
```

### 2. Ejecutar la Batería Completa de 23 Pruebas Unitarias:
```bash
./gradlew test
```

### 3. Ver Reporte de Pruebas en HTML:
Abre en tu navegador:
```
build/reports/tests/test/index.html
```
