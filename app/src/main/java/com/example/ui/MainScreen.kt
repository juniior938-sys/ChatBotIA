package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChatMessageItem
import com.example.ui.components.HalfScreenCharacterHero
import com.example.ui.components.SampleDocsSheet
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TableVisualizerDialog
import com.example.ui.theme.BotBlue
import com.example.ui.theme.BotBlueLight
import com.example.ui.theme.BotCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    val chatMessages by viewModel.chatMessages.collectAsState()
    val attachedDocument by viewModel.attachedDocument.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val activeTableDocument by viewModel.activeTableDocument.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val showSampleDocsSheet by viewModel.showSampleDocsSheet.collectAsState()

    var showAttachMenu by remember { mutableStateOf(false) }

    // File picker for PDF, CSV, TXT, MD, etc.
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.attachDocumentFromUri(context, uri)
        }
    }

    // Scroll to bottom when messages update
    LaunchedEffect(chatMessages.size, isProcessing) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .imePadding()
    ) {
        val screenHeight = maxHeight
        val heroHeight = (screenHeight * 0.46f).coerceIn(240.dp, 420.dp)

        Column(modifier = Modifier.fillMaxSize()) {
            // 1. TOP HALF: Prominent 3D Character Hero
            HalfScreenCharacterHero(
                modelName = viewModel.repository.currentModel,
                isProcessing = isProcessing,
                onClearChat = { viewModel.clearChat() },
                onOpenSettings = { viewModel.openSettings() },
                height = heroHeight
            )

            // 2. BOTTOM HALF: Clean, Modern Chat Area ("Abaixo o chat limpo")
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(DarkBackground)
            ) {
                // Messages List Area
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .testTag("clean_chat_messages_list")
                ) {
                    if (chatMessages.isEmpty()) {
                        item {
                            CleanChatEmptyNotice(
                                onSelectSample = { viewModel.openSampleDocs() }
                            )
                        }
                    } else {
                        items(chatMessages, key = { it.id }) { message ->
                            ChatMessageItem(
                                message = message,
                                onSpeakText = { text -> viewModel.speakText(text) },
                                onOpenSettings = { viewModel.openSettings() }
                            )
                        }
                    }

                    if (isProcessing) {
                        item {
                            ChatMessageItem(
                                message = com.example.data.model.ChatMessage(
                                    role = com.example.data.model.MessageRole.ASSISTANT,
                                    content = "",
                                    isProcessing = true
                                ),
                                onSpeakText = {},
                                onOpenSettings = {}
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(6.dp)) }
                }

                // Clean Attached Document Pill (if any file is attached)
                if (attachedDocument != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, BotBlueLight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = BotCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = attachedDocument!!.name,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Text(
                                text = "${attachedDocument!!.formattedSize} • ${attachedDocument!!.pageCount} ${if (attachedDocument!!.isTable) "linhas" else "páginas"}",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        if (attachedDocument!!.isTable) {
                            Text(
                                text = "Ver Grade",
                                color = BotCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { viewModel.viewTable(attachedDocument!!) }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.removeAttachedDocument() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remover",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Quick Action Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CleanActionPill(
                        icon = Icons.Default.Description,
                        label = "Resumir",
                        onClick = {
                            if (attachedDocument != null) {
                                viewModel.sendMessage(actionType = com.example.data.api.SummaryActionType.EXECUTIVE_SUMMARY)
                            } else {
                                viewModel.openSampleDocs()
                            }
                        }
                    )

                    CleanActionPill(
                        icon = Icons.Default.Search,
                        label = "Pesquisar",
                        onClick = {
                            viewModel.onInputTextChanged("Pesquise e me explique de forma direta: ")
                        }
                    )

                    CleanActionPill(
                        icon = Icons.Default.Lightbulb,
                        label = "Pensar",
                        onClick = {
                            viewModel.onInputTextChanged("Analise profundamente passo a passo: ")
                        }
                    )

                    CleanActionPill(
                        icon = Icons.Default.FolderOpen,
                        label = "Exemplos",
                        onClick = { viewModel.openSampleDocs() }
                    )
                }

                // Clean Bottom Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface)
                        .border(1.dp, DarkSurfaceBorder)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "+" Attachment Button with dropdown
                    Box {
                        IconButton(
                            onClick = { showAttachMenu = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BotBlue)
                                .testTag("attach_document_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Anexar documento",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showAttachMenu,
                            onDismissRequest = { showAttachMenu = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Carregar PDF / Planilha / TXT", color = TextPrimary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.FileUpload, contentDescription = null, tint = BotCyan)
                                },
                                onClick = {
                                    showAttachMenu = false
                                    filePickerLauncher.launch(
                                        arrayOf(
                                            "application/pdf",
                                            "text/csv",
                                            "text/comma-separated-values",
                                            "text/tab-separated-values",
                                            "text/plain",
                                            "text/markdown",
                                            "application/json",
                                            "*/*"
                                        )
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Documentos Modelo (PDF / CSV)", color = TextPrimary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = BotBlueLight)
                                },
                                onClick = {
                                    showAttachMenu = false
                                    viewModel.openSampleDocs()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Text Input Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.onInputTextChanged(it) },
                        placeholder = {
                            Text(
                                text = if (attachedDocument != null)
                                    "Pergunte sobre ${attachedDocument!!.name}..."
                                else
                                    "Pergunte algo ou envie um documento...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            focusManager.clearFocus()
                            viewModel.sendMessage()
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = BotBlueLight,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text_field")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Mic Voice Button
                    IconButton(
                        onClick = { viewModel.toggleListening() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isListening) BotCyan else DarkSurfaceElevated)
                            .border(1.dp, if (isListening) BotCyan else DarkSurfaceBorder, CircleShape)
                            .testTag("voice_mic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voz",
                            tint = if (isListening) DarkBackground else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.sendMessage()
                        },
                        enabled = !isProcessing && (inputText.isNotBlank() || attachedDocument != null),
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() || attachedDocument != null) BotBlue else DarkSurfaceElevated
                            )
                            .testTag("send_message_button")
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = if (inputText.isNotBlank() || attachedDocument != null) Color.White else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (activeTableDocument != null) {
        TableVisualizerDialog(
            document = activeTableDocument!!,
            onDismiss = { viewModel.closeTableViewer() },
            onSummarizeTable = {
                viewModel.sendMessage(actionType = com.example.data.api.SummaryActionType.TABLE_ANALYSIS)
            }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            repository = viewModel.repository,
            onDismiss = { viewModel.closeSettings() },
            onSave = { baseUrl, apiKey, model ->
                viewModel.updateApiConfig(baseUrl, apiKey, model)
            }
        )
    }

    if (showSampleDocsSheet) {
        SampleDocsSheet(
            onDismiss = { viewModel.closeSampleDocs() },
            onSelectDoc = { doc -> viewModel.loadSampleDocument(doc) }
        )
    }
}

@Composable
private fun CleanActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = BotCyan,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CleanChatEmptyNotice(
    onSelectSample: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Chat limpo iniciado",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Digite sua mensagem abaixo ou clique para testar um documento modelo.",
            color = TextSecondary,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Carregar Exemplo de PDF / Planilha",
            color = BotCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onSelectSample() }
                .padding(6.dp)
        )
    }
}
