package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BotBlue
import com.example.ui.theme.BotBlueLight
import com.example.ui.theme.BotCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Realistic movable 3D character component for the half-screen hero section.
 * Features:
 * - Natural organic 3D breathing, tilting, and floating kinematics
 * - Interactive 3D touch/drag parallax tracking with spring physics
 * - Interactive tap reaction (friendly nod and greeting bounce)
 * - Dynamic visor eye blink & scanning neon hologram effect
 * - Orbiting 3D cybernetic particles and depth-of-field ambient lighting
 */
@Composable
fun HalfScreenCharacterHero(
    modelName: String,
    isProcessing: Boolean,
    onClearChat: () -> Unit,
    onOpenSettings: () -> Unit,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Interactive spring animatables for touch tracking
    val dragYaw = remember { Animatable(0f) }
    val dragPitch = remember { Animatable(0f) }
    val dragTransX = remember { Animatable(0f) }
    val tapBounce = remember { Animatable(0f) }

    // Natural Eye Blinking State
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay((3200..5000).random().toLong())
            isBlinking = true
            delay(140)
            isBlinking = false
        }
    }

    // Interactive speech bubble tip
    var speechBubbleText by remember {
        mutableStateOf("✨ Olá! Toque ou arraste em mim para interagir")
    }

    // Infinite ambient animations
    val infiniteTransition = rememberInfiniteTransition(label = "robot_3d_physics")

    // 1. Natural slow breathing cycle (bobbing up and down)
    val breathingBob by infiniteTransition.animateFloat(
        initialValue = -7f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isProcessing) 1100 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingBob"
    )

    // 2. Subtle head/torso Z-axis natural sway
    val organicSwayZ by infiniteTransition.animateFloat(
        initialValue = -1.6f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "organicSwayZ"
    )

    // 3. 3D Perspective Pitch (chest breathing in and out in depth)
    val organicPitchX by infiniteTransition.animateFloat(
        initialValue = -2.2f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isProcessing) 1200 else 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "organicPitchX"
    )

    // 4. 3D Perspective Yaw (gently surveying left and right)
    val organicYawY by infiniteTransition.animateFloat(
        initialValue = -3.2f,
        targetValue = 3.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "organicYawY"
    )

    // 5. Breathing scale expansion
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1.00f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    // 6. Holographic orbit angle
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isProcessing) 4000 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitAngle"
    )

    // 7. Visor scan sweep
    val visorScanPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isProcessing) 900 else 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "visorScanPhase"
    )

    // 8. Ambient glow pulse
    val ambientGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = if (isProcessing) 0.9f else 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isProcessing) 800 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientGlow"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(Color(0xFF060912))
            .testTag("half_screen_character_hero")
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val totalHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

        // 1. Dynamic 3D Ambient Aura in the background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BotCyan.copy(alpha = ambientGlow * 0.45f),
                            BotBlue.copy(alpha = ambientGlow * 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(totalWidthPx * 0.5f, totalHeightPx * 0.42f),
                        radius = totalHeightPx * 0.8f
                    )
                )
        )

        // 2. Orbiting Holographic Rings & 3D Tech Nodes behind character
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width * 0.5f
            val cy = size.height * 0.45f
            val rx = size.width * 0.42f
            val ry = size.height * 0.20f

            // Orbit ring 1
            drawOval(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        BotCyan.copy(alpha = 0.15f),
                        BotCyan.copy(alpha = 0.65f),
                        Color.Transparent
                    )
                ),
                topLeft = Offset(cx - rx, cy - ry),
                size = androidx.compose.ui.geometry.Size(rx * 2f, ry * 2f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Orbiting holographic nodes
            val rad1 = Math.toRadians((orbitAngle).toDouble())
            val px1 = cx + rx * cos(rad1).toFloat()
            val py1 = cy + ry * sin(rad1).toFloat()
            drawCircle(
                color = BotCyan,
                radius = 4.dp.toPx(),
                center = Offset(px1, py1)
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(px1, py1)
            )

            val rad2 = Math.toRadians((orbitAngle + 180.0))
            val px2 = cx + rx * cos(rad2).toFloat()
            val py2 = cy + ry * sin(rad2).toFloat()
            drawCircle(
                color = BotBlueLight,
                radius = 3.5.dp.toPx(),
                center = Offset(px2, py2)
            )
        }

        // 3. Movable 3D Character Layer with Natural Physics & Touch Tracking
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                dragYaw.animateTo(
                                    0f,
                                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                                )
                            }
                            coroutineScope.launch {
                                dragPitch.animateTo(
                                    0f,
                                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                                )
                            }
                            coroutineScope.launch {
                                dragTransX.animateTo(
                                    0f,
                                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                                )
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch { dragYaw.snapTo(0f) }
                            coroutineScope.launch { dragPitch.snapTo(0f) }
                            coroutineScope.launch { dragTransX.snapTo(0f) }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val targetYaw = (dragYaw.value + (dragAmount.x / totalWidthPx) * 22f).coerceIn(-18f, 18f)
                            val targetPitch = (dragPitch.value - (dragAmount.y / totalHeightPx) * 16f).coerceIn(-12f, 12f)
                            val targetTransX = (dragTransX.value + dragAmount.x * 0.35f).coerceIn(-35f, 35f)
                            coroutineScope.launch { dragYaw.snapTo(targetYaw) }
                            coroutineScope.launch { dragPitch.snapTo(targetPitch) }
                            coroutineScope.launch { dragTransX.snapTo(targetTransX) }
                        }
                    )
                }
                .clickable {
                    // Tap reaction: Cheerful nod bounce!
                    coroutineScope.launch {
                        speechBubbleText = listOf(
                            "🤖 Olá! Estou pronto para responder suas perguntas!",
                            "⚡ Envie um PDF ou CSV para eu resumir!",
                            "💡 Pode me perguntar qualquer assunto!",
                            "📊 Análise de dados sem filtros em tempo real!"
                        ).random()
                        tapBounce.animateTo(-16f, tween(120, easing = FastOutSlowInEasing))
                        tapBounce.animateTo(
                            0f,
                            spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                        )
                    }
                }
                .graphicsLayer {
                    // Real 3D camera perspective
                    cameraDistance = 18f * density.density

                    // Combined organic movement + interactive touch displacement
                    val combinedPitch = organicPitchX + dragPitch.value
                    val combinedYaw = organicYawY + dragYaw.value
                    val combinedSway = organicSwayZ + (dragYaw.value * 0.15f)

                    rotationX = combinedPitch
                    rotationY = combinedYaw
                    rotationZ = combinedSway

                    translationX = dragTransX.value
                    translationY = breathingBob + tapBounce.value
                    scaleX = breathingScale
                    scaleY = breathingScale
                }
        ) {
            // High-resolution 3D robot image
            Image(
                painter = painterResource(id = R.drawable.hero_character_halfscree_1790614426757),
                contentDescription = "Personagem 3D Móvel e Interativo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Visor Eye Natural Blink and Neon Scanner Layer
            Canvas(modifier = Modifier.fillMaxSize()) {
                val visorCenterX = size.width * 0.50f
                val visorCenterY = size.height * 0.38f
                val visorWidth = size.width * 0.28f
                val visorHeight = size.height * 0.08f

                // Blinking shutter overlay
                if (isBlinking) {
                    drawOval(
                        color = Color(0xDD090D18),
                        topLeft = Offset(visorCenterX - visorWidth * 0.5f, visorCenterY - visorHeight * 0.5f),
                        size = androidx.compose.ui.geometry.Size(visorWidth, visorHeight)
                    )
                } else {
                    // Neon Cyan Visor Scan Light Beam sweeping naturally
                    val scanX = visorCenterX - (visorWidth * 0.5f) + (visorWidth * visorScanPhase)
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                BotCyan.copy(alpha = 0.8f),
                                Color.White.copy(alpha = 0.95f),
                                BotCyan.copy(alpha = 0.8f),
                                Color.Transparent
                            ),
                            startX = scanX - 25.dp.toPx(),
                            endX = scanX + 25.dp.toPx()
                        ),
                        start = Offset(scanX, visorCenterY - visorHeight * 0.4f),
                        end = Offset(scanX, visorCenterY + visorHeight * 0.4f),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }
        }

        // 4. Subtle holographic tech ring in front for depth layering
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width * 0.5f
            val cy = size.height * 0.52f
            val rx = size.width * 0.38f
            val ry = size.height * 0.16f

            drawOval(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        BotCyan.copy(alpha = 0.35f),
                        Color.Transparent,
                        BotBlueLight.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                ),
                topLeft = Offset(cx - rx, cy - ry),
                size = androidx.compose.ui.geometry.Size(rx * 2f, ry * 2f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // 5. Seamless bottom fade into clean chat area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            DarkBackground.copy(alpha = 0.65f),
                            DarkBackground
                        )
                    )
                )
        )

        // 6. Top action bar with clean buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clear Chat Button
            IconButton(
                onClick = onClearChat,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x990C1222))
                    .border(1.dp, Color(0x663B82F6), CircleShape)
                    .testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Limpar conversa",
                    tint = TextPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Model Status Pill in Center
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC090E1C))
                    .border(1.dp, BotCyan.copy(alpha = ambientGlow * 0.8f), RoundedCornerShape(20.dp))
                    .clickable { onOpenSettings() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isProcessing) BotCyan else NeonGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isProcessing) "Pensando..." else "3D • $modelName",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = BotCyan,
                    modifier = Modifier.size(13.dp)
                )
            }

            // Settings Button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x990C1222))
                    .border(1.dp, Color(0x663B82F6), CircleShape)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = TextPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        // 7. Interactive character speech bubble at the bottom of the hero
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xE60D1527))
                    .border(1.dp, BotCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable {
                        coroutineScope.launch {
                            tapBounce.animateTo(-12f, tween(100))
                            tapBounce.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = if (isProcessing)
                        "⚡ Analisando documento e processando resposta..."
                    else
                        speechBubbleText,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
