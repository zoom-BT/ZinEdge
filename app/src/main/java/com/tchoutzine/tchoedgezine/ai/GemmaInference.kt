package com.tchoutzine.tchoedgezine.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.tchoutzine.tchoedgezine.data.model.DiagnosisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

class GemmaInference(private val context: Context) {

    private var llm: LlmInference? = null
    private val initMutex = Mutex()

    var isReady = false
        private set

    companion object {
        const val MODEL_FILENAME     = "gemma-3n-E4B-it-int4.litertlm"
        const val MODEL_FILENAME_E2B = "gemma-3n-E2B-it-int4.litertlm"

        @Volatile private var _instance: GemmaInference? = null

        fun getInstance(context: Context): GemmaInference =
            _instance ?: synchronized(this) {
                _instance ?: GemmaInference(context.applicationContext).also { _instance = it }
            }
    }

    suspend fun prepareModel(
        onProgress: (stage: String, percent: Int) -> Unit,
    ): Boolean = withContext(Dispatchers.IO) {
        if (internalModelFile().exists()) {
            onProgress("Modèle prêt", 100)
            return@withContext true
        }

        val extFile = externalModelFile()
        if (extFile != null && extFile.exists()) {
            onProgress("Modèle trouvé (stockage externe)", 100)
            return@withContext true
        }

        val assetName = when {
            assetExists(MODEL_FILENAME)     -> MODEL_FILENAME
            assetExists(MODEL_FILENAME_E2B) -> MODEL_FILENAME_E2B
            else                            -> null
        }
        if (assetName != null) {
            val target = internalModelFile()
            target.parentFile?.mkdirs()
            onProgress("Copie depuis assets…", 0)
            val total = context.assets.openFd("models/$assetName").length
            return@withContext copyStream(
                input      = context.assets.open("models/$assetName"),
                output     = target,
                total      = total,
                onProgress = onProgress,
            )
        }

        onProgress("Modèle introuvable", -1)
        false
    }

    fun resolveModelFile(): File? {
        val internal = internalModelFile()
        if (internal.exists()) return internal
        val external = externalModelFile()
        if (external?.exists() == true) return external
        return null
    }

    fun externalModelDir(): File? =
        context.getExternalFilesDir(null)?.let { File(it, "models") }

