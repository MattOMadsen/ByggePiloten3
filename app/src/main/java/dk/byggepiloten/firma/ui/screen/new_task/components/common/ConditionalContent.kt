// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/ConditionalContent.kt
// RETTET: AnimatedVisibility pakker nu content i en Column for at give ColumnScope
// Fade + vertical expand/shrink (300ms) – smooth animation
// Tilføjet alle nødvendige imports
// Commit: Fix "p1" parameter fejl i ConditionalContent – fuldt fungerende med ColumnScope extension

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConditionalContent(
    visible: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + expandVertically(
            animationSpec = tween(300),
            expandFrom = Alignment.Top
        ),
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(
            animationSpec = tween(300),
            shrinkTowards = Alignment.Top
        )
    ) {
        Column {
            Spacer(modifier = Modifier.height(32.dp))
            this.content()  // this = ColumnScope → korrekt kald af extension lambda
        }
    }
}