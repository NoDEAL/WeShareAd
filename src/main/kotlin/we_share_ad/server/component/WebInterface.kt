package we_share_ad.server.component

import we_share_ad.server.util.json.model.JSONContent

interface WebInterface: JSONContent {
    fun getFilePath(): String
}