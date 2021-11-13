package sembako.sayunara.android.ui.component.splashcreen
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigApp

sealed class SplashScreenState {
    object Requesting : SplashScreenState()
    data class OnSuccess(val configApp: ConfigApp) : SplashScreenState()
    data class OnFailed(val message: Int): SplashScreenState()

}