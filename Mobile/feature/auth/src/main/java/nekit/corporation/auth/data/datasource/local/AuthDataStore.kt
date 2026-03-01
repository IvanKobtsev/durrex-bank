package nekit.corporation.auth.data.datasource.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.squareup.anvil.annotations.optional.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import nekit.corporation.auth.data.datasource.local.model.TokenLocal
import nekit.corporation.common.AppScope
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.also
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@SingleIn(AppScope::class)
class AuthDataStore @Inject constructor(
    private val context: Context,
    private val aead: Aead
) : DataStore<TokenLocal> {
    private val Context.dataStore by preferencesDataStore(name = DATA_STORE_NAME)

    @OptIn(ExperimentalEncodingApi::class)
    override val data: Flow<TokenLocal> = context.dataStore.data.map { preferences ->
        val token = preferences[token]?.let {
            val ciphertext = Base64.decode(it)
            aead.decrypt(ciphertext, null).toString(Charsets.UTF_8)
        }
        val date = preferences[dateKey]?.let {
            val ciphertext = Base64.decode(it)
            aead.decrypt(ciphertext, null).toString(Charsets.UTF_8)
        }


        TokenLocal(token, date)
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun updateData(transform: suspend (TokenLocal) -> TokenLocal): TokenLocal {
        var result: TokenLocal? = null
        return try {
            context.dataStore.updateData { preferences ->
                val currentToken = try {
                    preferences[token]?.let { encryptedToken ->
                        val ciphertext = Base64.decode(encryptedToken)
                        val decrypted = aead.decrypt(ciphertext, null)
                        String(decrypted, Charsets.UTF_8)
                    } ?: ""
                } catch (_: Exception) {
                    ""
                }

                val currentDate = try {
                    preferences[dateKey]?.let { encryptedToken ->
                        val ciphertext = Base64.decode(encryptedToken)
                        val decrypted = aead.decrypt(ciphertext, null)
                        String(decrypted, Charsets.UTF_8)
                    } ?: ""
                } catch (_: Exception) {
                    ""
                }

                val newTokenLocal = transform(TokenLocal(currentToken, currentDate))
                result = newTokenLocal

                val ciphertext = aead.encrypt(
                    newTokenLocal.token?.toByteArray(Charsets.UTF_8),
                    null
                )
                val encoded = Base64.encode(ciphertext)

                preferences.toPreferences().toMutablePreferences().apply {
                    set(token, encoded)
                }.toPreferences()
            }
            TokenLocal(result?.token, result?.expiresAt)
        } catch (e: Exception) {
            throw e
        }
    }

    private val token = stringPreferencesKey(TOKEN_KEY)
    private val dateKey = stringPreferencesKey(DATE_KEY)

    private companion object {
        const val TOKEN_KEY = "token_key"
        const val DATE_KEY = "date_key"
        const val DATA_STORE_NAME = "auth_data_store"
    }
}
