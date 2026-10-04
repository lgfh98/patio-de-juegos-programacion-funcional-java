package org.example.module04_optional_mastery;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OptionalMasteryTest {

    @Test
    @DisplayName("Debe transformar y filtrar Optional de manera funcional limpia")
    void testOptionalIdiomaticUsage() {
        var admin = new OptionalIdiomaticUsage.User("u1", "juan_dev", true, "ADMIN");
        var regular = new OptionalIdiomaticUsage.User("u2", "carlos_user", true, "USER");
        var inactiveAdmin = new OptionalIdiomaticUsage.User("u3", "ana_admin", false, "ADMIN");

        var adminBadge = OptionalIdiomaticUsage.getAdminBadge(Optional.of(admin));
        var regularBadge = OptionalIdiomaticUsage.getAdminBadge(Optional.of(regular));
        var inactiveBadge = OptionalIdiomaticUsage.getAdminBadge(Optional.of(inactiveAdmin));
        var emptyBadge = OptionalIdiomaticUsage.getAdminBadge(Optional.empty());

        assertThat(adminBadge).isPresent();
        assertThat(adminBadge.get().badgeLabel()).isEqualTo("VIP_ADMIN_SHIELD");

        assertThat(regularBadge).isEmpty();
        assertThat(inactiveBadge).isEmpty();
        assertThat(emptyBadge).isEmpty();

        // Fallback
        assertThat(OptionalIdiomaticUsage.resolveUserRole(Optional.of(regular), "GUEST")).isEqualTo("USER");
        assertThat(OptionalIdiomaticUsage.resolveUserRole(Optional.empty(), "GUEST")).isEqualTo("GUEST");
    }

    @Test
    @DisplayName("Debe evidenciar la trampa de rendimiento en orElse vs la evaluación lazy de orElseGet")
    void testOrElseVsOrElseGetPitfall() {
        var demonstrator1 = new OptionalInterviewTraps.TrapDemonstrator();
        var demonstrator2 = new OptionalInterviewTraps.TrapDemonstrator();

        var presentOptional = Optional.of("VALOR_PRESENTE");

        // 1. orElse: A pesar de que el valor está presente, el método costoso SE EJECUTÓ!
        String res1 = demonstrator1.resolveWithOrElse(presentOptional);
        assertThat(res1).isEqualTo("VALOR_PRESENTE");
        assertThat(demonstrator1.getExpensiveCallsCount())
                .as("orElse evalúa de inmediato sus argumentos incluso si el Optional está lleno!")
                .isEqualTo(1);

        // 2. orElseGet: Como el valor está presente, el Supplier NO se invoca!
        String res2 = demonstrator2.resolveWithOrElseGet(presentOptional);
        assertThat(res2).isEqualTo("VALOR_PRESENTE");
        assertThat(demonstrator2.getExpensiveCallsCount())
                .as("orElseGet evalúa de forma perezosa y no llama al servicio costoso!")
                .isZero();
    }

    @Test
    @DisplayName("Debe arrojar NoSuchElementException si se usa orElseThrow en empty")
    void testOrElseThrow() {
        Optional<String> empty = Optional.empty();

        assertThatThrownBy(empty::orElseThrow)
                .isInstanceOf(NoSuchElementException.class);
    }
}
