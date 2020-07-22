package we_share_ad.server.util

import org.json.JSONArray
import we_share_ad.server.component.WebInterface
import we_share_ad.server.constant.AdCategory
import we_share_ad.server.constant.AdSize
import we_share_ad.server.constant.Command
import we_share_ad.server.constant.ServerResponse
import we_share_ad.server.util.json.saveJSONArray

object WebInterfaceUtil {
    fun exportAll() {
        exportFirstCategory()
        exportSecondCategory()
        exportServerResponse()
        exportCommand()
        exportSize()
    }

    fun exportFirstCategory() = export(AdCategory.First.values())

    fun exportSecondCategory() = export(AdCategory.Second.values())

    fun exportServerResponse() = export(ServerResponse.values())

    fun exportCommand() = export(Command.values())

    fun exportSize() = export(AdSize.values())

    private fun <T: WebInterface> export(values: Array<T>) = JSONArray().apply {
        values.forEach { put(it.toJSONObject()) }
    }.saveJSONArray(values[0].getFilePath())
}