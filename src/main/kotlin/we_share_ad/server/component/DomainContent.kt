package we_share_ad.server.component

import org.json.JSONObject
import we_share_ad.server.database.component.Row
import we_share_ad.server.util.json.model.JSONParsable
import java.util.*

class DomainContent: JSONParsable, IDContent, Comparable<DomainContent> {
    lateinit var userId: UUID
    lateinit var domainId: UUID
    lateinit var domain: String
    var viewCount = 0
    var clickCount = 0

    constructor(jsonObject: JSONObject): super(jsonObject)

    constructor(sqlRow: Row) {
        this.userId = sqlRow.getUUID("user_id")
        this.domainId = sqlRow.getUUID("domain_id")
        this.domain = sqlRow.getString("domain")
        this.viewCount = sqlRow.getInt("view_count")
        this.clickCount = sqlRow.getInt("click_count")

        updateJSONObject()
    }

    constructor(userId: UUID, domainId: UUID, domain: String, viewCount: Int, clickCount: Int) {
        this.userId = userId
        this.domainId = domainId
        this.domain = domain
        this.viewCount = viewCount
        this.clickCount = clickCount

        updateJSONObject()
    }

    fun getInsertSql() = "INSERT INTO we_share_ad.domain (user_id, domain_id, domain, view_count, click_count) VALUES (" +
            "'$userId', '$domainId', '$domain', $viewCount, $clickCount)"

    fun getUpdateSql() = "UPDATE we_share_ad.domain SET domain='$domain', view_count=$viewCount, click_count=$clickCount " +
            "WHERE domain_id='$domainId'"

    fun getUpdateSqlWithoutCount() = "UPDATE we_share_ad.domain SET domain='$domain' WHERE domain_id='$domainId'"

    fun getUpdateSqlViewCount() = "UPDATE we_share_ad.domain SET view_count=view_count+1 WHERE domain_id='$domainId'"

    fun getUpdateSqlClickCount() = "UPDATE we_share_ad.domain SET click_count=click_count+1 WHERE domain_id='$domainId'"

    override fun getUUID() = domainId

    override fun compareTo(other: DomainContent): Int {
        val firstCompare = userId.compareTo(other.userId)
        if (firstCompare != 0) {
            return firstCompare
        }

        return domainId.compareTo(other.domainId)
    }
}