    suspend fun initialize(
        onProgress: (stage: String, percent: Int) -> Unit,
        onReady: () -> Unit,
        onError: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        // Idempotent : si déjà prêt, court-circuite sans recharger
        val alreadyReady = initMutex.withLock {
            if (isReady && llm != null) true
            else { llm?.close(); llm = null; isReady = false; false }
        }
        if (alreadyReady) { onReady(); return@withContext }

        val modelFile = resolveModelFile()
        if (modelFile == null) { onError("Modèle introuvable"); return@withContext }

        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(1024)
                .setTopK(10)
                .setTemperature(0.1f)
                .build()

            onProgress("Chargement Gemma 3n E4B…", 0)
            val instance = LlmInference.createFromOptions(context, options)
            initMutex.withLock { llm = instance; isReady = true }
            onProgress("Modèle prêt", 100)
            onReady()
        } catch (e: Exception) {
            onError("Erreur chargement : ${e.message}")
        }
    }

    suspend fun diagnoseHealth(symptoms: List<String>, imageDescription: String? = null): DiagnosisResult =
        withContext(Dispatchers.IO) {
            if (!isReady) return@withContext mockHealthResult()
            val raw = try { llm!!.generateResponse(buildHealthPrompt(symptoms, imageDescription)) }
                      catch (e: Exception) { return@withContext mockHealthResult() }
            parseJsonResult(raw) ?: mockHealthResult()
        }

    suspend fun detectCrop(cropType: String, imageDescription: String? = null): DiagnosisResult =
        withContext(Dispatchers.IO) {
            if (!isReady) return@withContext mockCropResult()
            val raw = try { llm!!.generateResponse(buildCropPrompt(cropType, imageDescription)) }
                      catch (e: Exception) { return@withContext mockCropResult() }
            parseJsonResult(raw) ?: mockCropResult()
        }

    suspend fun chat(message: String): String = withContext(Dispatchers.IO) {
        if (!isReady) return@withContext "Modèle en cours de chargement…"
        try {
            llm!!.generateResponse(
                "<start_of_turn>user\n$message<end_of_turn>\n<start_of_turn>model\n"
            )
        } catch (e: Exception) { "Erreur : ${e.message}" }
    }

    fun release() {
        llm?.close()
        llm = null
        isReady = false
    }

    // ── Privé ─────────────────────────────────────────────────────────────
    private fun internalModelFile() = File(context.filesDir, "models/$MODEL_FILENAME")
    private fun externalModelFile() = externalModelDir()?.let { File(it, MODEL_FILENAME) }

    private fun assetExists(name: String) = try {
        context.assets.open("models/$name").close(); true
    } catch (_: Exception) { false }

    private fun copyStream(
        input: java.io.InputStream,
        output: File,
        total: Long,
        onProgress: (String, Int) -> Unit,
    ): Boolean = try {
        input.use { inp ->
            output.outputStream().use { out ->
                val buf = ByteArray(8 * 1024 * 1024)
                var copied = 0L; var read: Int
                while (inp.read(buf).also { read = it } != -1) {
                    out.write(buf, 0, read)
                    copied += read
                    val pct = if (total > 0) ((copied * 100) / total).toInt() else 0
                    onProgress("Copie… $pct%", pct)
                }
            }
        }
        onProgress("Copie terminée", 100)
        true
    } catch (e: Exception) {
        output.delete()
        onProgress("Erreur copie : ${e.message}", -1)
        false
    }

    private fun buildHealthPrompt(symptoms: List<String>, imageDesc: String?) =
        """<start_of_turn>user
[ZinEdge · MedGemma · AfriMed-QA · Afrique subsaharienne]
Symptômes : ${symptoms.joinToString(", ")}
${if (imageDesc != null) "Zone examinée : $imageDesc" else ""}
Fournir un diagnostic probable adapté au contexte africain.
Répondre UNIQUEMENT en JSON valide, sans texte avant ou après :
{"name":"...","detail":"...","confidence":94,"severity":"moderate","recommendations":["...","...","..."]}
<end_of_turn>
<start_of_turn>model
"""

    private fun buildCropPrompt(cropType: String, imageDesc: String?) =
        """<start_of_turn>user
[ZinEdge · Détection cultures · Afrique subsaharienne]
Culture : $cropType
${if (imageDesc != null) "Description image : $imageDesc" else ""}
Identifier la maladie et proposer un traitement accessible localement.
Répondre UNIQUEMENT en JSON valide, sans texte avant ou après :
{"name":"...","detail":"...","confidence":91,"severity":"moderate","recommendations":["...","...","..."]}
<end_of_turn>
<start_of_turn>model
"""

    // Parser JSON robuste avec suivi de profondeur (résiste aux accolades dans les valeurs)
    private fun extractJsonObject(raw: String): String? {
        val text = raw.trim()
        val start = text.indexOf('{')
        if (start < 0) return null
        var depth = 0
        var inString = false
        var escaped = false
        for (i in start until text.length) {
            val ch = text[i]
            if (escaped) { escaped = false; continue }
            if (ch == '\\') { escaped = true; continue }
            if (ch == '"') { inString = !inString; continue }
            if (!inString) {
                if (ch == '{') depth++
                if (ch == '}') { depth--; if (depth == 0) return text.substring(start, i + 1) }
            }
        }
        return null
    }

    private fun parseJsonResult(raw: String): DiagnosisResult? = try {
        val jsonText = extractJsonObject(raw) ?: return null
        val json = org.json.JSONObject(jsonText)
        DiagnosisResult(
            name            = json.getString("name"),
            detail          = json.getString("detail"),
            confidence      = json.getInt("confidence"),
            severity        = json.getString("severity"),
            recommendations = json.getJSONArray("recommendations").let { arr ->
                (0 until arr.length()).map { arr.getString(it) }
            },
            usedOnlineModel = true,  // indique un vrai résultat IA (pas un mock)
        )
    } catch (_: Exception) { null }

    private fun mockHealthResult() = DiagnosisResult(
        name = "Paludisme P. falciparum",
        detail = "Plasmodium falciparum · Diagnostic probable",
        confidence = 94, severity = "moderate",
        usedOnlineModel = false,
        recommendations = listOf(
            "Arteméther-luméfantrine 20/120 mg — 6 prises / 3 jours",
            "Test goutte épaisse au centre de santé le plus proche",
            "Hydratation, repos, antipyrétiques si T° > 38,5°C",
        ),
    )

    private fun mockCropResult() = DiagnosisResult(
        name = "Brûlure du maïs",
        detail = "Exserohilum turcicum · Northern Leaf Blight",
        confidence = 91, severity = "moderate",
        usedOnlineModel = false,
        recommendations = listOf(
            "Retirer et brûler les feuilles infectées immédiatement",
            "Fongicide Mancozeb 80% WP — 2 kg/ha · 2 applications à 10 j",
            "Rotation culturale recommandée saison suivante",
        ),
    )
}
