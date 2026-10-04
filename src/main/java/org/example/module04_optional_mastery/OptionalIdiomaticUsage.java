package org.example.module04_optional_mastery;

import java.util.Optional;

/**
 * MÓDULO 04 - USO IDIOMÁTICO DE OPTIONAL (Pensamiento Monádico)
 * -----------------------------------------------------------------------------
 * En Java, Optional<T> es una mónada de presencia/ausencia de valor.
 * Su propósito central es evitar el temido NullPointerException (NPE) actuando como
 * TIPO DE RETORNO en métodos donde el resultado puede legítimamente no existir.
 *
 * ANTI-PATRÓN DE NOVATO:
 * Usar Optional como si fuera un 'if (x != null)':
 *   if (optionalUser.isPresent()) {
 *       User user = optionalUser.get();
 *       // ...
 *   }
 * ¡Esto no aprovecha la programación funcional!
 *
 * PATRÓN IDIOMÁTICO:
 * Pensamiento funcional monádico encadenando:
 * - map()             : Transforma el valor si existe.
 * - flatMap()         : Desanida si la función retorna otro Optional.
 * - filter()          : Descarta el valor si no cumple una condición.
 * - ifPresentOrElse() : Ejecuta una acción si existe, u otra alternativa si está vacío.
 */
public class OptionalIdiomaticUsage {

    public record User(String id, String username, boolean active, String role) {}
    public record UserBadgeDto(String username, String badgeLabel) {}

    /**
     * Estilo Funcional Limpio:
     * Si el usuario existe, está activo y tiene rol ADMIN, retornamos su DTO Badge.
     * De lo contrario, retornamos Optional.empty() de forma transparente.
     */
    public static Optional<UserBadgeDto> getAdminBadge(Optional<User> optionalUser) {
        return optionalUser
                .filter(User::active)                       // Solo si está activo
                .filter(u -> "ADMIN".equalsIgnoreCase(u.role())) // Solo si es admin
                .map(u -> new UserBadgeDto(u.username(), "VIP_ADMIN_SHIELD")); // Proyección a DTO
    }

    /**
     * Alternativas de valor por defecto:
     * Retorna el rol del usuario, o un valor fallback por defecto si el usuario es nulo o vacío.
     */
    public static String resolveUserRole(Optional<User> optionalUser, String fallbackRole) {
        return optionalUser
                .map(User::role)
                .map(String::toUpperCase)
                .orElse(fallbackRole);
    }
}
