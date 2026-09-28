package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.document.SampleDocuments
import com.example.data.model.DocumentType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Weyn AI", appName)
    }

    @Test
    fun `verify sample documents data integrity`() {
        val tableDoc = SampleDocuments.financialTableSample
        assertEquals(DocumentType.TABLE_CSV, tableDoc.type)
        assertTrue(tableDoc.tableHeaders.isNotEmpty())
        assertTrue(tableDoc.tableRows.isNotEmpty())
        assertTrue(tableDoc.isTable)

        val legalDoc = SampleDocuments.legalContractSample
        assertEquals(DocumentType.PDF, legalDoc.type)
        assertTrue(legalDoc.textContent.contains("CONTRATO"))

        val whitepaper = SampleDocuments.technicalWhitepaperSample
        assertEquals(DocumentType.MARKDOWN, whitepaper.type)
        assertNotNull(whitepaper.formattedSize)
    }

    @Test
    fun `verify question and answer flow in repository`() = kotlinx.coroutines.runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = com.example.data.api.WeynRepository(context)

        // Test question execution
        val result = repository.executeChat("Quem é você e o que você faz?", null)
        val content = when (result) {
            is com.example.data.api.ChatResult.Success -> result.content
            is com.example.data.api.ChatResult.BalanceOrErrorFallback -> result.fallbackResponse
        }
        assertTrue(content.contains("Assistente Pessoal de IA"))

        // Test question with table document
        val tableResult = repository.executeChat(
            "Analisar dados",
            SampleDocuments.financialTableSample,
            com.example.data.api.SummaryActionType.TABLE_ANALYSIS
        )
        val tableContent = when (tableResult) {
            is com.example.data.api.ChatResult.Success -> tableContentCheck(tableResult.content)
            is com.example.data.api.ChatResult.BalanceOrErrorFallback -> tableResult.fallbackResponse
        }
        assertTrue(tableContent.isNotEmpty())
    }

    private fun tableContentCheck(text: String): String = text
}
