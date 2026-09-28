package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val role: String,
    val content: String,
    val timestamp: Long,
    val attachedDocumentName: String?,
    val attachedDocumentType: String?,
    val isError: Boolean,
    val errorDetails: String?,
    val modelName: String?
)

@Entity(tableName = "saved_documents")
data class DocumentEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val fileType: String,
    val sizeBytes: Long,
    val wordCount: Int,
    val pageCount: Int,
    val textContent: String,
    val summaryText: String?,
    val timestamp: Long
)
