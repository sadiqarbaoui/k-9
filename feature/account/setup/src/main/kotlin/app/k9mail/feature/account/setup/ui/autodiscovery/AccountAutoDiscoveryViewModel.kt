package app.k9mail.feature.account.setup.ui.autodiscovery

import androidx.lifecycle.viewModelScope
import app.k9mail.autodiscovery.api.AutoDiscoveryResult
import app.k9mail.autodiscovery.api.ImapServerSettings
import app.k9mail.core.common.domain.usecase.validation.ValidationResult
import app.k9mail.core.ui.compose.common.mvi.BaseViewModel
import app.k9mail.feature.account.setup.domain.DomainContract.UseCase
import app.k9mail.feature.account.setup.domain.entity.AutoDiscoveryAuthenticationType
import app.k9mail.feature.account.setup.domain.input.StringInputField
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.ConfigStep
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.Effect
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.Error
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.Event
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.State
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.Validator
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.ViewModel
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
internal class AccountAutoDiscoveryViewModel(
    initialState: State = State(),
    private val validator: Validator,
    private val getAutoDiscovery: UseCase.GetAutoDiscovery,
) : BaseViewModel<State, Event, Effect>(initialState), ViewModel {

    override fun initState(state: State) {
        updateState {
            state.copy()
        }
    }

    override fun event(event: Event) {
        when (event) {
            is Event.EmailAddressChanged -> changeEmailAddress(event.emailAddress)
            is Event.PasswordChanged -> changePassword(event.password)
            is Event.ConfigurationApprovalChanged -> changeConfigurationApproval(event.confirmed)

            Event.OnNextClicked -> onNext()
            Event.OnBackClicked -> onBack()
            Event.OnRetryClicked -> onRetry()
            Event.OnEditConfigurationClicked -> {
                navigateNext(isAutomaticConfig = false)
            }
            Event.OnOAuthFailed -> TODO()
        }
    }

    private fun changeEmailAddress(emailAddress: String) {
        updateState {
            State(
                emailAddress = StringInputField(value = emailAddress),
            )
        }
    }

    private fun changePassword(password: String) {
        updateState {
            it.copy(
                password = it.password.updateValue(password),
            )
        }
    }

    private fun changeConfigurationApproval(approved: Boolean) {
        updateState {
            it.copy(
                configurationApproved = it.configurationApproved.updateValue(approved),
            )
        }
    }

    private fun onNext() {
        when (state.value.configStep) {
            ConfigStep.EMAIL_ADDRESS ->
                if (state.value.error != null) {
                    updateState {
                        it.copy(
                            error = null,
                            configStep = ConfigStep.PASSWORD,
                        )
                    }
                } else {
                    submitEmail()
                }

            ConfigStep.PASSWORD -> submitPassword()
            ConfigStep.OAUTH -> navigateOAuth()
        }
    }

    private fun onRetry() {
        updateState {
            it.copy(error = null)
        }
        loadAutoDiscovery()
    }

    private fun submitEmail() {
        with(state.value) {
            val emailValidationResult = validator.validateEmailAddress(emailAddress.value)
            val hasError = emailValidationResult is ValidationResult.Failure

            updateState {
                it.copy(
                    emailAddress = it.emailAddress.updateFromValidationResult(emailValidationResult),
                )
            }

            if (!hasError) {
                loadAutoDiscovery()
            }
        }
    }

    private fun loadAutoDiscovery() {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isLoading = true,
                )
            }

            val result = getAutoDiscovery.execute(state.value.emailAddress.value)
            when (result) {
                AutoDiscoveryResult.NoUsableSettingsFound -> updateAutoDiscoverySettings(null)
                is AutoDiscoveryResult.Settings -> updateAutoDiscoverySettings(result)
                is AutoDiscoveryResult.NetworkError -> updateError(Error.NetworkError)
                is AutoDiscoveryResult.UnexpectedException -> updateError(Error.UnknownError)
            }
        }
    }

    private fun updateAutoDiscoverySettings(settings: AutoDiscoveryResult.Settings?) {
        val isOauth = (settings?.incomingServerSettings as? ImapServerSettings)
            ?.authenticationTypes?.contains(AutoDiscoveryAuthenticationType.OAuth2) ?: false
        updateState {
            it.copy(
                isLoading = false,
                autoDiscoverySettings = settings,
                configStep = if (isOauth) ConfigStep.OAUTH else ConfigStep.PASSWORD,
            )
        }

        if (isOauth) {
            navigateOAuth()
        }
    }

    private fun updateError(error: Error) {
        updateState {
            it.copy(
                isLoading = false,
                error = error,
            )
        }
    }

    private fun submitPassword() {
        with(state.value) {
            val emailValidationResult = validator.validateEmailAddress(emailAddress.value)
            val passwordValidationResult = validator.validatePassword(password.value)
            val configurationApprovalValidationResult = validator.validateConfigurationApproval(
                isApproved = configurationApproved.value,
                isAutoDiscoveryTrusted = autoDiscoverySettings?.isTrusted,
            )
            val hasError = listOf(
                emailValidationResult,
                passwordValidationResult,
                configurationApprovalValidationResult,
            ).any { it is ValidationResult.Failure }

            updateState {
                it.copy(
                    emailAddress = it.emailAddress.updateFromValidationResult(emailValidationResult),
                    password = it.password.updateFromValidationResult(passwordValidationResult),
                    configurationApproved = it.configurationApproved.updateFromValidationResult(
                        configurationApprovalValidationResult,
                    ),
                )
            }

            if (!hasError) {
                navigateNext(state.value.autoDiscoverySettings != null)
            }
        }
    }

    private fun onBack() {
        when (state.value.configStep) {
            ConfigStep.EMAIL_ADDRESS -> {
                if (state.value.error != null) {
                    updateState {
                        it.copy(error = null)
                    }
                } else {
                    navigateBack()
                }
            }

            ConfigStep.OAUTH,
            ConfigStep.PASSWORD,
            -> updateState {
                it.copy(
                    configStep = ConfigStep.EMAIL_ADDRESS,
                    password = StringInputField(),
                )
            }
        }
    }

    private fun onOAuthFailed() {
        updateState {
            it.copy(
                configStep = ConfigStep.PASSWORD,
            )
        }
    }

    private fun navigateBack() = emitEffect(Effect.NavigateBack)

    private fun navigateNext(isAutomaticConfig: Boolean) {
        emitEffect(Effect.NavigateNext(isAutomaticConfig))
    }

    private fun navigateOAuth() = emitEffect(
        Effect.NavigateOAuth(
            hostname = getIncomingHostname()!!,
            emailAddress = state.value.emailAddress.value,
        ),
    )

    private fun getIncomingHostname(): String? {
        val autoDiscovery = state.value.autoDiscoverySettings
        return if (autoDiscovery != null) {
            when (autoDiscovery.incomingServerSettings) {
                is ImapServerSettings -> {
                    val imapServerSettings = autoDiscovery.incomingServerSettings as ImapServerSettings
                    imapServerSettings.hostname.value
                }

                else -> {
                    null
                }
            }
        } else {
            null
        }
    }
}
