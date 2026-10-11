package org.example.amigoscode.functionalinterface;

import java.util.List;
import java.util.function.Supplier;

public class _Supplier {
    static void main() {
        System.out.println(getDBConnectionUrl());
        System.out.println(getDBConnectionUrlsSupplier.get());
    }

    static String getDBConnectionUrl() {
        return "jdbc://localhost:5432/users";

    }

    static Supplier<List<String>> getDBConnectionUrlsSupplier = ()
            -> List.of(
            "jdbc://localhost:5432/users",
            "jdbc://localhost:5432/customer");
}

