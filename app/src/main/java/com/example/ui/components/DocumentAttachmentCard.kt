package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.SummaryActionType
import com.example.data.model.DocumentInfo
import com.example.ui.theme.BotBlue
import com.example.ui.theme.BotBlueLight
import com.example.ui.theme.BotCyan
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DocumentAttachmentCard(
    document: DocumentInfo,
    onRemove: () -> Unit,
    onViewTable: () -> Unit,
    onActionClick: (SummaryActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, BotBlueLight.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag("document_attachment_card")
    ) {
        // Top info bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Document Type Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(document.type.badgeColorHex))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = document.type.label,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.name,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${document.formattedSize} • ${document.pageCount} ${if (document.isTable) "linhas" else "páginas"} • ${document.wordCount} palavras",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            if (document.isTable) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BotCyan.copy(alpha = 0.15f))
                        .border(1.dp, BotCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .clickable { onViewTable() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = BotCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Grade",
                            color = BotCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF26334D))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover documento",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick summarization action chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionPill(
                icon = Icons.Default.Description,
                label = "Resumo Executivo",
                onClick = { onActionClick(SummaryActionType.EXECUTIVE_SUMMARY) }
            )

            ActionPill(
                icon = Icons.Default.FormatListBulleted,
                label = "Pontos-Chave",
                onClick = { onActionClick(SummaryActionType.KEY_POINTS) }
            )

            if (document.isTable) {
                ActionPill(
                    icon = Icons.Default.QueryStats,
                    label = "Análise Quantitativa",
                    onClick = { onActionClick(SummaryActionType.TABLE_ANALYSIS) }
                )
            }

            ActionPill(
                icon = Icons.Default.ElectricBolt,
                label = "Simplificar",
                onClick = { onActionClick(SummaryActionType.SIMPLIFY_LANGUAGE) }
            )
        }
    }
}

@Composable
private fun ActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BotCyan,
                modifier = Modifier.size(14.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFF1E2B45),
            labelColor = TextPrimary
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = BotBlueLight.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(12.dp)
    )
}
