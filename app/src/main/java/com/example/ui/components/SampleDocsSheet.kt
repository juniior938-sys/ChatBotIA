package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.document.SampleDocuments
import com.example.data.model.DocumentInfo
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MatrixRed
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SampleDocsSheet(
    onDismiss: () -> Unit,
    onSelectDoc: (DocumentInfo) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MatrixRed.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("sample_docs_dialog"),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MatrixRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Documentos Modelo",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Carregue um arquivo estruturado de teste para experimentar o resumo sem filtros e análise de dados instantaneamente:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Item 1: Financial CSV Table
                DocModelItem(
                    icon = Icons.Default.TableChart,
                    iconTint = CyberCyan,
                    badge = "CSV TABELADO",
                    badgeColor = Color(0xFF00897B),
                    title = "Relatório Financeiro Q3 2026",
                    description = "9 linhas de receitas, despesas, margens brutas e crescimento por departamento.",
                    onClick = { onSelectDoc(SampleDocuments.financialTableSample) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Item 2: Legal SLA Contract PDF
                DocModelItem(
                    icon = Icons.Default.Gavel,
                    iconTint = MatrixRed,
                    badge = "CONTRATO PDF",
                    badgeColor = Color(0xFFC2185B),
                    title = "Contrato Enterprise SLA & Termos",
                    description = "Cláusulas de disponibilidade de nós de computação, penalidades de SLA e confidencialidade.",
                    onClick = { onSelectDoc(SampleDocuments.legalContractSample) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Item 3: Technical Whitepaper
                DocModelItem(
                    icon = Icons.Default.Memory,
                    iconTint = NeonGreen,
                    badge = "WHITEPAPER MD",
                    badgeColor = Color(0xFF2E7D32),
                    title = "Whitepaper Weyn Matrix Engine",
                    description = "Arquitetura técnica de inferência neural sem censura, latência e compatibilidade com OpenAI.",
                    onClick = { onSelectDoc(SampleDocuments.technicalWhitepaperSample) }
                )
            }
        }
    }
}

@Composable
private fun DocModelItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    badge: String,
    badgeColor: Color,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
    }
}
