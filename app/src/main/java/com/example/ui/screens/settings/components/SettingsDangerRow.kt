package com.example.ui.screens.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing

@Composable
fun SettingsDangerRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.DeleteForever,
    testTag: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        ),
        border = BorderStroke(MaterialTheme.dimens.cardBorderWidth, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.buttonHeight)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
