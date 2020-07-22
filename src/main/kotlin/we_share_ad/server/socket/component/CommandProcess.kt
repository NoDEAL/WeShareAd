package we_share_ad.server.socket.component

import org.json.JSONObject
import we_share_ad.server.component.AdContent
import we_share_ad.server.component.DomainContent
import we_share_ad.server.component.User
import we_share_ad.server.constant.AdCategory
import we_share_ad.server.constant.AdSize
import we_share_ad.server.constant.Command
import we_share_ad.server.constant.Command.*
import we_share_ad.server.constant.ServerResponse
import we_share_ad.server.database.DatabaseServer
import we_share_ad.server.socket.Connection
import we_share_ad.server.util.AdUtil
import we_share_ad.server.util.DomainUtil
import we_share_ad.server.util.UserUtil
import we_share_ad.server.util.json.model.JSONList
import we_share_ad.server.util.json.model.toJSONList
import java.sql.Date
import java.util.*
import kotlin.random.Random

object CommandProcess {
    fun onReceived(command: CommandContent): JSONObject {
        return when (Command.findCommand(command.name)) {
            CLOSE -> JSONObject() // Never called

            ADD_USER -> addUser(command)
            GET_USER -> getUser(command)
            UPDATE_USER -> updateUser(command)
            DELETE_USER -> deleteUser(command)
            SIGN_IN -> signIn(command)

            ADD_AD -> addAd(command)
            GET_AD -> getAd(command)
            UPDATE_AD -> updateAd(command)
            DELETE_AD -> deleteAd(command)
            COUNT_AD_VIEW -> countAdView(command)
            COUNT_AD_CLICK -> countAdClick(command)

            ADD_DOMAIN -> addDomain(command)
            GET_DOMAIN -> getDomain(command)
            GET_BY_DOMAIN -> getByDomain(command)
            UPDATE_DOMAIN -> updateDomain(command)
            DELETE_DOMAIN -> deleteDomain(command)
            COUNT_DOMAIN_VIEW -> countDomainView(command)
            COUNT_DOMAIN_CLICK -> countDomainClick(command)
        }
    }

