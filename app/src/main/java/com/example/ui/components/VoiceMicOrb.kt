package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MatrixRed
import com.example.ui.theme.MatrixRedDark
import com.example.ui.theme.MatrixRedGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun VoiceMicOrb(
    isListening: Boolean,
    onToggleListening: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_orb")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 600 else 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = if (isListening) 0.85f else 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 600 else 1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isListening) "Ouvindo... Fale sua instrução ou pergunta" else "Falar com Weyn Turbo",
            color = if (isListening) MatrixRed else TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Glowing Voice Mic Orb
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .clickable { onToggleListening() }
                .testTag("voice_mic_orb")
        ) {
            // Outermost soft glow
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(MatrixRedGlow.copy(alpha = glowAlpha * 0.4f))
            )

            // Middle ring
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .scale(pulseScale * 0.95f)
                    .clip(CircleShape)
                    .background(MatrixRedDark.copy(alpha = 0.45f))
                    .border(1.5.dp, MatrixRed.copy(alpha = glowAlpha), CircleShape)
            )

            // Central solid action button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MatrixRed,
                                MatrixRedDark,
                                Color(0xFF6B0E23)
                            )
                        )
                    )
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Microfone de voz",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Equalizer audio visualizer bars
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(18.dp)
        ) {
            val barHeights = if (isListening) {
                listOf(8.dp, 16.dp, 12.dp, 18.dp, 10.dp, 15.dp, 6.dp)
            } else {
                listOf(4.dp, 7.dp, 5.dp, 9.dp, 6.dp, 8.dp, 4.dp)
            }

            barHeights.forEachIndexed { index, baseHeight ->
                val dynamicHeight = if (isListening) {
                    val factor by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(250 + (index * 60)),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "bar_$index"
                    )
                    baseHeight * factor
                } else {
                    baseHeight
                }

                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(dynamicHeight)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isListening) MatrixRed else MatrixRed.copy(alpha = 0.6f))
                )
            }
        }
    }
}
