package com.example.data.api

import com.example.data.model.ApiErrorResponse
import com.example.data.model.ChatCompletionRequest
import com.example.data.model.ChatCompletionResponse
import com.example.data.model.ModelListResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class WeynApiService(
    private var baseUrl: String = "https://matrixchats.com/api/v1",
    private var apiKey: String = "mc_t62aun79l70wltmgbak95u3da4nhss1884mhl6bn"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val chatRequestAdapter = moshi.adapter(ChatCompletionRequest::class.java)
    private val chatResponseAdapter = moshi.adapter(ChatCompletionResponse::class.java)
    private val errorResponseAdapter = moshi.adapter(ApiErrorResponse::class.java)
    private val modelListAdapter = moshi.adapter(ModelListResponse::class.java)

    fun updateCredentials(newBaseUrl: String, newApiKey: String) {
        baseUrl = newBaseUrl.trimEnd('/')
        apiKey = newApiKey.trim()
    }

    suspend fun sendChatCompletion(request: ChatCompletionRequest): Result<ChatCompletionResponse> =
        withContext(Dispatchers.IO) {
            try {
                val jsonPayload = chatRequestAdapter.toJson(request)
                val body = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())

                val httpRequest = Request.Builder()
                    .url("$baseUrl/chat/completions")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                client.newCall(httpRequest).execute().use { response ->
                    val responseBody = response.body?.string().orEmpty()

                    if (response.isSuccessful) {
                        val parsed = chatResponseAdapter.fromJson(responseBody)
                        if (parsed != null && parsed.choices.isNotEmpty()) {
                            Result.success(parsed)
                        } else {
                            Result.failure(Exception("Resposta vazia da IA"))
                        }
                    } else {
                        val errorObj = try {
                            errorResponseAdapter.fromJson(responseBody)
                        } catch (_: Exception) {
                            null
                        }
                        val errorMsg = errorObj?.error ?: errorObj?.message ?: "Erro HTTP ${response.code}: $responseBody"
                        Result.failure(ApiException(errorMsg, response.code, errorObj?.code))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getAvailableModels(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val httpRequest = Request.Builder()
                .url("$baseUrl/models")
                .addHeader("Authorization", "Bearer $apiKey")
                .get()
                .build()

            client.newCall(httpRequest).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val parsed = modelListAdapter.fromJson(responseBody)
                    val modelIds = parsed?.data?.map { it.id } ?: emptyList()
                    Result.success(modelIds)
                } else {
                    Result.failure(Exception("HTTP ${response.code} ao buscar modelos"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class ApiException(
    message: String,
    val statusCode: Int,
    val errorCode: String? = null
) : Exception(message)
