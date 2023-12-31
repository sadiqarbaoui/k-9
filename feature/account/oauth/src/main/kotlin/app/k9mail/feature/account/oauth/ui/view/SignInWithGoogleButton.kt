package app.k9mail.feature.account.oauth.ui.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.k9mail.core.ui.compose.common.DevicePreviews
import app.k9mail.core.ui.compose.theme.PreviewWithThemes
import app.k9mail.feature.account.oauth.R

/**
 * A sign in with Google button, following the Google Branding Guidelines.
 *
 * @see [Google Branding Guidelines](https://developers.google.com/identity/branding-guidelines)
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignInWithGoogleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = getSurfaceColor(MaterialTheme.colors.isLight),
        ),
        color = getSurfaceColor(MaterialTheme.colors.isLight),
        elevation = elevation?.elevation(true, interactionSource)?.value ?: 0.dp,
    ) {
        Row(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing,
                    ),
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = Color.White,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(18.dp),
                    painter = painterResource(
                        id = R.drawable.account_oauth_ic_google_logo,
                    ),
                    contentDescription = "Google logo",
                    tint = Color.Unspecified,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    id = if (isLoading) {
                        R.string.account_oauth_sign_in_with_google_button_loading
                    } else {
                        R.string.account_oauth_sign_in_with_google_button
                    },
                ),
                style = TextStyle(
                    color = getTextColor(MaterialTheme.colors.isLight),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = 1.25.sp,
                ),
            )
            if (isLoading) {
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colors.primary,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Suppress("MagicNumber")
private fun getSurfaceColor(isLight: Boolean): Color {
    return if (isLight) {
        Color(0xFFFFFFFF)
    } else {
        Color(0xFF4285F4)
    }
}

@Suppress("MagicNumber")
private fun getTextColor(isLight: Boolean): Color {
    return if (isLight) {
        Color(0x87000000)
    } else {
        Color(0xFFFFFFFF)
    }
}

@DevicePreviews
@Composable
internal fun SignInWithGoogleButtonPreview() {
    PreviewWithThemes {
        SignInWithGoogleButton(
            onClick = {},
        )
    }
}

@DevicePreviews
@Composable
internal fun SignInWithGoogleButtonLoadingPreview() {
    PreviewWithThemes {
        SignInWithGoogleButton(
            onClick = {},
            isLoading = true,
        )
    }
}
