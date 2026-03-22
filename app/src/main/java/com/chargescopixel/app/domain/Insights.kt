package com.chargescopixel.app.domain

data class InsightsSummary(
    val totalSessions: Int,
    val totalChargePercentGained: Int,
    val estimatedCycles: Double,
    val averageSessionGain: Double,
    val averageSessionDurationMinutes: Double,
    val hottestSessionTempC: Float
)
