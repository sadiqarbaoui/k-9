package app.k9mail.feature.account.setup.ui.autodiscovery

import app.k9mail.core.ui.compose.testing.ComposeTest
import app.k9mail.core.ui.compose.testing.setContent
import app.k9mail.core.ui.compose.theme.ThunderbirdTheme
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.Effect
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.State
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AccountAutoDiscoveryScreenKtTest : ComposeTest() {

    @Test
    fun `should delegate navigation effects`() = runTest {
        val initialState = State()
        val viewModel = FakeAccountAutoDiscoveryViewModel(initialState)
        var onNextCounter = 0
        var onBackCounter = 0
        var onOAuthCounter = 0

        setContent {
            ThunderbirdTheme {
                AccountAutoDiscoveryScreen(
                    onNext = { _, _ -> onNextCounter++ },
                    onBack = { onBackCounter++ },
                    onOAuth = { _, _ -> onOAuthCounter++ },
                    viewModel = viewModel,
                )
            }
        }

        assertThat(onNextCounter).isEqualTo(0)
        assertThat(onBackCounter).isEqualTo(0)
        assertThat(onOAuthCounter).isEqualTo(0)

        viewModel.effect(Effect.NavigateNext(isAutomaticConfig = false))

        assertThat(onNextCounter).isEqualTo(1)
        assertThat(onBackCounter).isEqualTo(0)
        assertThat(onOAuthCounter).isEqualTo(0)

        viewModel.effect(Effect.NavigateBack)

        assertThat(onNextCounter).isEqualTo(1)
        assertThat(onBackCounter).isEqualTo(1)
        assertThat(onOAuthCounter).isEqualTo(0)

        viewModel.effect(Effect.NavigateOAuth("hostname", "emailAddress"))

        assertThat(onNextCounter).isEqualTo(1)
        assertThat(onBackCounter).isEqualTo(1)
        assertThat(onOAuthCounter).isEqualTo(1)
    }
}
