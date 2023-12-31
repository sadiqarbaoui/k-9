package app.k9mail.feature.account.setup.ui.outgoing

import app.k9mail.feature.account.setup.domain.entity.toAuthType
import app.k9mail.feature.account.setup.domain.entity.toMailConnectionSecurity
import app.k9mail.feature.account.setup.ui.validation.AccountValidationContract
import com.fsck.k9.mail.ServerSettings

internal fun AccountOutgoingConfigContract.State.toServerSettings(): ServerSettings {
    return ServerSettings(
        type = "smtp",
        host = server.value,
        port = port.value!!.toInt(),
        connectionSecurity = security.toMailConnectionSecurity(),
        authenticationType = authenticationType.toAuthType(),
        username = if (authenticationType.isUsernameRequired) username.value else "",
        password = if (authenticationType.isPasswordRequired) password.value else null,
        clientCertificateAlias = clientCertificateAlias,
    )
}

internal fun AccountOutgoingConfigContract.State.toValidationState(): AccountValidationContract.State {
    return AccountValidationContract.State(
        serverSettings = toServerSettings(),
        // TODO add authorization state
    )
}
