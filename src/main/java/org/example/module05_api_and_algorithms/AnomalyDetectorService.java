package org.example.module05_api_and_algorithms;

import org.example.module05_api_and_algorithms.model.DomainModels.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ESCENARIO 1: ENDPOINT ALGORÍTMICO DE DETECCIÓN DE ANOMALÍAS EN TRANSACCIONES
 * -----------------------------------------------------------------------------
 * En una API bancaria o fintech, se requiere un servicio para analizar lotes
 * de transacciones y alertar sobre transacciones sospechosas (anomalías estadísticas)
 * basadas en el comportamiento histórico de cada cuenta (Z-Score > 2.0 desviaciones estándar).
 *
 * Flujo declarativo implementado con Streams:
 * 1. Filtrar transacciones válidas (APPROVED).
 * 2. Agrupar transacciones por cuenta (groupingBy).
 * 3. Calcular la media y desviación estándar de cada cuenta de forma funcional.
 * 4. Filtrar y proyectar aquellas que superan el umbral a AnomalyAlert.
 * 5. Consolidar en un reporte DTO inmutable.
 */
public class AnomalyDetectorService {

    public static final double DEFAULT_Z_SCORE_THRESHOLD = 1.5;

    public List<AccountAnomalySummary> detectAnomalies(List<Transaction> transactions) {
        return detectAnomalies(transactions, DEFAULT_Z_SCORE_THRESHOLD);
    }

    public List<AccountAnomalySummary> detectAnomalies(List<Transaction> transactions, double zScoreThreshold) {
        if (transactions == null || transactions.isEmpty()) {
            return List.of();
        }

        // 1. Filtrado inicial y agrupación por cuenta
        Map<String, List<Transaction>> transactionsByAccount = transactions.stream()
                .filter(tx -> tx.status() == TransactionStatus.APPROVED)
                .collect(Collectors.groupingBy(Transaction::accountId));

        // 2. Procesamiento algorítmico funcional por cuenta
        return transactionsByAccount.entrySet().stream()
                .map(entry -> analyzeAccountTransactions(entry.getKey(), entry.getValue(), zScoreThreshold))
                .filter(summary -> !summary.anomalies().isEmpty()) // Solo cuentas con anomalías
                .toList();
    }

    private AccountAnomalySummary analyzeAccountTransactions(String accountId, List<Transaction> txs, double zScoreThreshold) {
        int count = txs.size();
        if (count < 2) {
            // Muestra insuficiente para calcular desviación estándar confiable
            return new AccountAnomalySummary(accountId, count, count == 1 ? txs.getFirst().amount() : 0.0, List.of());
        }

        // Media aritmética mediante IntStream/DoubleStream
        double mean = txs.stream()
                .mapToDouble(Transaction::amount)
                .average()
                .orElse(0.0);

        // Varianza y Desviación Estándar funcional: sum((x - mean)^2) / (N - 1)
        double variance = txs.stream()
                .mapToDouble(Transaction::amount)
                .map(amount -> Math.pow(amount - mean, 2))
                .sum() / (count - 1);

        double stdDev = Math.sqrt(variance);

        // Si la desviación estándar es 0 (todas las transacciones son del mismo monto), no hay anomalías
        if (stdDev == 0.0) {
            return new AccountAnomalySummary(accountId, count, mean, List.of());
        }

        // Detección de outliers (Z-Score > 2.0)
        List<AnomalyAlert> alerts = txs.stream()
                .map(tx -> {
                    double zScore = (tx.amount() - mean) / stdDev;
                    return new AnomalyAuditCandidate(tx, zScore);
                })
                .filter(candidate -> candidate.zScore() > zScoreThreshold)
                .map(candidate -> new AnomalyAlert(
                        candidate.tx().id(),
                        accountId,
                        candidate.tx().amount(),
                        mean,
                        Math.round(candidate.zScore() * 100.0) / 100.0,
                        "Monto supera significativamente la media de la cuenta (Z-Score: " +
                                String.format("%.2f", candidate.zScore()) + ")"
                ))
                .toList();

        return new AccountAnomalySummary(accountId, count, Math.round(mean * 100.0) / 100.0, alerts);
    }

    private record AnomalyAuditCandidate(Transaction tx, double zScore) {}
}
