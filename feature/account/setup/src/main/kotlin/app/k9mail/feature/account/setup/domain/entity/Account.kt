package app.k9mail.feature.account.setup.domain.entity

import app.k9mail.feature.account.oauth.domain.entity.AuthorizationState
import com.fsck.k9.mail.ServerSettings

data class Account(
    val emailAddress: String,
    val incomingServerSettings: ServerSettings,
    val outgoingServerSettings: ServerSettings,
    val authorizationState: AuthorizationState?,
    val options: AccountOptions,
)
