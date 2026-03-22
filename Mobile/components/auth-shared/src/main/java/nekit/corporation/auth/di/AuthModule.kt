package nekit.corporation.auth.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.internal.RegistryConfiguration
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import nekit.corporation.auth.domain.Validator

@ContributesTo(AppScope::class)
interface AuthModule {

    @Provides
    fun provideCredentialManager(context: Context): CredentialManager =
        CredentialManager.create(context)

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
    fun provideValidator():Validator = Validator

    companion object {
        private const val KEYSET_NAME = "tink_keyset"
        private const val PREF_FILE = "tink_prefs"

        private const val MASTER_KEY_URI = "android-keystore://tink_master_key"
    }
}