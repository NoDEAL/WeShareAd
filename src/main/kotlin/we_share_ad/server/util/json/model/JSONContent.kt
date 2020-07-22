package we_share_ad.server.util.json.model

import org.json.JSONObject

interface JSONContent {
    fun toJSONObject(): JSONObject

    fun updateJSONObject() {}
}