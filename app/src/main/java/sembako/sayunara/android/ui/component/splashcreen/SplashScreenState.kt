package sembako.sayunara.android.ui.component.splashcreen
import sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup

sealed class SplashScreenState {
    object Requesting : SplashScreenState()
    data class OnSuccess(val configSetup: ConfigSetup) : SplashScreenState()
    data class OnFailed(val message: Int): SplashScreenState()

}