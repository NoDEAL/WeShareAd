import we_share_ad.server.constant.AdCategory
import we_share_ad.server.database.DatabaseServer
import we_share_ad.server.socket.SocketServer
import we_share_ad.server.util.WebInterfaceUtil

class Main {
    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            println("Hello World!")

            WebInterfaceUtil.exportAll()

            DatabaseServer.createInstance("localhost", "mysql", "root", "kimju888").start()
            SocketServer.createInstance(8080).start()

            AdCategory.First.values().forEach { println(it.toJSONObject()) }
            AdCategory.Second.values().forEach { println(it.toJSONObject()) }
        }
    }
}