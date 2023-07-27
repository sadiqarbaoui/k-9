package app.k9mail.feature.account.setup.ui.autodiscovery

import app.k9mail.autodiscovery.api.AutoDiscoveryResult
import app.k9mail.core.common.domain.usecase.validation.ValidationResult
import app.k9mail.core.ui.compose.common.mvi.UnidirectionalViewModel
import app.k9mail.feature.account.setup.domain.input.BooleanInputField
import app.k9mail.feature.account.setup.domain.input.StringInputField

interface AccountAutoDiscoveryContract {

    enum class ConfigStep {
        EMAIL_ADDRESS,
        OAUTH,
        PASSWORD,
    }

    interface ViewModel : UnidirectionalViewModel<State, Event, Effect> {
        fun initState(state: State)
    }

    data class State(
        val configStep: ConfigStep = ConfigStep.EMAIL_ADDRESS,
        val emailAddress: StringInputField = StringInputField(),
        val password: StringInputField = StringInputField(),
        val autoDiscoverySettings: AutoDiscoveryResult.Settings? = null,
        val configurationApproved: BooleanInputField = BooleanInputField(),

        val isSuccess: Boolean = false,
        val error: Error? = null,
        val isLoading: Boolean = false,
    )

    sealed class Event {
        data class EmailAddressChanged(val emailAddress: String) : Event()
        data class PasswordChanged(val password: String) : Event()
        data class ConfigurationApprovalChanged(val confirmed: Boolean) : Event()

        object OnOAuthFailed : Event()
        object OnNextClicked : Event()
        object OnBackClicked : Event()
        object OnRetryClicked : Event()
        object OnEditConfigurationClicked : Event()
    }

    sealed class Effect {
        data class NavigateNext(
            val isAutomaticConfig: Boolean,
        ) : Effect()
        data class NavigateOAuth(
            val hostname: String,
            val emailAddress: String,
        ) : Effect()
        object NavigateBack : Effect()
    }

    interface Validator {
        fun validateEmailAddress(emailAddress: String): ValidationResult
        fun validatePassword(password: String): ValidationResult
        fun validateConfigurationApproval(isApproved: Boolean?, isAutoDiscoveryTrusted: Boolean?): ValidationResult
    }

    sealed interface Error {
        object NetworkError : Error
        object UnknownError : Error
    }
}
