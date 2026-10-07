package com.krecsa.sublurk.data.repository

import com.krecsa.sublurk.data.network.CrtShApi
import com.krecsa.sublurk.data.network.CrtShEntry
import com.krecsa.sublurk.data.local.SubdomainDao
import com.krecsa.sublurk.data.local.SubdomainEntity
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubdomainRepository @Inject constructor(
    private val api: CrtShApi,
    private val dao: SubdomainDao,
) {

    suspend fun getSubdomains(
        domain: String,
        forceRefresh: Boolean = false,
    ): Result<List<String>> {
        if (!forceRefresh) {
            val cached = dao.getByDomain(domain)
            if (cached.isNotEmpty()) {
                return Result.success(cached)
            }
        }

        return fetchWithRetry(domain)
    }

    private suspend fun fetchWithRetry(domain: String): Result<List<String>> {
        val maxAttempts = 3
        var lastError: Exception? = null

        repeat(maxAttempts) { attempt ->
            try {
                val response = api.search("%.$domain")

                if (response.isSuccessful) {
                    val entries = response.body().orEmpty()
                    val subdomains = extractSubdomains(entries, domain)

                    dao.deleteByDomain(domain)
                    dao.insertAll(
                        subdomains.map {
                            SubdomainEntity(
                                domain = domain,
                                subdomain = it,
                                timestamp = System.currentTimeMillis(),
                            )
                        }
                    )

                    return Result.success(subdomains)
                }

                if (response.code() in listOf(502, 503, 504)) {
                    lastError = Exception("Сервер перегружен (${response.code()})")
                    delay(1000L * (attempt + 1))
                    return@repeat
                }

                return Result.failure(Exception("Ошибка сервера: ${response.code()}"))
            } catch (e: Exception) {
                lastError = e
                delay(1000L * (attempt + 1))
            }
        }

        val cached = dao.getByDomain(domain)
        if (cached.isNotEmpty()) {
            return Result.success(cached)
        }

        return Result.failure(lastError ?: Exception("Не удалось получить данные"))
    }

    private fun extractSubdomains(
        entries: List<CrtShEntry>,
        domain: String,
    ): List<String> {
        return entries
            .flatMap { it.nameValue?.split("\n").orEmpty() }
            .map { it.trim().lowercase() }
            .filter { it.endsWith(domain) }
            .map { it.removePrefix("*.") }
            .filter { isSubdomain(it, domain) }
            .distinct()
            .sorted()
    }

    private fun isSubdomain(value: String, domain: String): Boolean {
        if (value.contains("@")) return false
        if (value.matches(Regex("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$"""))) return false
        if (!value.endsWith(".$domain")) return false
        if (value.length > 253) return false
        if (!value.matches(Regex("""^[a-z0-9]([a-z0-9-]*[a-z0-9])?(\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$"""))) return false
        return true
    }
}