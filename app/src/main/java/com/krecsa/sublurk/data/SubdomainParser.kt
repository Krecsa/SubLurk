package com.krecsa.sublurk.data

import com.krecsa.sublurk.data.network.CrtShEntry

object SubdomainParser {

    fun extractSubdomains(entries: List<CrtShEntry>, domain: String): List<String> {
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