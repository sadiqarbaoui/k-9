package app.k9mail.feature.account.setup.ui

import app.k9mail.core.ui.compose.common.mvi.UnidirectionalViewModel
import app.k9mail.feature.account.oauth.domain.entity.OAuthResult
import app.k9mail.feature.account.oauth.ui.AccountOAuthContract
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract
import app.k9mail.feature.account.setup.ui.incoming.AccountIncomingConfigContract
import app.k9mail.feature.account.setup.ui.options.AccountOptionsContract
import app.k9mail.feature.account.setup.ui.outgoing.AccountOutgoingConfigContract

interface AccountSetupContract {

    enum class SetupStep {
        AUTO_CONFIG,
        INCOMING_CONFIG,
        INCOMING_VALIDATION,
        OUTGOING_CONFIG,
        OUTGOING_VALIDATION,
        OPTIONS,
    }

    interface ViewModel : UnidirectionalViewModel<State, Event, Effect>

    data class State(
        val setupStep: SetupStep = SetupStep.AUTO_CONFIG,
        val isAutomaticConfig: Boolean = false,
        val showOAuth: Boolean = false,
    )

    sealed interface Event {
        data class OnOAuth(
            val hostname: String,
            val emailAddress: String,
        ) : Event

        data class OnOAuthResult(
            val result: OAuthResult,
        ) : Event

        data class OnAutoDiscoveryFinished(
            val state: AccountAutoDiscoveryContract.State,
            val isAutomaticConfig: Boolean,
        ) : Event

        data class OnStateCollected(
            val autoDiscoveryState: AccountAutoDiscoveryContract.State,
            val incomingState: AccountIncomingConfigContract.State,
            val outgoingState: AccountOutgoingConfigContract.State,
            val optionsState: AccountOptionsContract.State,
        ) : Event

        object OnNext : Event
        object OnBack : Event
    }

    sealed interface Effect {

        data class UpdateOAuth(
            val state: AccountOAuthContract.State,
        ) : Effect

        data class UpdateIncomingConfig(
            val state: AccountIncomingConfigContract.State,
        ) : Effect

        object UpdateIncomingConfigValidation : Effect

        data class UpdateOutgoingConfig(
            val state: AccountOutgoingConfigContract.State,
        ) : Effect

        object UpdateOutgoingConfigValidation : Effect

        data class UpdateOptions(
            val state: AccountOptionsContract.State,
        ) : Effect

        object CollectExternalStates : Effect

        data class AutDiscoveryOAuthFinished(
            val result: OAuthResult,
        ) : Effect

        data class NavigateNext(
            val accountUuid: String,
        ) : Effect

        object NavigateBack : Effect
    }
}
