package nekit.corporation.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import nekit.corporation.data.remote.model.MockEntity


@Database(
    entities = [MockEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

}