package org.example.amigoscode.functionalinterface;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class _Predicate {
    static void main() {
        System.out.println("Without predicate");
        System.out.println(isPhoneNumberValid("07000000000"));
        System.out.println(isPhoneNumberValid("0700000000"));
        System.out.println(isPhoneNumberValid("09009877300"));

        System.out.println("With predicate");
        System.out.println(isPhoneNumberValidPredicate.test("07000000000"));
        System.out.println(isPhoneNumberValidPredicate.test("0700000000"));
        System.out.println(isPhoneNumberValidPredicate.test("09009877300"));

        System.out.println(
                "Is phone number valid and contains number 3 = " + isPhoneNumberValidPredicate.and(containsNumber3).test("09009877300")
        );
        System.out.println(
                "Is phone number valid and contains number 3 = " + isPhoneNumberValidPredicate.and(containsNumber3).test("07000030000")
        );
        System.out.println(
                "The phone number starts in 5 and contains 5 = " + containsNumber.and(startWithNumber).test("0700050000", "5")
        );
        // Configuramos cada predicado con su propio valor independiente usando .apply()
        boolean result = startsWith.apply("07")
                .and(contains.apply("5"))
                .test("07000500000");

        System.out.println("Resultado: " + result);
    }

    static boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.startsWith("07") && phoneNumber.length() == 11;

    }

    static Predicate<String> isPhoneNumberValidPredicate = phoneNumber ->
            phoneNumber.startsWith("07") && phoneNumber.length() == 11;

    static Predicate<String> containsNumber3 = phoneNumber ->
            phoneNumber.contains("3");

    static BiPredicate<String, String> containsNumber = (String phoneNumber, String number) ->
            phoneNumber.contains(number);

    static BiPredicate<String, String> startWithNumber = (String phoneNumber, String number) ->
            phoneNumber.startsWith(number);

    // Recibe el prefijo y RETORNA un Predicate<String>
    static Function<String, Predicate<String>> startsWith =
            prefix -> phone -> phone.startsWith(prefix);

    // Recibe el número a buscar y RETORNA un Predicate<String>
    static Function<String, Predicate<String>> contains =
            number -> phone -> phone.contains(number);
}
