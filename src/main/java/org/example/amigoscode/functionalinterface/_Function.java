package org.example.amigoscode.functionalinterface;

import java.util.function.BiFunction;
import java.util.function.Function;

public class _Function {
    static void main() {
        // Function takes 1 oargument and produces one result
        int increment = incrementByOne(1);
        System.out.println(increment);

        Integer increment2 = incrementByOneFunction.apply(1);
        System.out.println(increment2);

        int multiply = multiplyBy10Function.apply(increment2);
        System.out.println(multiply);

        Function<Integer, Integer> addBy1AndThenMultiplyBy10Function = incrementByOneFunction.andThen(multiplyBy10Function);
        System.out.println(addBy1AndThenMultiplyBy10Function.apply(4));

        // BiFunction takes 2 arguments and produces 1 result

        System.out.println(
                addBy1AndThenMultiplyByNumber(4, 100)
        );
        System.out.println(
                addBy1AndThenMultiplyByNumberBiFunction.apply(4, 100)
        );
        System.out.println(
                new _Function().addBy1AndThenMultiplyByNumberBiFunctionStaticTest.apply(4, 100)
        );
    }

    static Function<Integer, Integer> incrementByOneFunction = number -> number + 1;

    static Function<Integer, Integer> multiplyBy10Function = number -> number * 10;

    static int incrementByOne(int number) {
        return number + 1;
    }

    static int addBy1AndThenMultiplyByNumber(int number, int numToMultiplyBy) {
        return (number + 1) * numToMultiplyBy;
    }

    static BiFunction<Integer, Integer, Integer> addBy1AndThenMultiplyByNumberBiFunction = (Integer number, Integer numToMultiplyBy) -> (number + 1) * numToMultiplyBy;

    BiFunction<Integer, Integer, Integer> addBy1AndThenMultiplyByNumberBiFunctionStaticTest = (Integer number, Integer numToMultiplyBy) -> (number + 1) * numToMultiplyBy;
}
