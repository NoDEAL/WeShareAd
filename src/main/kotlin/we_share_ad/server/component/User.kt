package we_share_ad.server.component

import org.json.JSONObject
import we_share_ad.server.constant.AdCategory
import we_share_ad.server.database.component.Row
import we_share_ad.server.util.DomainUtil
import we_share_ad.server.util.json.model.JSONList
import we_share_ad.server.util.json.model.JSONParsable
import we_share_ad.server.util.json.model.toJSONList
import java.sql.Date
import java.util.*

/**
 * we_share_ad.user에 대응하는 객체
 *
 * @author 김지환
 */
class User: JSONParsable, IDContent, Comparable<User> {
    lateinit var userId: UUID
    lateinit var email: String
    lateinit var password: String
    lateinit var categoryFirst: AdCategory.First
    lateinit var categorySecond: List<AdCategory.Second>
    lateinit var domains: JSONList<DomainContent>
    lateinit var region: String
    var bidCurrent: Int = 0
    var bidNext: Int = 0
    var emailAgreement: Boolean = false
    var eulaVersion: Int = 0
    var privacyVersion: Int = 0
    lateinit var dateSignUp: Date
    lateinit var lastSignIn: Date

    /**
     * [JSONObject]로부터 초기화
     *
     * @author 김지환
     * @param jsonObject: 필드에 맞는 JSONObject. 필드 이름은 underscore 형식으로 작성되어야함
     */
    constructor(jsonObject: JSONObject): super(jsonObject)

    /**
     * SQL [Row]로부터 초기화
     *
     * @author 김지환
     * @param sqlRow: 광고의 _id를 제외한 모든 column의 select 결과가 있는 SQL 결과
     */
    constructor(sqlRow: Row) {
        val rawCategorySecond = sqlRow.getJSONArray("category_second")

        this.userId = sqlRow.getUUID("user_id")
        this.email = sqlRow.getString("email")
        this.password = sqlRow.getString("password")
        this.categoryFirst = AdCategory.getFirstAdCategory(sqlRow.getInt("category_first"))
        this.categorySecond = (0 until rawCategorySecond.length()).map {
            AdCategory.getSecondAdCategory(this.categoryFirst, rawCategorySecond.getInt(it))
        }
        this.domains = DomainUtil.getDomains(this.userId)
        this.region = sqlRow.getString("region")
        this.bidCurrent = sqlRow.getInt("bid_current")
        this.bidNext = sqlRow.getInt("bid_next")
        this.emailAgreement = sqlRow.getBoolean("email_agreement")
        this.eulaVersion = sqlRow.getInt("eula_version")
        this.privacyVersion = sqlRow.getInt("privacy_version")
        this.dateSignUp = sqlRow.getDate("date_sign_up")
        this.lastSignIn = sqlRow.getDate("last_sign_in")

        updateJSONObject()
    }

    /**
     * 실행중인 전달받은 값으로 초기화
     *
     * @author 김지환
     */
    constructor(userId: UUID, email: String, password: String, categoryFirst: AdCategory.First,
                categorySecond: JSONList<AdCategory.Second>, domains: JSONList<DomainContent>, region: String,
                bidCurrent: Int, bidNext: Int, emailAgreement: Boolean, eulaVersion: Int, privacyVersion: Int,
                dateSignUp: Date, lastSignIn: Date) {
        this.userId = userId
        this.email = email
        this.password = password
        this.categoryFirst = categoryFirst
        this.categorySecond = categorySecond
        this.domains = domains
        this.region = region
        this.bidCurrent = bidCurrent
        this.bidNext = bidNext
        this.emailAgreement = emailAgreement
        this.eulaVersion = eulaVersion
        this.privacyVersion = privacyVersion
        this.dateSignUp = dateSignUp
        this.lastSignIn = lastSignIn

        updateJSONObject()
    }

    override fun getUUID() = userId

    override fun compareTo(other: User) = userId.compareTo(other.userId)

    fun getInsertSql() = "INSERT INTO we_share_ad.user (user_id, email, password, category_first, " +
            "category_second, region, bid_current, bid_next, email_agreement, eula_version, privacy_version, " +
            "date_sign_up, last_sign_in) values ('$userId', '$email', '$password', $categoryFirst, '$categorySecond', " +
            "'$region', $bidCurrent, $bidNext, $emailAgreement, $eulaVersion, $privacyVersion, '$dateSignUp', " +
            "'$lastSignIn')"

    fun getUpdateSql() = "UPDATE we_share_ad.user SET email='$email', password='$password', " +
            "category_first=$categoryFirst, category_second='$categorySecond', region='$region', " +
            "bid_current=$bidCurrent, bid_next=$bidNext, email_agreement=$emailAgreement, eula_version=$eulaVersion, " +
            "privacy_version=$privacyVersion, date_sign_up='$dateSignUp', last_sign_in=$lastSignIn " +
            "WHERE user_id='$userId'"
}