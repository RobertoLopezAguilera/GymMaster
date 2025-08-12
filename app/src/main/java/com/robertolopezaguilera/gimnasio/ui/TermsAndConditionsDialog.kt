package com.robertolopezaguilera.gimnasio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.robertolopezaguilera.gimnasio.R
import com.robertolopezaguilera.gimnasio.ui.theme.*

@Composable
fun TermsAndConditionsDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { /* No permitir cerrar sin aceptar/rechazar */ },
        title = {
            Text(
                text = stringResource(R.string.terms_and_conditions_title),
                style = MaterialTheme.typography.headlineSmall,
                color = GymDarkBlue
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(
                    text = HtmlCompat.fromHtml(
                        stringResource(R.string.terms_and_conditions_content),
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    ).toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymDarkBlue,
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.accept_terms))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDecline,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = GymDarkBlue
                )
            ) {
                Text(stringResource(R.string.decline_terms))
            }
        },
        modifier = modifier
    )
}