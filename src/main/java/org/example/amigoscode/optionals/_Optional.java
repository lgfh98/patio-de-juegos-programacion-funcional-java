package org.example.amigoscode.optionals;

import java.util.Optional;
import java.util.function.Supplier;

public class _Optional {
    static void main() {

        Supplier<IllegalStateException> exception = ()
                -> new IllegalStateException("exception custom message");

        try {
            Object value = Optional.ofNullable("Hello")
                    .orElseThrow(exception);
            System.out.println(value);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        Optional.ofNullable(null)
                .ifPresentOrElse(email -> {
                    // business logic
                    System.out.println("Sending message to " + email);
                }, () -> {
                    System.out.println("Cannot send email");
                });
    }
}
