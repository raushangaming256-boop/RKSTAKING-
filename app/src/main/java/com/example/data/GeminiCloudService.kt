package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

// Match exact JSON payloads required by Gemini API v1beta
data class Part(val text: String)
data class Content(val parts: List<Part>)
data class GenerateContentRequest(val contents: List<Content>)

data class Candidate(val content: Content)
data class GenerateContentResponse(val candidates: List<Candidate>)

object GeminiCloudService {
    private const val MODEL_NAME = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val requestAdapter = moshi.adapter(GenerateContentRequest::class.java)
    private val responseAdapter = moshi.adapter(GenerateContentResponse::class.java)

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getBusinessInsights(
        customers: List<CustomerEntity>,
        transactions: List<TransactionEntity>,
        userEmail: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "⚠️ Google Cloud API Key is missing! Please configure GEMINI_API_KEY in your AI Studio secrets panel to activate Smart Cloud Ledger Insights."
        }

        // Prepare ledger context for AI
        val ledgerSummaryStringBuilder = java.lang.StringBuilder()
        ledgerSummaryStringBuilder.append("Here's my business ledger details:\n")
        
        customers.forEach { cust ->
            val txs = transactions.filter { it.customerId == cust.id }
            val balance = txs.sumOf { if (it.isCredit) it.amount else -it.amount }
            val status = if (balance > 0) "owes me ₹$balance (Due/Udhaar)" else if (balance < 0) "I owe them ₹${kotlin.math.abs(balance)} (Advance)" else "Settled"
            ledgerSummaryStringBuilder.append("- Customer Name: ${cust.name}, Phone: ${cust.phone}, Status: $status, Total transactions: ${txs.size}\n")
        }

        val prompt = """
            You are a helpful local commercial business advisor for a small business merchant using the OkKhata ledger book.
            Analyze the following shop ledger statistics and write a helpful business summary for the merchant.
            
            ${ledgerSummaryStringBuilder.toString()}
            
            Based on this:
            1. Write a 1-sentence welcome greeting for $userEmail praising their management.
            2. High-level cash flow feedback (e.g. how much money is outstanding to collect, status of advances).
            3. Pick the top 2 customers with highest credit/dues who might need a soft polite reminder.
            4. Provide 1 quick actionable business tip on minimizing customer bad debts.
            
            Write your response in clean, beautiful Hinglish with some Hindi words (written in Latin/English characters) so it's simple to understand for an Indian shop keeper. Use bullet points and emoji icons. Keep it concise.
        """.trimIndent()

        try {
            val reqPayload = GenerateContentRequest(listOf(Content(listOf(Part(prompt)))))
            val jsonBody = requestAdapter.toJson(reqPayload)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "Google Cloud Service Response Failed. Please contact ledger support or configure correct secrets."
                }
                val rawBody = response.body?.string() ?: ""
                val resObj = responseAdapter.fromJson(rawBody)
                return@withContext resObj?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Unable to parse Google Cloud AI summary."
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Failed to connect to Google Cloud Vertex Service: ${e.message}"
        }
    }

    suspend fun generatePoliteReminder(
        customerName: String,
        amount: Double,
        phone: String,
        shopName: String = "",
        upiId: String = ""
    ): String = withContext(Dispatchers.IO) {
        val displayShop = if (shopName.isBlank()) "OkKhata Merchant Store" else shopName
        val upiInfo = if (upiId.isNotBlank()) "\nPay via UPI ID: $upiId" else ""

        val defaultMsg = """
            💼 *OFFICIAL PAYMENT ADVICE*
            🏪 *$displayShop*
            ━━━━━━━━━━━━━━━━━━━
            Client: *$customerName*
            
            📌 *Outstanding Ledger Due: ₹${String.format(java.util.Locale.US, "%.2f", amount)}*
            📋 *Status: Pending Clearance*${if (upiId.isNotBlank()) "\n\n📲 *Direct UPI Settlement:* `$upiId`\n_(GPay / PhonePe / Paytm / BHIM)_" else ""}
            
            Please process the settlement payment to keep your commercial ledger account updated.
            ━━━━━━━━━━━━━━━━━━━
            _Billing & Accounts Department_
            *$displayShop*
        """.trimIndent()

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext defaultMsg
        }

        val prompt = """
            Generate an executive, authoritative, highly professional merchant payment notice from business '$displayShop' to client '$customerName' for an outstanding ledger due of Rs. $amount.
            Include UPI ID ($upiId) if provided.
            Tone requirement:
            - Professional, dignified, firm, and authoritative business communication (like a corporate or banking ledger invoice reminder).
            - DO NOT beg or plead (no "kripya reham karein", no desperate repeated begging).
            - State the invoice amount clearly and request prompt settlement for active account maintenance.
            
            Provide 2 executive options:
            Option 1 (Professional Hinglish): Firm, dignified business settlement notice.
            Option 2 (Corporate English): Clean commercial ledger settlement advice.
            
            Strictly format as clean readable text without markdown codeblocks or technical jargon.
        """.trimIndent()

        try {
            val reqPayload = GenerateContentRequest(listOf(Content(listOf(Part(prompt)))))
            val jsonBody = requestAdapter.toJson(reqPayload)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext defaultMsg
                }
                val rawBody = response.body?.string() ?: ""
                val resObj = responseAdapter.fromJson(rawBody)
                return@withContext resObj?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: defaultMsg
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext defaultMsg
        }
    }
}
