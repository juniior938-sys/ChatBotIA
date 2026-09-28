package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.ChatResult
import com.example.data.api.SummaryActionType
import com.example.data.api.WeynRepository
import com.example.data.document.DocumentParser
import com.example.data.document.SampleDocuments
import com.example.data.model.ChatMessage
import com.example.data.model.DocumentInfo
import com.example.data.model.MessageRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val initialApiKey = try {
        BuildConfig.WEYN_API_KEY.ifEmpty { "mc_t62aun79l70wltmgbak95u3da4nhss1884mhl6bn" }
    } catch (_: Exception) {
        "mc_t62aun79l70wltmgbak95u3da4nhss1884mhl6bn"
    }

    private val initialBaseUrl = try {
        BuildConfig.WEYN_BASE_URL.ifEmpty { "https://matrixchats.com/api/v1" }
    } catch (_: Exception) {
        "https://matrixchats.com/api/v1"
    }

    val repository = WeynRepository(application, initialBaseUrl, initialApiKey)

    val chatMessages: StateFlow<List<ChatMessage>> = repository.getChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _attachedDocument = MutableStateFlow<DocumentInfo?>(null)
    val attachedDocument: StateFlow<DocumentInfo?> = _attachedDocument.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _activeTableDocument = MutableStateFlow<DocumentInfo?>(null)
    val activeTableDocument: StateFlow<DocumentInfo?> = _activeTableDocument.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showSampleDocsSheet = MutableStateFlow(false)
    val showSampleDocsSheet: StateFlow<Boolean> = _showSampleDocsSheet.asStateFlow()

    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    init {
        // Init TTS for voice responses
        textToSpeech = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("pt", "BR"))
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                }
            }
        }

        // Ensure welcome message is set
        viewModelScope.launch {
            val existing = repository.getChatMessages().firstOrNull() ?: emptyList()
            if (existing.isEmpty() || (existing.size == 1 && existing.first().role == MessageRole.ASSISTANT && !existing.first().content.startsWith("Oi Sou seu assistente pessoal"))) {
                repository.clearHistory()
                val welcome = ChatMessage(
                    role = MessageRole.ASSISTANT,
                    content = "Oi Sou seu assistente pessoal o que deseja saber!",
                    modelName = "Weyn • ${repository.currentModel}"
                )
                repository.saveMessage(welcome)
            }
        }
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun attachDocumentFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val parsed = DocumentParser.parseUri(context, uri)
                _attachedDocument.value = parsed
            } catch (e: Exception) {
                val errMsg = ChatMessage(
                    role = MessageRole.ASSISTANT,
                    content = "Erro ao processar o arquivo: ${e.message}",
                    isError = true
                )
                repository.saveMessage(errMsg)
            }
        }
    }

    fun loadSampleDocument(doc: DocumentInfo) {
        _attachedDocument.value = doc
        _showSampleDocsSheet.value = false
    }

    fun removeAttachedDocument() {
        _attachedDocument.value = null
    }

    fun viewTable(doc: DocumentInfo) {
        _activeTableDocument.value = doc
    }

    fun closeTableViewer() {
        _activeTableDocument.value = null
    }

    fun openSettings() {
        _showSettingsDialog.value = true
    }

    fun closeSettings() {
        _showSettingsDialog.value = false
    }

    fun openSampleDocs() {
        _showSampleDocsSheet.value = true
    }

    fun closeSampleDocs() {
        _showSampleDocsSheet.value = false
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearHistory()
            val welcome = ChatMessage(
                role = MessageRole.ASSISTANT,
                content = "Oi Sou seu assistente pessoal o que deseja saber!",
                modelName = "Weyn • ${repository.currentModel}"
            )
            repository.saveMessage(welcome)
        }
    }

    fun toggleListening() {
        _isListening.value = !_isListening.value
        if (_isListening.value) {
            // Simulated voice speech trigger
            _inputText.value = "Faça um resumo executivo objetivo dos principais pontos do documento anexado."
        }
    }

    fun speakText(text: String) {
        if (isTtsReady && textToSpeech != null) {
            // Strip markdown symbols for clean TTS voice
            val clean = text.replace(Regex("""[#*_`~>\[\]\(\)]"""), " ")
            textToSpeech?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "weyn_tts")
        }
    }

    fun sendMessage(
        prompt: String = _inputText.value,
        actionType: SummaryActionType = SummaryActionType.FREE_CHAT
    ) {
        val userPrompt = if (actionType != SummaryActionType.FREE_CHAT && prompt.isBlank()) {
            when (actionType) {
                SummaryActionType.EXECUTIVE_SUMMARY -> "Resumo Executivo do Documento"
                SummaryActionType.KEY_POINTS -> "Extrair Pontos-Chave e Ações"
                SummaryActionType.TABLE_ANALYSIS -> "Análise Estrutural e Numérica da Tabela"
                SummaryActionType.SIMPLIFY_LANGUAGE -> "Simplificar Linguagem do Documento"
                SummaryActionType.FREE_CHAT -> prompt
            }
        } else {
            prompt
        }

        if (userPrompt.isBlank() && _attachedDocument.value == null) return

        val currentDoc = _attachedDocument.value
        _inputText.value = ""
        _isProcessing.value = true

        viewModelScope.launch {
            // Save user message
            val userMessage = ChatMessage(
                role = MessageRole.USER,
                content = userPrompt,
                attachedDocumentName = currentDoc?.name,
                attachedDocumentType = currentDoc?.type
            )
            repository.saveMessage(userMessage)

            // Save processing placeholder
            val result = repository.executeChat(userPrompt, currentDoc, actionType)

            when (result) {
                is ChatResult.Success -> {
                    val aiMsg = ChatMessage(
                        role = MessageRole.ASSISTANT,
                        content = result.content,
                        modelName = result.model
                    )
                    repository.saveMessage(aiMsg)
                    currentDoc?.let { repository.saveDocument(it, result.content) }
                }

                is ChatResult.BalanceOrErrorFallback -> {
                    val aiMsg = ChatMessage(
                        role = MessageRole.ASSISTANT,
                        content = result.fallbackResponse,
                        isError = true,
                        errorDetails = result.apiError,
                        modelName = result.model
                    )
                    repository.saveMessage(aiMsg)
                    currentDoc?.let { repository.saveDocument(it, result.fallbackResponse) }
                }
            }

            _isProcessing.value = false
        }
    }

    fun updateApiConfig(baseUrl: String, apiKey: String, model: String) {
        repository.updateConfig(baseUrl, apiKey, model)
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
