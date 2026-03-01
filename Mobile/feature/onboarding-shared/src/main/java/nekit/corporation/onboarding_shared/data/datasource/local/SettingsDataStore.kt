package nekit.corporation.onboarding_shared.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.anvil.annotations.optional.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import nekit.corporation.common.AppScope
import nekit.corporation.onboarding_shared.data.datasource.local.model.Settings
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@SingleIn(AppScope::class)
class SettingsDataStore @Inject constructor(
    private val context: Context,
) : DataStore<Settings> {

    private val Context.dataStore by preferencesDataStore(name = DATA_STORE_NAME)

    @OptIn(ExperimentalEncodingApi::class)
    override val data: Flow<Settings> = context.dataStore.data.map { preferences ->
        val settingsString = preferences[settingKey]
        if (!settingsString.isNullOrEmpty()) {
            try {
                val decodedBytes = Base64.decode(settingsString)
                val jsonString = String(decodedBytes, Charsets.UTF_8)
                Json.decodeFromString<Settings>(jsonString)
            } catch (_: Exception) {
                settingCommon
            }
        } else {
            settingCommon
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun updateData(transform: suspend (Settings) -> Settings): Settings {
        return context.dataStore.updateData { preferences ->
            val currentSettings = preferences[settingKey]?.let { settingsString ->
                try {
                    val decodedBytes = Base64.decode(settingsString)
                    val jsonString = String(decodedBytes, Charsets.UTF_8)
                    Json.decodeFromString<Settings>(jsonString)
                } catch (_: Exception) {
                    settingCommon
                }
            } ?: settingCommon

            val newSettings = transform(currentSettings)

            val jsonString = Json.encodeToString(Settings.serializer(), newSettings)
            val encodedString = Base64.encode(jsonString.toByteArray(Charsets.UTF_8))

            preferences.toMutablePreferences().apply {
                set(settingKey, encodedString)
            }.toPreferences()
        }.let { updatedPreferences ->
            updatedPreferences[settingKey]?.let { settingsString ->
                try {
                    val decodedBytes = Base64.decode(settingsString)
                    val jsonString = String(decodedBytes, Charsets.UTF_8)
                    Json.decodeFromString<Settings>(jsonString)
                } catch (_: Exception) {
                    settingCommon
                }
            } ?: settingCommon
        }
    }

    private val settingKey = stringPreferencesKey(SETTING_KEY)

    private companion object {
        const val SETTING_KEY = "settings_key"
        const val DATA_STORE_NAME = "settings_data_store"

        val settingCommon = Settings(isShowedOnboarding = false)
    }
}