package we_share_ad.server.socket

import org.json.JSONObject
import we_share_ad.server.component.IDContent
import we_share_ad.server.constant.Command
import we_share_ad.server.socket.component.CommandContent
import we_share_ad.server.socket.component.CommandProcess
import we_share_ad.server.util.ClosableThread
import we_share_ad.server.util.TimeUtil
import java.io.*
import java.net.Socket
import java.util.*

class Connection(private val socket: Socket): ClosableThread(), IDContent {
    private val bufferedReader = BufferedReader(InputStreamReader(socket.inputStream))
    private val bufferedWriter = BufferedWriter(OutputStreamWriter(socket.outputStream))
    private var token = UUID.randomUUID()

    init {
        println(TimeUtil.getTimeStamp())
        println("New Connection from: ${socket.inetAddress.hostAddress}:${socket.port}")

        prefix = {
            val serverMessage = JSONObject().apply { put("value", 200) }
            send(serverMessage)
        }

        loop = {
            val clientMessage = bufferedReader.readLine()
            if (clientMessage == null) {
                sleep()
            } else {
                println("${TimeUtil.getTimeStamp()} token: $token from: ${socket.inetAddress}")
                println("message=$clientMessage")

                val parsedMessage = JSONObject(clientMessage)
                if (parsedMessage.getString("name") == Command.CLOSE.title) {
                    close()
                } else {
                    val command = CommandContent(parsedMessage)
                    val serverMessage = CommandProcess.onReceived(command)
                    send(serverMessage)
                }
            }
        }
    }

    private fun send(serverMessage: JSONObject) {
        println("${TimeUtil.getTimeStamp()} token: $token to: ${socket.inetAddress}")
        println("message=$serverMessage")

        bufferedWriter.write(serverMessage.toString() + '\n')
        bufferedWriter.flush()
    }

    override fun getUUID(): UUID = token

    override fun close() {
        super.close()

        bufferedReader.close()
        bufferedWriter.close()
        socket.close()

        SocketServer.getInstance().removeConnection(this)
    }
    
    companion object {
        fun makeServerResponse(uuid: UUID, arguments: JSONObject) = JSONObject().apply {
            put("uuid", uuid.toString())
            put("arguments", arguments)
        }

        fun makePositiveResponse(uuid: UUID) = makeServerResponse(uuid, JSONObject().apply {
            put("result", true)
        })

        fun makePositiveResponse(uuid: UUID, arguments: JSONObject) = makeServerResponse(uuid, arguments.apply {
            put("result", true)
        })

        fun makeNegativeResponse(uuid: UUID, code: Int) = makeNegativeResponse(uuid, code, JSONObject())

        fun makeNegativeResponse(uuid: UUID, code: Int, arguments: JSONObject) = makeServerResponse(uuid, arguments).apply {
            put("result", false)
            put("code", code)
        }
    }
}