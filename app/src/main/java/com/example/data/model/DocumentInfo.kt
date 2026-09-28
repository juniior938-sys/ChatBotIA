package com.example.data.model

data class DocumentInfo(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val type: DocumentType,
    val sizeBytes: Long,
    val textContent: String,
    val pageCount: Int = 1,
    val wordCount: Int = 0,
    val characterCount: Int = 0,
    val tableHeaders: List<String> = emptyList(),
    val tableRows: List<List<String>> = emptyList()
) {
    val formattedSize: String
        get() {
            if (sizeBytes < 1024) return "$sizeBytes B"
            val kb = sizeBytes / 1024.0
            if (kb < 1024) return String.format(java.util.Locale.US, "%.1f KB", kb)
            val mb = kb / 1024.0
            return String.format(java.util.Locale.US, "%.1f MB", mb)
        }

    val isTable: Boolean
        get() = type == DocumentType.TABLE_CSV || type == DocumentType.TABLE_TSV || tableRows.isNotEmpty()
}

enum class DocumentType(val label: String, val badgeColorHex: Long) {
    PDF("PDF", 0xFFE53935),
    TABLE_CSV("CSV", 0xFF43A047),
    TABLE_TSV("TSV", 0xFF2E7D32),
    TEXT("TXT", 0xFF1E88E5),
    MARKDOWN("MD", 0xFF8E24AA),
    CODE("CÓDIGO", 0xFFFF8F00),
    OTHER("DOC", 0xFF546E7A)
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val attachedDocumentName: String? = null,
    val attachedDocumentType: DocumentType? = null,
    val isError: Boolean = false,
    val errorDetails: String? = null,
    val isProcessing: Boolean = false,
    val modelName: String? = null
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}
