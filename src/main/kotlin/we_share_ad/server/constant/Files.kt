package we_share_ad.server.constant

import java.util.*

object Files {
    private val HOME_ROOT = System.getProperty("user.home")
    private val FILE_ROOT = "$HOME_ROOT/raw"

    val INTERFACE_CATEGORY_FIRST = "$FILE_ROOT/category_first.json"
    val INTERFACE_CATEGORY_SECOND = "$FILE_ROOT/category_second.json"
    val INTERFACE_SERVER_RESPONSE = "$FILE_ROOT/server_response.json"
    val INTERFACE_COMMANDS = "$FILE_ROOT/commands.json"
    val INTERFACE_SIZE = "$FILE_ROOT/size.json"
}