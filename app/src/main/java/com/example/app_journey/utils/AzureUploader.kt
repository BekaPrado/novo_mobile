package com.example.app_journey.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream

object AzureUploader {

    /**
     * Faz upload de uma imagem para o Azure Blob Storage
     * e retorna a URL pública do arquivo.
     */
    suspend fun uploadImageToAzure(
        inputStream: InputStream,
        fileName: String
    ): String? = withContext(Dispatchers.IO) {

        // ⚙️ Configurações fixas do seu Azure
        val storageAccount = "journey2025"
        val containerName = "journey"
        val sasToken = "sp=racwl&st=2025-10-07T12:06:43Z&se=2025-12-20T20:21:43Z&sv=2024-11-04&sr=c&sig=olO%2FAQVZv1dP2I68WhoQ3D%2BcUpAaq7H3CepabScHisg%3D"

        // 🔹 Monta a URL para upload
        val blobName = "${System.currentTimeMillis()}-$fileName"
        val baseUrl = "https://$storageAccount.blob.core.windows.net/$containerName/$blobName"
        val uploadUrl = "$baseUrl?$sasToken"

        try {
            val fileBytes = inputStream.readBytes()

            val requestBody = RequestBody.create(
                "application/octet-stream".toMediaTypeOrNull(),
                fileBytes
            )

            val request = Request.Builder()
                .url(uploadUrl)
                .put(requestBody)
                .addHeader("x-ms-blob-type", "BlockBlob")
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            return@withContext if (response.isSuccessful) {
                println("✅ Upload concluído: $baseUrl")
                baseUrl
            } else {
                println("❌ Erro no upload (${response.code}): ${response.message}")
                null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
