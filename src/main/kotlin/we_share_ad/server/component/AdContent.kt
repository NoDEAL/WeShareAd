package we_share_ad.server.component

import org.json.JSONObject
import we_share_ad.server.constant.AdCategory
import we_share_ad.server.constant.AdSize
import we_share_ad.server.database.component.Row
import we_share_ad.server.util.json.model.JSONParsable
import java.util.*

/**
 * we_share_ad.ad_(N)에 대응하는 객체.
 * CREATE SQL: CREATE TABLE `we_share_ad`.`ad_1` ( `_id` INT NOT NULL AUTO_INCREMENT , `user_id` CHAR(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL , `ad_id` CHAR(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL , `category_second` INT NOT NULL , `size` INT NOT NULL , `view_count` INT NOT NULL , `click_count` INT NOT NULL , `url_dest` VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL , PRIMARY KEY (`_id`)) ENGINE = InnoDB CHARSET=utf8 COLLATE utf8_general_ci;
 *
 * @author 김지환
 */
class AdContent: JSONParsable, IDContent, Comparable<AdContent> {
    lateinit var userId: UUID
    lateinit var adId: UUID
    lateinit var categoryFirst: AdCategory.First
    lateinit var categorySecond: AdCategory.Second
    lateinit var size: AdSize
    var viewCount: Int = 0
    var clickCount: Int = 0
    lateinit var urlDest: String

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
    constructor(sqlRow: Row, first: AdCategory.First) {
        this.userId = sqlRow.getUUID("user_id")
        this.adId = sqlRow.getUUID("ad_id")
        this.categoryFirst = first
        this.categorySecond = AdCategory.getSecondAdCategory(first, sqlRow.getInt("category_second"))
        this.size = AdSize.findSize(sqlRow.getInt("size"))
        this.viewCount = sqlRow.getInt("view_count")
        this.clickCount = sqlRow.getInt("click_count")
        this.urlDest = sqlRow.getString("url_dest")

        updateJSONObject()
    }

    /**
     * 실행중인 전달받은 값으로 초기화
     *
     * @author 김지환
     */
    constructor(userId: UUID, imageId: UUID, categoryFirst: AdCategory.First, categorySecond: AdCategory.Second,
                size: AdSize, viewCount: Int, clickCount: Int, urlDest: String) {
        this.userId = userId
        this.adId = imageId
        this.categoryFirst = categoryFirst
        this.categorySecond = categorySecond
        this.size = size
        this.viewCount = viewCount
        this.clickCount = clickCount
        this.urlDest = urlDest

        updateJSONObject()
    }

    override fun getUUID() = adId

    fun getInsertSql() = "INSERT INTO we_share_ad.ad_${categoryFirst.categoryId} (user_id, ad_id, category_second, " +
            "size, view_count, click_count, url_dest) VALUE ('$userId', '$adId', ${categorySecond.categoryId}, " +
            "${size.id}, $viewCount, $clickCount, '$urlDest')"

    fun getUpdateSql() = "UPDATE we_share_ad.ad_${categoryFirst.categoryId} SET category_second=$categorySecond, size=$size, " +
            "view_count=$viewCount, click_count=$clickCount, url_dest=$urlDest"

    /**
     * AdContent간의 비교
     * 상위 카테고리를 비교한 다음 상위 카테고리가 같을 때 [adId]를 통해 비교
     *
     * @author 김지환
     * @param other: 비교 대상 [AdContent]
     * @return 비교 결과
     */
    override fun compareTo(other: AdContent): Int {
        val firstCompare = categoryFirst.categoryId.compareTo(other.categoryFirst.categoryId)
        if (firstCompare != 0) {
            return firstCompare
        }

        return adId.compareTo(other.adId)
    }
}