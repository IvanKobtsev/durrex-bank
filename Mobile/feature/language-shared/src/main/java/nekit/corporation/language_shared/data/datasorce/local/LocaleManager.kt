import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleManager {
    
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    
    fun setLocale(context: Context, languageCode: String): Context {
        persistLanguage(context, languageCode)
        return updateResources(context, languageCode)
    }
    
    private fun persistLanguage(context: Context, languageCode: String) {
        val preferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        preferences.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
    }
    
    fun getPersistedLanguage(context: Context): String {
        val preferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return preferences.getString(SELECTED_LANGUAGE, "ru") ?: "ru"
    }
    
    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }
    
    fun getLocalizedContext(context: Context): Context {
        val language = getPersistedLanguage(context)
        return updateResources(context, language)
    }
}