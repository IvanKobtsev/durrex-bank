package nekit.corporation.auth.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nekit.corporation.auth.data.datasource.local.model.TokenLocal
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Inject
@SingleIn(AppScope::class)
class AuthDataStore(
    context: Context,
    private val aead: Aead
) : DataStore<TokenLocal> {
    private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "auth_data_store"
    )

    private val dataStore = context.authDataStore

    @OptIn(ExperimentalEncodingApi::class)
    override val data: Flow<TokenLocal> = dataStore.data.map { prefs ->
        val token = prefs[tokenKey]?.let { encoded ->
            try {
                val ciphertext = Base64.decode(encoded)
                val decrypted = aead.decrypt(ciphertext, null)
                String(decrypted, Charsets.UTF_8)
            } catch (_: Exception) {
                ""
            }
        } ?: ""

        val date = prefs[dateKey]?.let { encoded ->
            try {
                val ciphertext = Base64.decode(encoded)
                val decrypted = aead.decrypt(ciphertext, null)
                String(decrypted, Charsets.UTF_8)
            } catch (_: Exception) {
                ""
            }
        } ?: ""

        TokenLocal(token.ifEmpty { null }, date.ifEmpty { null })
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun updateData(transform: suspend (TokenLocal) -> TokenLocal): TokenLocal {
        var newTokenLocal: TokenLocal? = null
        return dataStore.updateData { preferences ->
            // Чтение текущих значений
            val currentToken = try {
                preferences[tokenKey]?.let { encoded ->
                    String(aead.decrypt(Base64.decode(encoded, android.util.Base64.NO_WRAP), null), Charsets.UTF_8)
                }
            } catch (_: Exception) {
                null
            }

            val currentDate = try {
                preferences[dateKey]?.let { encoded ->
                    String(aead.decrypt(Base64.decode(encoded, android.util.Base64.NO_WRAP), null), Charsets.UTF_8)
                }
            } catch (_: Exception) {
                null
            }

            newTokenLocal = transform(TokenLocal(currentToken, currentDate))

            preferences.toMutablePreferences().apply {
                if (newTokenLocal.token.isNullOrEmpty()) {
                    remove(tokenKey)
                } else {
                    val encrypted = aead.encrypt(newTokenLocal.token!!.toByteArray(Charsets.UTF_8), null)
                    this[tokenKey] = android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP)
                }

                if (newTokenLocal.expiresAt.isNullOrEmpty()) {
                    remove(dateKey)
                } else {
                    val encrypted = aead.encrypt(newTokenLocal.expiresAt!!.toByteArray(Charsets.UTF_8), null)
                    this[dateKey] = android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP)
                }
            }
        }.let {
            newTokenLocal!!
        }
    }
    private val tokenKey = stringPreferencesKey(TOKEN_KEY)
    private val dateKey = stringPreferencesKey(DATE_KEY)

    private companion object {
        const val TOKEN_KEY = "token_key"
        const val DATE_KEY = "date_key"
    }
}