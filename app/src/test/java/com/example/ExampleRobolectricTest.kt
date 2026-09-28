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
}
