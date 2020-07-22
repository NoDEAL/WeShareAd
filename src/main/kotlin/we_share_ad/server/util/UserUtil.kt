package we_share_ad.server.util

import we_share_ad.server.component.User
import we_share_ad.server.database.DatabaseServer
import java.util.*

object UserUtil {
    /**
     * 사용자 정보가 담긴 캐시
     *
     * @author 김지환
     */
    private var users: List<User> = updateUsers()

    /**
     * 마지막 캐시 업데이트 millis
     *
     * @author 김지환
     */
    private var lastUpdate: Long = 0

    /**
     * 사용자 정보 캐시 업데이트
     *
     * @author 김지환
     * @return DB에 저장된 사용자 정보
     */
    private fun updateUsers(): List<User> {
        val sql = "SELECT * FROM we_share_ad.user"
        val queryId = DatabaseServer.getInstance().addQuery(sql)
        val queryResult = DatabaseServer.getInstance().getResult(queryId)

        lastUpdate = System.currentTimeMillis()
        return queryResult.content.map { User(it) }.sorted()
    }

    /**
     * 캐시에서 사용자 정보 반환.
     * 1분 이상 업데이트되지 않았다면 업데이트 후 반환
     *
     * @author 김지환
     * @param userId: 탐색할 userId
     * @param needPassword: 반환 시 비밀번호 표시 여부. true일 때 비밀번호 정상 표시
     * @return 캐시에서 탐색된 사용자 정보
     */
    fun getUser(userId: UUID, needPassword: Boolean = false): User? {
        if (lastUpdate < System.currentTimeMillis() - 60 * 1000) {
            users = updateUsers()
        }

        val index = users.binarySearch { it.userId.compareTo(userId) }
        return if (index >= 0) {
            if (!needPassword) {
                users[index].password = ""
            }
            users[index]
        } else {
            null
        }
    }
}