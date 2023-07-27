package app.k9mail.feature.account.setup.ui

import app.k9mail.core.ui.compose.testing.ComposeTest
import app.k9mail.core.ui.compose.testing.onNodeWithTag
import app.k9mail.core.ui.compose.testing.setContent
import app.k9mail.core.ui.compose.theme.ThunderbirdTheme
import app.k9mail.feature.account.setup.ui.AccountSetupContract.Effect
import app.k9mail.feature.account.setup.ui.AccountSetupContract.SetupStep
import app.k9mail.feature.account.setup.ui.AccountSetupContract.State
import app.k9mail.feature.account.setup.ui.autodiscovery.FakeAccountAutoDiscoveryViewModel
import app.k9mail.feature.account.setup.ui.incoming.FakeAccountIncomingConfigViewModel
import app.k9mail.feature.account.setup.ui.options.FakeAccountOptionsViewModel
import app.k9mail.feature.account.setup.ui.outgoing.FakeAccountOutgoingConfigViewModel
import app.k9mail.feature.account.setup.ui.validation.FakeAccountValidationViewModel
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AccountSetupScreenKtTest : ComposeTest() {

    @Test
    fun `should display correct screen for every setup step`() = runTest {
        val viewModel = FakeAccountSetupViewModel()
        val autoDiscoveryViewModel = FakeAccountAutoDiscoveryViewModel()
        val oAuthViewModel = FakeAccountOAuthViewModel()
        val incomingViewModel = FakeAccountIncomingConfigViewModel()
        val incomingValidationViewModel = FakeAccountValidationViewModel()
        val outgoingViewModel = FakeAccountOutgoingConfigViewModel()
        val outgoingValidationViewModel = FakeAccountValidationViewModel()
        val optionsViewModel = FakeAccountOptionsViewModel()

        setContent {
            ThunderbirdTheme {
                AccountSetupScreen(
                    onFinish = { },
                    onBack = { },
                    viewModel = viewModel,
                    autoDiscoveryViewModel = autoDiscoveryViewModel,
                    oAuthViewModel = oAuthViewModel,
                    incomingViewModel = incomingViewModel,
                    incomingValidationViewModel = incomingValidationViewModel,
                    outgoingViewModel = outgoingViewModel,
                    outgoingValidationViewModel = outgoingValidationViewModel,
                    optionsViewModel = optionsViewModel,
                )
            }
        }

        for (step in SetupStep.values()) {
            viewModel.state { it.copy(setupStep = step) }
            onNodeWithTag(getTagForStep(step)).assertExists()
        }
    }

    @Test
    fun `should delegate navigation effects`() = runTest {
        val initialState = State()
        val viewModel = FakeAccountSetupViewModel(initialState)
        val autoDiscoveryViewModel = FakeAccountAutoDiscoveryViewModel()
        val oAuthViewModel = FakeAccountOAuthViewModel()
        val incomingViewModel = FakeAccountIncomingConfigViewModel()
        val incomingValidationViewModel = FakeAccountValidationViewModel()
        val outgoingViewModel = FakeAccountOutgoingConfigViewModel()
        val outgoingValidationViewModel = FakeAccountValidationViewModel()
        val optionsViewModel = FakeAccountOptionsViewModel()
        var onFinishCounter = 0
        var onBackCounter = 0

        setContent {
            ThunderbirdTheme {
                AccountSetupScreen(
                    onFinish = { onFinishCounter++ },
                    onBack = { onBackCounter++ },
                    viewModel = viewModel,
                    autoDiscoveryViewModel = autoDiscoveryViewModel,
                    oAuthViewModel = oAuthViewModel,
                    incomingViewModel = incomingViewModel,
                    incomingValidationViewModel = incomingValidationViewModel,
                    outgoingViewModel = outgoingViewModel,
                    outgoingValidationViewModel = outgoingValidationViewModel,
                    optionsViewModel = optionsViewModel,
                )
            }
        }

        assertThat(onFinishCounter).isEqualTo(0)
        assertThat(onBackCounter).isEqualTo(0)

        viewModel.effect(Effect.NavigateNext("accountUuid"))

        assertThat(onFinishCounter).isEqualTo(1)
        assertThat(onBackCounter).isEqualTo(0)

        viewModel.effect(Effect.NavigateBack)

        assertThat(onFinishCounter).isEqualTo(1)
        assertThat(onBackCounter).isEqualTo(1)
    }

    private fun getTagForStep(step: SetupStep): String = when (step) {
        SetupStep.AUTO_CONFIG -> "AccountAutoDiscoveryContent"
        SetupStep.INCOMING_CONFIG -> "AccountIncomingConfigContent"
        SetupStep.INCOMING_VALIDATION -> "AccountValidationContent"
        SetupStep.OUTGOING_CONFIG -> "AccountOutgoingConfigContent"
        SetupStep.OUTGOING_VALIDATION -> "AccountValidationContent"
        SetupStep.OPTIONS -> "AccountOptionsContent"
    }
}