    /**
     * 사용자 추가
     *
     * @author 김지환
     * @param command: add_user 명령. 인수로 다음이 포함되어야 함: 이메일, 비밀번호, 첫번째 카테고리, 두번째 카테고리 목록, 국가코드,
     *                 이메일 수신 동의 여부, 최종 사용자 약관 동의 버전, 개인정보 처리방침 동의 버전
     * @return 사용자 추가 성공 여부
     */
    private fun addUser(command: CommandContent): JSONObject {
        val email = command.arguments.getString("email")
        val password = command.arguments.getString("password")
        val categoryFirst = AdCategory.getFirstAdCategory(command.arguments.getInt("category_first"))
        val categorySecond = command.arguments.getJSONArray("category_second").toJSONList<AdCategory.Second>()
        val region = command.arguments.getString("region")
        val emailAgreement = command.arguments.getBoolean("email_agreement")
        val eulaVersion = command.arguments.getInt("eula_version")
        val privacyVersion = command.arguments.getInt("privacy_version")
        val userId = UUID.randomUUID()
        val bidCurrent = 0
        val bidNext = 0
        val dateSignUp = Date(System.currentTimeMillis())
        val lastSignIn = Date(System.currentTimeMillis())

        val sql = User(
                userId = userId,
                email = email,
                password = password,
                categoryFirst = categoryFirst,
                categorySecond = categorySecond,
                domains = JSONList(),
                region = region,
                bidCurrent = bidCurrent,
                bidNext = bidNext,
                emailAgreement = emailAgreement,
                eulaVersion = eulaVersion,
                privacyVersion = privacyVersion,
                dateSignUp = dateSignUp,
                lastSignIn = lastSignIn
        ).getInsertSql()
        // INSERT not need result
        DatabaseServer.getInstance().addQuery(sql)

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 사용자 탐색
     *
     * @author 김지환
     * @param command: get_user 명령. 인수로 다음이 포함되어야 함: 사용자 고유 ID
     * @return 탐색된 [User] 또는 오류 코드
     * @see ServerResponse.USER_ID_NOT_FOUND: 사용자 고유 ID가 존재하지 않을 때
     */
    private fun getUser(command: CommandContent): JSONObject {
        val userId = UUID.fromString(command.arguments.getString("user_id"))
        val found = UserUtil.getUser(userId, true)

        return if (found == null) {
            Connection.makeNegativeResponse(command.uuid, ServerResponse.USER_ID_NOT_FOUND.ordinal)
        } else {
            Connection.makePositiveResponse(command.uuid, JSONObject().apply { put("user", found.toJSONObject()) })
        }
    }

    /**
     * 사용자 정보 갱신
     *
     * @author 김지환
     * @param command: update_user 명령. 인수로 다음이 포함되어야 함: 갱신될 내용이 적용된 [User] 전체 정보
     * @return 사용자 정보 갱신 성공 여부
     */
    private fun updateUser(command: CommandContent): JSONObject {
        val user = User(command.arguments.getJSONObject("user"))
        val sql = user.getUpdateSql()
        // UPDATE not need result
        DatabaseServer.getInstance().addQuery(sql)

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 사용자 삭제
     *
     * @author 김지환
     * @param command: delete_user 명령. 인수로 다음이 포함되어야 함: 사용자 고유 ID
     * @return 사용자 삭제 성공 여부
     */
    private fun deleteUser(command: CommandContent): JSONObject {
        val userId = command.arguments.getString("user_id")
        val sql = "DELETE FROM we_share_ad.user WHERE user_id='$userId'"
        // DELETE not need result
        DatabaseServer.getInstance().addQuery(sql)

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 로그인
     *
     * @author 김지환
     * @param command: sign_in 명령. 인수로 다음이 포함되어야 함: 이메일, 비밀번호
     * @return 로그인 대상 [User] 또는 오류 코드
     * @see ServerResponse.EMAIL_NOT_FOUND: 이메일이 존재하지 않거나 비밀번호가 일치하지 않을 때
     */
    private fun signIn(command: CommandContent): JSONObject {
        val email = command.arguments.getString("email")
        val password = command.arguments.getString("password")
        val sql = "SELECT * FROM we_share_ad.user WHERE email='$email' AND password='$password'"
        val queryId = DatabaseServer.getInstance().addQuery(sql)
        val queryResult = DatabaseServer.getInstance().getResult(queryId)

        return if (queryResult.getRowCount() == 0) {
            Connection.makeNegativeResponse(command.uuid, ServerResponse.EMAIL_NOT_FOUND.ordinal)
        } else {
            Connection.makePositiveResponse(command.uuid, User(queryResult.getRow(0)).toJSONObject())
        }
    }

    /**
     * 광고 추가
     *
     * @author 김지환
     * @param command: add_ad 명령. 인수로 다음이 포함되어야 함: 사용자 고유 ID, 광고 첫번째 카테고리, 광고 두번째 카테고리, 광고 크기,
     *                                                   클릭 시 이동 대상 URL
     * @return 추가된 광고 고유 ID
     */
    private fun addAd(command: CommandContent): JSONObject {
        val userId = UUID.fromString(command.arguments.getString("user_id"))
        val categoryFirst = AdCategory.getFirstAdCategory(command.arguments.getInt("category_first"))
        val categorySecond = AdCategory.getSecondAdCategory(categoryFirst, command.arguments.getInt("category_second"))
        val size = AdSize.findSize(command.arguments.getInt("size"))
        val urlDest = command.arguments.getString("url_dest")
        val adId = UUID.randomUUID()
        val viewCount = 0
        val clickCount = 0

        val adContent = AdContent(userId, adId, categoryFirst, categorySecond, size, viewCount, clickCount, urlDest)
        DatabaseServer.getInstance().addQuery(adContent.getInsertSql())

        return Connection.makePositiveResponse(command.uuid, JSONObject().apply { put("ad_id", adId.toString()) })
    }

    /**
     * 광고 탐색
     *
     * @author 김지환
     * @param command: get_ad 명령. 인수로 다음이 포함되어야 함: 사용자 고유 ID
     * @return 탐색된 광고 또는 오류 코드
     * @see ServerResponse.USER_ID_NOT_FOUND: 사용자 고유 ID가 탐색되지 않았을 때
     * @see ServerResponse.AD_NOT_FOUND: 해당하는 광고가 존재하지 않을 때
     */
    private fun getAd(command: CommandContent): JSONObject {
        val userId = UUID.fromString(command.arguments.getString("user_id"))
        // userId를 바탕으로 사용자 정보 받아옴. 1분 이내에 삽입된 정보는 탐색되지 않음
        // userId가 탐색되지 않을 때 USER_ID_NOT_FOUND 반환
        val user = UserUtil.getUser(userId)
                ?: return Connection.makeNegativeResponse(command.uuid, ServerResponse.USER_ID_NOT_FOUND.ordinal)
        // 요청한 사용자에게 맞는 카테고리 전체 탐색
        val ads = AdUtil.getAds(user.categoryFirst)
                .filter { it.userId != user.userId }
                .filter { user.categorySecond.contains(it.categorySecond) }
        if (ads.isEmpty()) {
            return Connection.makeNegativeResponse(command.uuid, ServerResponse.AD_NOT_FOUND.ordinal)
        }

        // 카테고리에 해당하는 광고 중 랜덤 반환
        val arguments = JSONObject().apply { put("ad", ads[Random.nextInt(ads.size)].toJSONObject()) }

        return Connection.makePositiveResponse(command.uuid, arguments)
    }

    /**
     * 광고 정보 갱신
     *
     * @author 김지환
     * @param command: update_ad 명령. 인수로 다음이 포함되어야 함: 갱신될 내용이 적용된 [AdContent] 전체 정보
     * @return 광고 정보 갱신 성공 여부
     */
    private fun updateAd(command: CommandContent): JSONObject {
        val ad = AdContent(command.arguments.getJSONObject("ad"))
        DatabaseServer.getInstance().addQuery(ad.getUpdateSql())

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 광고 삭제
     *
     * @author 김지환
     * @param command: delete_ad 명령. 인수로 다음이 포함되어야 함: 광고 고유 ID, 광고 첫번째 카테고리
     * @return 광고 삭제 성공 여부
     */
    private fun deleteAd(command: CommandContent): JSONObject {
        val categoryFirst = AdCategory.getFirstAdCategory(command.arguments.getInt("category_first"))
        val adId = command.arguments.getInt("ad_id")
        val sql = "DELETE FROM we_share_ad.ad_${categoryFirst.categoryId} WHERE ad_id='$adId'"
        // DELETE not need result
        DatabaseServer.getInstance().addQuery(sql)

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 광고 노출 횟수 증가
     *
     * @author 김지환
     * @param command: count_ad_view 명령. 인수로 다음이 포함되어야 함: 광고 고유 ID
     * @return 광고 노출 횟수 증가 성공 여부 또는 오류 코드
     * @see ServerResponse.AD_NOT_FOUND: 광고 고유 ID가 존재하지 않을 때
     */
    private fun countAdView(command: CommandContent): JSONObject {
        val adId = UUID.fromString(command.arguments.getString("ad_id"))
        val firstCategory = AdUtil.getFirstCategory(adId)
                ?: return Connection.makeNegativeResponse(command.uuid, ServerResponse.AD_NOT_FOUND.ordinal)
        val sql = "UPDATE we_share_ad.ad_${firstCategory.categoryId} SET view_count=view_count+1 WHERE ad_id='$adId'"
        // UPDATE not need result
        DatabaseServer.getInstance().addQuery(sql)

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 광고 클릭 횟수 증가
     *
     * @author 김지환
     * @param command: count_ad_click 명령. 인수로 다음이 포함되어야 함: 광고 고유 ID
     * @return 광고 클릭 횟수 증가 성공 여부 또는 오류 코드
     * @see ServerResponse.AD_NOT_FOUND: 광고 고유 ID가 존재하지 않을 때
     */
    private fun countAdClick(command: CommandContent): JSONObject {
        val adId = UUID.fromString(command.arguments.getString("ad_id"))
        val firstCategory = AdUtil.getFirstCategory(adId)
                ?: return Connection.makeNegativeResponse(command.uuid, ServerResponse.AD_NOT_FOUND.ordinal)
        val sql = "UPDATE we_share_ad.ad_${firstCategory.categoryId} SET click_count=click_count+1 WHERE ad_id='$adId'"
        // UPDATE not need result
        DatabaseServer.getInstance().addQuery(sql)

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 도메인 추가
     *
     * @author 김지환
     * @param command: add_domain 명령. 인수로 다음이 포함되어야 함: 사용자 고유 ID, 도메인
     * @return 추가된 [DomainContent] 전체 정보
     */
    private fun addDomain(command: CommandContent): JSONObject {
        val userId = UUID.fromString(command.arguments.getString("user_id"))
        val domain = command.arguments.getString("domain")
        val domainId = UUID.randomUUID()
        val viewCount = 0
        val clickCount = 0

        val domainContent = DomainContent(userId, domainId, domain, viewCount, clickCount)
        DatabaseServer.getInstance().addQuery(domainContent.getInsertSql())

        return Connection.makePositiveResponse(command.uuid, JSONObject().apply { put("domain", domainContent.toJSONObject()) })
    }

    /**
     * 도메인 탐색 - domainId
     *
     * @author 김지환
     * @param command: get_domain 명령. 인수로 다음이 포함되어야 함: 도메인 고유 ID
     * @return 탐색된 [DomainContent] 전체 정보 또는 오류 코드
     * @see ServerResponse.DOMAIN_NOT_FOUND
     */
    private fun getDomain(command: CommandContent): JSONObject {
        val domainId = UUID.fromString(command.arguments.getString("domain_id"))
        val domain = DomainUtil.getDomain(domainId)
                ?: return Connection.makeNegativeResponse(command.uuid, ServerResponse.DOMAIN_NOT_FOUND.ordinal)

        return Connection.makePositiveResponse(command.uuid, JSONObject().apply { put("domain", domain.toJSONObject()) })
    }

    /**
     * 도메인 탐색 - domain
     *
     * @author 김지환
     * @param command: get_by_domain 명령. 인수로 다음이 포함되어야 함: 도메인
     * @return 탐색된 [DomainContent] 전체 정보 또는 오류 코드
     * @see ServerResponse.DOMAIN_NOT_FOUND
     */
    private fun getByDomain(command: CommandContent): JSONObject {
        val domain = command.arguments.getString("domain")
        val domainContent = DomainUtil.getDomain(domain)
                ?: return Connection.makeNegativeResponse(command.uuid, ServerResponse.DOMAIN_NOT_FOUND.ordinal)

        return Connection.makePositiveResponse(command.uuid, JSONObject().apply { put("domain", domainContent.toJSONObject()) })
    }

    /**
     * 도메인 정보 갱신
     *
     * @author 김지환
     * @param command: update_domain 명령. 인수로 다음이 포함되어야 함: 갱신될 정보가 적용된 [DomainContent]
     * @return 도메인 정보 갱신 성공 여부
     */
    private fun updateDomain(command: CommandContent): JSONObject {
        val domain = DomainContent(command.arguments.getJSONObject("domain"))
        DatabaseServer.getInstance().addQuery(domain.getUpdateSql())

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 도메인 삭제
     *
     * @author 김지환
     * @param command: delete_domain 명령. 인수로 다음이 포함되어야 함: 도메인 고유 ID
     * @return 도메인 삭제 성공 여부
     */
    private fun deleteDomain(command: CommandContent): JSONObject {
        val domainId = UUID.fromString(command.arguments.getString("domain_id"))
        val sql = "DELETE FROM we_share_ad.domain WHERE domain_id='$domainId'"
        DatabaseServer.getInstance().addQuery(sql)

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 도메인이 포함된 사이트에서 노출된 광고 수 증가
     *
     * @author 김지환
     * @param command: count_domain_view 명령. 인수로 다음이 포함되어야 함: 도메인
     * @return 도메인 광고 노출 증가 성공 여부 또는 오류 코드
     * @see ServerResponse.DOMAIN_NOT_FOUND: 도메인이 존재하지 않을 때
     */
    private fun countDomainView(command: CommandContent): JSONObject {
        val domain = command.arguments.getString("domain")
        val sql = DomainUtil.getDomain(domain)?.getUpdateSqlViewCount()
                ?: return Connection.makeNegativeResponse(command.uuid, ServerResponse.DOMAIN_NOT_FOUND.ordinal)
        DatabaseServer.getInstance().addQuery(sql)

        return Connection.makePositiveResponse(command.uuid)
    }

    /**
     * 도메인이 포함된 사이트에서 클릭된 광고 수 증가
     *
     * @author 김지환
     * @param command: count_domain_click 명령. 인수로 다음이 포함되어야 함: 도메인
     * @return 도메인 광고 클릭 증가 성공 여부 또는 오류 코드
     * @see ServerResponse.DOMAIN_NOT_FOUND: 도메인이 존재하지 않을 때
     */
    private fun countDomainClick(command: CommandContent): JSONObject {
        val domain = command.arguments.getString("domain")
        val sql = DomainUtil.getDomain(domain)?.getUpdateSqlClickCount()
                ?: return Connection.makeNegativeResponse(command.uuid, ServerResponse.DOMAIN_NOT_FOUND.ordinal)
        DatabaseServer.getInstance().addQuery(sql)

        return Connection.makePositiveResponse(command.uuid)
    }
}