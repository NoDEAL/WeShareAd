package we_share_ad.server.socket.component

import org.json.JSONObject
import we_share_ad.server.util.json.model.JSONParsable
import java.util.*

class CommandContent: JSONParsable {
    lateinit var uuid: UUID
    lateinit var name: String
    lateinit var arguments: JSONObject

    constructor(jsonObject: JSONObject): super(jsonObject)

    constructor(uuid: UUID, name: String, arguments: JSONObject) {
        this.uuid = uuid
        this.name = name
        this.arguments = arguments

        updateJSONObject()
    }
}