package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<ChatMessageDto>,
    @Json(name = "temperature") val temperature: Double = 0.7,
    @Json(name = "max_tokens") val maxTokens: Int? = null,
    @Json(name = "stream") val stream: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ChatChoiceDto(
    @Json(name = "index") val index: Int = 0,
    @Json(name = "message") val message: ChatMessageDto,
    @Json(name = "finish_reason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class UsageDto(
    @Json(name = "prompt_tokens") val promptTokens: Int = 0,
    @Json(name = "completion_tokens") val completionTokens: Int = 0,
    @Json(name = "total_tokens") val totalTokens: Int = 0
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    @Json(name = "id") val id: String? = null,
    @Json(name = "object") val objectType: String? = null,
    @Json(name = "created") val created: Long? = null,
    @Json(name = "model") val model: String? = null,
    @Json(name = "choices") val choices: List<ChatChoiceDto> = emptyList(),
    @Json(name = "usage") val usage: UsageDto? = null
)

@JsonClass(generateAdapter = true)
data class ApiErrorResponse(
    @Json(name = "error") val error: String? = null,
    @Json(name = "code") val code: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class ModelItemDto(
    @Json(name = "id") val id: String,
    @Json(name = "display_name") val displayName: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "context_tokens") val contextTokens: Int? = null,
    @Json(name = "throughput_tps") val throughputTps: Int? = null
)

@JsonClass(generateAdapter = true)
data class ModelListResponse(
    @Json(name = "data") val data: List<ModelItemDto> = emptyList()
)
