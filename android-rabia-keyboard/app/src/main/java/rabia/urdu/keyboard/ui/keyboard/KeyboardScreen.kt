package rabia.urdu.keyboard.ui.keyboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun KeyboardScreen(
    onKeyPress: (String) -> Unit,
    onShift: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSpace: () -> Unit = {},
    onEnter: () -> Unit = {},
    onSwitchSymbols: () -> Unit = {},
    onSwitchLanguage: () -> Unit = {},
    onBrowser: () -> Unit = {},
    onSettings: () -> Unit = {},
    onAlternate: (String) -> Unit = onKeyPress,
    isShifted: Boolean = false,
    mode: KeyboardMode = KeyboardMode.URDU
) {
    var localMode by remember { mutableStateOf(mode) }
    var localShift by remember { mutableStateOf(isShifted) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color(0xFF111111))
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = localMode,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "modeSwitch"
        ) { currentMode ->
            val currentRows = KeyboardLayout.rowsFor(currentMode)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                currentRows.forEachIndexed { index, row ->
                    val rowWeight = row.keys.sumOf { it.widthWeight.toDouble() }.toFloat()
                    val targetWeight = 10f
                    val sideWeight = ((targetWeight - rowWeight) / 2f).coerceAtLeast(0f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                when (index) {
                                    currentRows.lastIndex -> 50.dp
                                    else -> 48.dp
                                }
                            )
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (sideWeight > 0f) {
                            Spacer(modifier = Modifier.weight(sideWeight))
                        }
                        row.keys.forEach { key ->
                            KeyboardKey(
                                key = key,
                                isShiftActive = localShift,
                                modifier = Modifier
                                    .weight(key.widthWeight)
                                    .fillMaxHeight(),
                                onPress = {
                                    when (key.actionType) {
                                        KeyActionType.CHARACTER,
                                        KeyActionType.PUNCTUATION -> {
                                            onKeyPress(key.typedLabel(localShift))
                                            if (localShift) localShift = false
                                        }

                                        KeyActionType.SHIFT -> {
                                            onShift()
                                            localShift = !localShift
                                        }

                                        KeyActionType.DELETE -> onDelete()
                                        KeyActionType.SPACE -> onSpace()
                                        KeyActionType.ENTER -> onEnter()
                                        KeyActionType.SYMBOLS -> {
                                            onSwitchSymbols()
                                            localMode = if (localMode == KeyboardMode.SYMBOLS) {
                                                KeyboardMode.ENGLISH
                                            } else {
                                                KeyboardMode.SYMBOLS
                                            }
                                            localShift = false
                                        }

                                        KeyActionType.LANGUAGE -> {
                                            onSwitchLanguage()
                                            localMode = when (key.label) {
                                                "ABC" -> KeyboardMode.ENGLISH
                                                "ابپ" -> KeyboardMode.URDU
                                                else -> if (localMode == KeyboardMode.URDU) KeyboardMode.ENGLISH else KeyboardMode.URDU
                                            }
                                            localShift = false
                                        }
                                    }
                                },
                                onLongPress = {
                                    if (key.actionType == KeyActionType.SPACE) onSettings()
                                },
                                onAlternateSelected = { ch -> onAlternate(ch) }
                            )
                        }
                        if (sideWeight > 0f) {
                            Spacer(modifier = Modifier.weight(sideWeight))
                        }
                    }
                    if (index != currentRows.lastIndex) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}
