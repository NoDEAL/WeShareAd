package we_share_ad.server.util

import we_share_ad.server.component.DomainContent
import we_share_ad.server.database.DatabaseServer
import we_share_ad.server.util.json.model.JSONList
import we_share_ad.server.util.json.model.toJSONList
import java.util.*

object DomainUtil {
    private var domains: List<DomainContent> = updateDomain()

    private var lastUpdate: Long = 0

    private fun updateDomain(): List<DomainContent> {
        val sql = "SELECT * FROM we_share_ad.domain"
        val queryId = DatabaseServer.getInstance().addQuery(sql)
        val queryResult = DatabaseServer.getInstance().getResult(queryId)

        lastUpdate = System.currentTimeMillis()
        return queryResult.content.map { DomainContent(it) }.sorted()
    }

    private fun ensureUpdate() {
        if (lastUpdate < System.currentTimeMillis() - 60 * 1000) {
            domains = updateDomain()
        }
    }

    fun getDomain(domainId: UUID): DomainContent? {
        ensureUpdate()

        val index = domains.binarySearch { it.domainId.compareTo(domainId) }
        return if (index >= 0) {
            domains[index]
        } else {
            null
        }
    }

    fun getDomain(domain: String): DomainContent? {
        ensureUpdate()

        val index = domains.binarySearch { it.domain.compareTo(domain) }
        return if (index >= 0) {
            domains[index]
        } else {
            null
        }
    }

    fun getDomains(userId: UUID): JSONList<DomainContent> {
        ensureUpdate()

        return domains.filter { it.userId == userId }.toJSONList()
    }
}