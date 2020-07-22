package we_share_ad.server.component

import java.util.*

/**
 * UUID를 포함한 content의 interface
 *
 * @author 김지환
 */
interface IDContent {
    /**
     * @author 김지환
     * @return 객체가 가진 UUID 반환
     */
    fun getUUID(): UUID
}