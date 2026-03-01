package nekit.corporation.auth.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetManager
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.internal.RegistryConfiguration
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import nekit.corporation.auth.domain.Validator
import nekit.corporation.common.AppScope

@ContributesTo(AppScope::class)
@Module
object AuthModule {
    private const val KEYSET_NAME = "tink_keyset"
    private const val PREF_FILE = "tink_prefs"

    private const val MASTER_KEY_URI = "android-keystore://tink_master_key"

    @Provides
    fun provideCredentialManager(context: Context): CredentialManager = CredentialManager.create(context)

    @Provides
    fun provideAndroidKeysetManager(context: Context): AndroidKeysetManager =
        AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE)
            .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()

    @Provides
    fun provideAead(keysetManager: AndroidKeysetManager): Aead =
        keysetManager.keysetHandle.getPrimitive(
            RegistryConfiguration.get(),
            Aead::class.java
        )

    @Provides
    fun provideValidator() = Validator
}