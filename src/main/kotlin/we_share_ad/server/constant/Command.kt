package we_share_ad.server.constant

import org.json.JSONObject
import we_share_ad.server.component.WebInterface

/**
 * PHP에서 실행하는 명령이 포함된 enum
 *
 * @author 김지환
 * @param title: 언더스코어 형식의 실제 명령
 */
enum class Command(val title: String): WebInterface {
    // Server common
    CLOSE("close"),

    // User related commands
    ADD_USER("add_user"),
    GET_USER("get_user"),
    UPDATE_USER("update_user"),
    DELETE_USER("delete_user"),
    SIGN_IN("sign_in"),

    // Ad related commands
    ADD_AD("add_ad"),
    GET_AD("get_ad"),
    UPDATE_AD("update_ad"),
    DELETE_AD("delete_ad"),
    COUNT_AD_VIEW("count_ad_view"),
    COUNT_AD_CLICK("count_ad_click"),

    // Domain related commands
    ADD_DOMAIN("add_domain"),
    GET_DOMAIN("get_domain"),
    GET_BY_DOMAIN("get_by_domain"),
    UPDATE_DOMAIN("update_domain"),
    DELETE_DOMAIN("delete_domain"),
    COUNT_DOMAIN_VIEW("count_domain_view"),
    COUNT_DOMAIN_CLICK("count_domain_click");

    override fun toJSONObject() = JSONObject().apply {
        put("id", ordinal)
        put("title", title)
    }

    override fun getFilePath() = Files.INTERFACE_COMMANDS

    companion object {
        /**
         * 명령을 이름으로부터 검색
         *
         * @author 김지환
         * @param title: 언더스코어 형식의 명령
         * @return 탐색된 명령
         */
        fun findCommand(title: String) = values().first { it.title == title }
    }
}