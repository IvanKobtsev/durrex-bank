package nekit.corporation.user.domain.usecase

import android.util.Log
import dev.zacsweers.metro.Inject
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.model.Settings

@Inject
class UpdateHiddenIdsUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(added: List<Int>, removed: List<Int>): Settings {
        Log.d("RAG", "added: $added,removed: $removed")
        return userRepository.updateHidden(added, removed)
    }
}