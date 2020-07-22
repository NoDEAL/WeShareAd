package we_share_ad.server.util

import we_share_ad.server.component.AdContent
import we_share_ad.server.constant.AdCategory
import we_share_ad.server.database.DatabaseServer
import java.util.*
import kotlin.collections.ArrayList

object AdUtil {
    /**
     * 모든 광고에 대한 Database cache.
     * 이미지를 포함하지 않고 각 상위 카테고리에 따라, adId에 따라 정렬되어 저장.
     */
    private val adCache = AdCategory.First.values().mapTo(ArrayList()) { first: AdCategory.First ->
        val sql = "SELECT * FROM we_share_ad.ad_${first.categoryId}"
        val queryId = DatabaseServer.getInstance().addQuery(sql)
        val queryResult = DatabaseServer.getInstance().getResult(queryId)

        Cache(System.currentTimeMillis(), first, queryResult.content.map { AdContent(it, first) }.sorted())
    }.toArray(emptyArray<Cache>())

    /**
     * 광고 cache class
     *
     * @author 김지환
     * @param timestamp: 캐싱된 시각
     * @param categoryFirst: 첫번째 범주의 카테고리
     * @param data: 첫번째 범주에 해당하는 광고 목록
     */
    private data class Cache(
            var timestamp: Long,
            var categoryFirst: AdCategory.First,
            var data: List<AdContent>
    )

    /**
     * 첫번째 범주에 해당하는 모든 광고 호출
     * 캐싱된 시각이 1분이 넘을 경우 새로고침 후 반환
     * 실시간 SQL 접근 시 부하를 방지하기 위함
     *
     * @author 김지환
     * @param first: 첫번째 범주의 카테고리
     * @return 최대 1분간 새로고침이 지연된 광고 목록
     */
    fun getAds(first: AdCategory.First): List<AdContent> {
        val index = adCache.indexOfFirst { it.categoryFirst == first }

        synchronized(adCache) {
            if (adCache[index].timestamp < System.currentTimeMillis() - 60 * 1000) {
                val sql = "SELECT * FROM we_share_ad.ad_${first.categoryId}"
                val queryId = DatabaseServer.getInstance().addQuery(sql)
                val queryResult = DatabaseServer.getInstance().getResult(queryId)

                adCache[index].data = queryResult.content.map { AdContent(it, first) }.sorted()
                adCache[index].timestamp = System.currentTimeMillis()
            }
        }

        return adCache[index].data
    }

    /**
     * 첫번째 범주와 광고 ID를 통한 특정 광고 호출
     *
     * @author 김지환
     * @param first: 첫번째 범주의 카테고리
     * @param adId: 광고 ID
     * @return 탐색된 광고
     * @see [getAds]
     */
    fun getAd(first: AdCategory.First, adId: UUID): AdContent {
        val ads = getAds(first)
        return ads[ads.binarySearch { it.adId.compareTo(adId) }]
    }

    /**
     * 광고 ID만으로 특정 광고 호출
     * 모든 첫번째 범주의 카테고리에 대한 탐색이 필요하기 때문에 지연 발생 가능
     *
     * @author 김지환
     * @param adId: 광고 ID
     * @return 탐색된 광고
     * @see [getAd]
     */
    fun getAd(adId: UUID): AdContent? {
        adCache.forEach { cache ->
            val index = cache.data.binarySearch { it.adId.compareTo(adId) }
            if (index >= 0) {
                return cache.data[index]
            }
        }

        return null
    }

    fun getFirstCategory(adId: UUID): AdCategory.First? {
        adCache.forEach { cache ->
            val index = cache.data.binarySearch { it.adId.compareTo(adId) }
            if (index >= 0) {
                return cache.data[index].categoryFirst
            }
        }

        return null
    }
}