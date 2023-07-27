package app.k9mail.feature.account.setup.domain.usecase

import app.k9mail.feature.account.setup.AccountSetupExternalContract.AccountCreator.AccountCreatorResult
import app.k9mail.feature.account.setup.domain.entity.Account
import app.k9mail.feature.account.setup.domain.entity.AccountOptions
import app.k9mail.feature.account.setup.domain.entity.MailConnectionSecurity
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fsck.k9.mail.AuthType
import com.fsck.k9.mail.ServerSettings
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CreateAccountTest {

    @Test
    fun `should successfully create account`() = runTest {
        var recordedAccount: Account? = null
        val createAccount = CreateAccount(
            accountCreator = { account ->
                recordedAccount = account
                AccountCreatorResult.Success(accountUuid = "uuid")
            },
        )

        val emailAddress = "user@example.com"
        val incomingServerSettings = ServerSettings(
            type = "imap",
            host = "imap.example.com",
            port = 993,
            connectionSecurity = MailConnectionSecurity.SSL_TLS_REQUIRED,
            authenticationType = AuthType.PLAIN,
            username = "user",
            password = "password",
            clientCertificateAlias = null,
        )
        val outgoingServerSettings = ServerSettings(
            type = "smtp",
            host = "smtp.example.com",
            port = 465,
            connectionSecurity = MailConnectionSecurity.SSL_TLS_REQUIRED,
            authenticationType = AuthType.PLAIN,
            username = "user",
            password = "password",
            clientCertificateAlias = null,
        )
        val options = AccountOptions(
            accountName = "accountName",
            displayName = "displayName",
            emailSignature = null,
            checkFrequencyInMinutes = 15,
            messageDisplayCount = 25,
            showNotification = true,
        )

        val result = createAccount.execute(emailAddress, incomingServerSettings, outgoingServerSettings, options)

        assertThat(result).isEqualTo("uuid")
        assertThat(recordedAccount).isEqualTo(
            Account(
                emailAddress = emailAddress,
                incomingServerSettings = incomingServerSettings,
                outgoingServerSettings = outgoingServerSettings,
                authorizationState = null, // TODO
                options = options,
            ),
        )
    }
}
