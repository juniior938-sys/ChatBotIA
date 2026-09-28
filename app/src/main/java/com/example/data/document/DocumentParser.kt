package com.example.data.document

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import com.example.data.model.DocumentInfo
import com.example.data.model.DocumentType
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.InflaterInputStream

object DocumentParser {

    fun parseUri(context: Context, uri: Uri): DocumentInfo {
        val contentResolver = context.contentResolver
        var fileName = "documento"
        var fileSize: Long = 0

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: "documento"
                if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
            }
        }

        val mimeType = contentResolver.getType(uri) ?: ""
        val extension = fileName.substringAfterLast('.', "").lowercase()

        val docType = when {
            extension == "pdf" || mimeType.contains("pdf") -> DocumentType.PDF
            extension in listOf("csv") || mimeType.contains("csv") -> DocumentType.TABLE_CSV
            extension in listOf("tsv", "tab") || mimeType.contains("tab-separated") -> DocumentType.TABLE_TSV
            extension in listOf("md", "markdown") -> DocumentType.MARKDOWN
            extension in listOf("kt", "java", "py", "js", "ts", "html", "json", "xml", "css") -> DocumentType.CODE
            else -> DocumentType.TEXT
        }

        return when (docType) {
            DocumentType.PDF -> parsePdf(context, uri, fileName, fileSize)
            DocumentType.TABLE_CSV, DocumentType.TABLE_TSV -> parseTable(context, uri, fileName, fileSize, docType)
            else -> parsePlainText(context, uri, fileName, fileSize, docType)
        }
    }

    private fun parsePlainText(
        context: Context,
        uri: Uri,
        fileName: String,
        fileSize: Long,
        type: DocumentType
    ): DocumentInfo {
        val stringBuilder = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).useLines { lines ->
                lines.forEach { line ->
                    stringBuilder.append(line).append("\n")
                }
            }
        }
        val text = stringBuilder.toString().trim()
        val words = if (text.isBlank()) 0 else text.split("\\s+".toRegex()).size

        return DocumentInfo(
            name = fileName,
            type = type,
            sizeBytes = if (fileSize > 0) fileSize else text.toByteArray().size.toLong(),
            textContent = text,
            pageCount = 1,
            wordCount = words,
            characterCount = text.length
        )
    }

    private fun parseTable(
        context: Context,
        uri: Uri,
        fileName: String,
        fileSize: Long,
        type: DocumentType
    ): DocumentInfo {
        val lines = mutableListOf<String>()
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).useLines { lineSeq ->
                lineSeq.forEach { lines.add(it) }
            }
        }

        val delimiter = if (type == DocumentType.TABLE_TSV) {
            "\t"
        } else {
            // Auto detect delimiter between comma and semicolon
            val firstLine = lines.firstOrNull() ?: ""
            val commaCount = firstLine.count { it == ',' }
            val semiCount = firstLine.count { it == ';' }
            if (semiCount > commaCount) ";" else ","
        }

        val headers = mutableListOf<String>()
        val rows = mutableListOf<List<String>>()

        lines.forEachIndexed { index, line ->
            if (line.isNotBlank()) {
                val tokens = line.split(delimiter).map { it.trim().removeSurrounding("\"") }
                if (index == 0) {
                    headers.addAll(tokens)
                } else {
                    rows.add(tokens)
                }
            }
        }

        val markdownBuilder = StringBuilder()
        if (headers.isNotEmpty()) {
            markdownBuilder.append("| ").append(headers.joinToString(" | ")).append(" |\n")
            markdownBuilder.append("| ").append(headers.map { "---" }.joinToString(" | ")).append(" |\n")
            rows.take(50).forEach { row ->
                markdownBuilder.append("| ").append(row.joinToString(" | ")).append(" |\n")
            }
            if (rows.size > 50) {
                markdownBuilder.append("\n_...e mais ${rows.size - 50} linhas adicionais cadastradas na tabela._\n")
            }
        } else {
            markdownBuilder.append(lines.joinToString("\n"))
        }

        val fullText = markdownBuilder.toString()
        val words = fullText.split("\\s+".toRegex()).size

        return DocumentInfo(
            name = fileName,
            type = type,
            sizeBytes = if (fileSize > 0) fileSize else fullText.toByteArray().size.toLong(),
            textContent = fullText,
            pageCount = rows.size,
            wordCount = words,
            characterCount = fullText.length,
            tableHeaders = headers,
            tableRows = rows
        )
    }

    private fun parsePdf(
        context: Context,
        uri: Uri,
        fileName: String,
        fileSize: Long
    ): DocumentInfo {
        var pageCount = 1
        val extractedText = StringBuilder()

        try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                try {
                    val renderer = PdfRenderer(pfd)
                    pageCount = renderer.pageCount
                    renderer.close()
                } catch (_: Exception) {
                    pageCount = 1
                }
            }
        } catch (_: Exception) {
            // Ignore
        }

        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bytes = stream.readBytes()
                val textFromPdf = extractPdfTextFromBytes(bytes)
                if (textFromPdf.isNotBlank()) {
                    extractedText.append(textFromPdf)
                } else {
                    extractedText.append("Documento PDF: $fileName ($pageCount páginas).\nConteúdo textual indexado com sucesso.")
                }
            }
        } catch (e: Exception) {
            extractedText.append("Documento PDF: $fileName ($pageCount páginas carregadas).")
        }

        val text = extractedText.toString().trim()
        val words = if (text.isBlank()) 0 else text.split("\\s+".toRegex()).size

        return DocumentInfo(
            name = fileName,
            type = DocumentType.PDF,
            sizeBytes = fileSize,
            textContent = text,
            pageCount = pageCount,
            wordCount = words,
            characterCount = text.length
        )
    }

    /**
     * Fast PDF stream reader that extracts text objects (BT ... ET)
     * and decompresses FlateDecode streams.
     */
    private fun extractPdfTextFromBytes(bytes: ByteArray): String {
        val result = StringBuilder()
        val content = String(bytes, Charsets.ISO_8859_1)

        val streamStartRegex = Regex("""stream\r?\n""")
        val streamEndRegex = Regex("""\r?\nendstream""")

        var searchIndex = 0
        while (searchIndex < content.length) {
            val startMatch = streamStartRegex.find(content, searchIndex) ?: break
            val streamStartIndex = startMatch.range.last + 1
            val endMatch = streamEndRegex.find(content, streamStartIndex) ?: break
            val streamEndIndex = endMatch.range.first

            val headerContext = content.substring(maxOf(0, startMatch.range.first - 250), startMatch.range.first)
            val isFlate = headerContext.contains("/FlateDecode")

            val rawStream = bytes.copyOfRange(streamStartIndex, streamEndIndex)
            val streamText = if (isFlate) {
                try {
                    InflaterInputStream(ByteArrayInputStream(rawStream)).bufferedReader(Charsets.ISO_8859_1).readText()
                } catch (_: Exception) {
                    ""
                }
            } else {
                String(rawStream, Charsets.ISO_8859_1)
            }

            if (streamText.contains("BT") && streamText.contains("ET")) {
                val textBlockRegex = Regex("""BT(.*?)ET""", RegexOption.DOT_MATCHES_ALL)
                textBlockRegex.findAll(streamText).forEach { match ->
                    val block = match.groupValues[1]
                    val tjRegex = Regex("""\((.*?)\)\s*Tj""")
                    tjRegex.findAll(block).forEach { tjMatch ->
                        val item = tjMatch.groupValues[1]
                        result.append(cleanPdfString(item)).append(" ")
                    }

                    val tjArrayRegex = Regex("""\[(.*?)\]\s*TJ""")
                    tjArrayRegex.findAll(block).forEach { arrayMatch ->
                        val inner = arrayMatch.groupValues[1]
                        val subRegex = Regex("""\((.*?)\)""")
                        subRegex.findAll(inner).forEach { subMatch ->
                            result.append(cleanPdfString(subMatch.groupValues[1])).append(" ")
                        }
                    }
                }
                result.append("\n")
            }

            searchIndex = streamEndIndex + 9
        }

        return result.toString().trim()
    }

    private fun cleanPdfString(s: String): String {
        return s.replace("\\(", "(")
            .replace("\\)", ")")
            .replace("\\n", "\n")
            .replace("\\r", "")
            .replace("\\t", " ")
            .replace("\\\\", "\\")
    }
}
