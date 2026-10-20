package com.krecsa.sublurk

import com.krecsa.sublurk.data.SubdomainParser
import com.krecsa.sublurk.data.network.CrtShEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class SubdomainParserTest {

    @Test
    fun `extractSubdomains filters emails and invalid records`() {
        val domain = "mail.ru"
        val entries = listOf(
            CrtShEntry(
                issuerCaId = null, issuerName = null, commonName = "mail.ru",
                nameValue = "mail.ru\nwww.mail.ru\napi.mail.ru",
                id = null, entryTimestamp = null, notBefore = null, notAfter = null
            ),
            CrtShEntry(
                issuerCaId = null, issuerName = null, commonName = "test",
                nameValue = "user@mail.ru\nadmin@mail.ru",
                id = null, entryTimestamp = null, notBefore = null, notAfter = null
            ),
            CrtShEntry(
                issuerCaId = null, issuerName = null, commonName = "192.168.1.1",
                nameValue = "192.168.1.1",
                id = null, entryTimestamp = null, notBefore = null, notAfter = null
            ),
            CrtShEntry(
                issuerCaId = null, issuerName = null, commonName = "*.dev.mail.ru",
                nameValue = "*.dev.mail.ru\ndev.mail.ru\n*.corp.mail.ru\ncorp.mail.ru",
                id = null, entryTimestamp = null, notBefore = null, notAfter = null
            )
        )

        val result = SubdomainParser.extractSubdomains(entries, domain)

        val expected = listOf(
            "api.mail.ru",
            "corp.mail.ru",
            "dev.mail.ru",
            "www.mail.ru"
        )

        assertEquals(expected, result)
    }

    @Test
    fun `extractSubdomains returns empty list for empty input`() {
        val result = SubdomainParser.extractSubdomains(emptyList(), "mail.ru")
        assertEquals(emptyList<String>(), result)
    }
}