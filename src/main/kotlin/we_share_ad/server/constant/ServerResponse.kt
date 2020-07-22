package we_share_ad.server.constant

import org.json.JSONObject
import we_share_ad.server.component.WebInterface

enum class ServerResponse: WebInterface {
    /*
     * 서버 공통 응답
     */
    NO_SUCH_COMMAND,

    /*
     * User related response
     */
    USER_ID_NOT_FOUND,
    EMAIL_NOT_FOUND,
    PASSWORD_MISMATCH,

    /*
     * Ad related response
     */
    AD_NOT_FOUND,

    /*
     * Domain related response
     */
    DOMAIN_NOT_FOUND;

    override fun toJSONObject() = JSONObject().apply {
        put("code", ordinal)
        put("title", name)
    }

    override fun getFilePath() = Files.INTERFACE_SERVER_RESPONSE
}