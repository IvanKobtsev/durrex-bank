package nekit.corporation.crash.data.local

import android.content.Context
import dev.zacsweers.metro.Inject
import kotlinx.serialization.json.Json
import nekit.corporation.crash.data.model.CrashLog
import java.io.File

@Inject
class CrashLogStore(
    private val context: Context,
    private val json: Json
) {

    fun append(log: CrashLog) {
        val file = File(context.filesDir, FILE_NAME)

        val line = json.encodeToString(log)

        file.appendText(line + "\n")
    }

    fun readAll(): List<CrashLog> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        return file.readLines().mapNotNull {
            runCatching { json.decodeFromString<CrashLog>(it) }.getOrNull()
        }
    }

    fun clear() {
        File(context.filesDir, FILE_NAME).writeText("")
    }

    private companion object {
        private const val FILE_NAME = "crashes.jsonl"
    }
}