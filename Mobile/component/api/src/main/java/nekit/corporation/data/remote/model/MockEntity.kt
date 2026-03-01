package nekit.corporation.data.remote.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity("mock")
data class MockEntity(
    @PrimaryKey val mock: String,
)
