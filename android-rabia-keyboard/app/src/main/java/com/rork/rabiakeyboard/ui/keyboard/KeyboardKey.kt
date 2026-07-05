package com.rork.rabiakeyboard.ui.keyboard

import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Single keyboard key with a classic 3D glossy Rabia-style look, press animation,
 * haptic feedback and long-press alternate characters.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun KeyboardKey(
    key: KeyDef,
    isShiftActive: Boolean,
    modifier: Modifier = Modifier,
    onPress: () -> Unit = {},
    onLongPress: () -> Unit = {},
    onAlternateSelected: (String) -> Unit = {}
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }
    var showAlternates by remember { mutableStateOf(false) }
    var longPressFired by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "keyPressScale"
    )

    val outerBevel = Brush.verticalGradient(
        colors = listOf(Color(0xFF888888), Color(0xFF555555), Color(0xFF2A2A2A))
    )
    val keyFace = Brush.verticalGradient(
        colors = if (isPressed) {
            listOf(Color(0xFF333333), Color(0xFF262626), Color(0xFF1A1A1A))
        } else {
            listOf(Color(0xFF5A5A5A), Color(0xFF3F3F3F), Color(0xFF2B2B2B))
        }
    )
    val textColor = Color(0xFFFFFFFF)
    val accentColor = Color(0xFF81D4FA)
    val dimmedColor = Color(0xFFAAAAAA)

    val supportsLongPress =
        (key.actionType == KeyActionType.CHARACTER && key.alternates.isNotEmpty()) ||
            key.actionType == KeyActionType.SPACE

    Box(
        modifier = modifier
            .padding(1.5.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Outer 3D bevel / border.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(outerBevel, shape = RoundedCornerShape(6.dp))
                .padding(start = 1.dp, top = 1.5.dp, end = 1.dp, bottom = 1.dp),
            contentAlignment = Alignment.Center
        ) {
            // Inner key face with touch handling.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(keyFace, shape = RoundedCornerShape(5.dp))
                    .pointerInteropFilter { motionEvent ->
                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> {
                                isPressed = true
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                longPressFired = false
                                true
                            }

                            MotionEvent.ACTION_UP -> {
                                if (!longPressFired) onPress()
                                isPressed = false
                                showAlternates = false
                                true
                            }

                            MotionEvent.ACTION_CANCEL -> {
                                isPressed = false
                                showAlternates = false
                                true
                            }

                            else -> false
                        }
                    }
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                // Top glossy highlight line.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                        )
                )

                // Long-press trigger.
                if (supportsLongPress && isPressed) {
                    LaunchedEffect(Unit) {
                        delay(450)
                        if (isPressed && !longPressFired) {
                            longPressFired = true
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            if (key.actionType == KeyActionType.SPACE) {
                                onLongPress()
                            } else if (key.alternates.isNotEmpty()) {
                                showAlternates = true
                            }
                        }
                    }
                }

                // Alternate characters popup.
                if (showAlternates && key.alternates.isNotEmpty()) {
                    AlternatesPopup(
                        alternates = key.alternates,
                        onSelected = { ch ->
                            onAlternateSelected(ch)
                            showAlternates = false
                            isPressed = false
                        },
                        onDismiss = {
                            showAlternates = false
                            isPressed = false
                        }
                    )
                }

                KeyContent(
                    key = key,
                    isShiftActive = isShiftActive,
                    textColor = textColor,
                    accentColor = accentColor,
                    dimmedColor = dimmedColor
                )
            }
        }
    }
}

@Composable
private fun KeyContent(
    key: KeyDef,
    isShiftActive: Boolean,
    textColor: Color,
    accentColor: Color,
    dimmedColor: Color
) {
    when (key.actionType) {
        KeyActionType.SHIFT -> {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Shift",
                tint = if (isShiftActive) accentColor else textColor,
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }

        KeyActionType.DELETE -> {
            Icon(
                imageVector = Icons.Default.Backspace,
                contentDescription = "Delete",
                tint = textColor,
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }

        KeyActionType.SPACE -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = null,
                    tint = dimmedColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "V1.0.1",
                    color = dimmedColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        KeyActionType.ENTER -> {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                contentDescription = "Enter",
                tint = textColor,
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }

        KeyActionType.LANGUAGE -> {
            if (key.label.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Change language",
                    tint = textColor,
                    modifier = Modifier.fillMaxSize(0.5f)
                )
            } else {
                Text(
                    text = key.label,
                    color = textColor,
                    fontSize = if (key.label.length <= 3) 18.sp else 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        KeyActionType.SYMBOLS -> {
            Text(
                text = key.label,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        KeyActionType.PUNCTUATION,
        KeyActionType.CHARACTER -> {
            val display = if (isShiftActive && key.shiftedLabel.isNotEmpty()) {
                key.shiftedLabel
            } else {
                key.label
            }
            Text(
                text = display,
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Floating popup that shows alternate characters above the key.
 */
@Composable
private fun AlternatesPopup(
    alternates: List<String>,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = (-44).dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF2B2B2B), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            alternates.forEach { ch ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFF4A4A4A), RoundedCornerShape(4.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ch,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
