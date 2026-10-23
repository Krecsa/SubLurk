package com.krecsa.sublurk.data.repository

import com.krecsa.sublurk.data.SubdomainParser
import com.krecsa.sublurk.data.network.CertSpotterApi
import com.krecsa.sublurk.data.network.CrtShApi
import com.krecsa.sublurk.data.local.SubdomainDao
import com.krecsa.sublurk.data.local.SubdomainEntity
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubdomainRepository @Inject constructor(
    private val crtShApi: CrtShApi,
    private val certSpotterApi: CertSpotterApi,
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

        val crtResult = fetchFromCrtShWithRetry(domain)
        if (crtResult.isSuccess) {
            val subdomains = crtResult.getOrThrow()
            saveToCache(domain, subdomains)
            return Result.success(subdomains)
        }

        val certSpotterResult = fetchFromCertSpotter(domain)
        if (certSpotterResult.isSuccess) {
            val subdomains = certSpotterResult.getOrThrow()
            saveToCache(domain, subdomains)
            return Result.success(subdomains)
        }

        val cached = dao.getByDomain(domain)
        if (cached.isNotEmpty()) {
            return Result.success(cached)
        }

        return Result.failure(Exception("All sources are unavailable. Try again later."))
    }

    private suspend fun fetchFromCrtShWithRetry(domain: String): Result<List<String>> {
        val maxAttempts = 3
        var lastError: Exception? = null

        repeat(maxAttempts) { attempt ->
            try {
                val response = crtShApi.search("%.$domain")

                if (response.isSuccessful) {
                    val entries = response.body().orEmpty()
                    val subdomains = SubdomainParser.extractSubdomains(entries, domain)
                    return Result.success(subdomains)
                }

                if (response.code() in listOf(502, 503, 504)) {
                    lastError = Exception("crt.sh overloaded (${response.code()})")
                    delay(1000L * (attempt + 1))
                    return@repeat
                }

                return Result.failure(Exception("crt.sh: ${response.code()}"))
            } catch (e: Exception) {
                lastError = e
                delay(1000L * (attempt + 1))
            }
        }

        return Result.failure(lastError ?: Exception("crt.sh unavailable"))
    }

    private suspend fun fetchFromCertSpotter(domain: String): Result<List<String>> {
        return try {
            val response = certSpotterApi.getIssuances(domain)
            if (response.isSuccessful) {
                val issuances = response.body().orEmpty()
                val subdomains = issuances
                    .flatMap { it.dnsNames.orEmpty() }
                    .map { it.trim().lowercase() }
                    .filter { it.endsWith(".$domain") }
                    .map { it.removePrefix("*.") }
                    .filter { !it.contains("@") }
                    .distinct()
                    .sorted()

                Result.success(subdomains)
            } else {
                Result.failure(Exception("Cert Spotter: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveToCache(domain: String, subdomains: List<String>) {
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
    }
}