package com.tchoutzine.tchoedgezine.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ConsultationType { HEALTH, AGRICULTURE }

@Entity(tableName = "consultations")
data class Consultation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: ConsultationType,
    val diagnosisName: String,
    val diagnosisDetail: String,
    val confidence: Int,           // 0-100
    val severity: String,          // "low" | "moderate" | "high"
    val recommendations: String,   // JSON string list
    val timestampMs: Long = System.currentTimeMillis(),
    val synced: Boolean = false,
)

data class DiagnosisResult(
    val name: String,
    val detail: String,
    val confidence: Int,
    val severity: String,
    val recommendations: List<String>,
    val usedOnlineModel: Boolean = false,
)
