package nekit.corporation.data.local

import android.content.Context
import androidx.room.Room
import kotlin.jvm.java

internal object Database {
    fun build(context: Context): AppDataBase = Room.databaseBuilder(
        context,
        AppDataBase::class.java,
        "app_database"
    ).build()
}