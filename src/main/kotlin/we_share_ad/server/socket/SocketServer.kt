package we_share_ad.server.socket

import we_share_ad.server.util.ClosableThread
import we_share_ad.server.util.IDList
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.util.*

/**
 * Socket 연결을 대기하는 Thread
 *
 * @author 김지환
 * @param port: 서버를 열 포트
 * @constructor 포트에 해당하는 소켓 서버 생성
 */
class SocketServer private constructor(port: Int): ClosableThread() {
    companion object {
        /**
         * Singleton 객체 유지를 위한 instance
         *
         * @author 김지환
         */
        private var instance: SocketServer? = null

        /**
         * 이미 생성된 인스턴스를 호출하기 위한 method
         *
         * @author 김지환
         * @throws RuntimeException: 인스턴스가 생성되지 않았을 때 예외 발생 [createInstance]
         * @return 실행중인 인스턴스
         */
        fun getInstance(): SocketServer {
            if (instance == null) {
                throw RuntimeException("Instance never initialized")
            }

            return instance!!
        }

        /**
         * 1회에 한해 인스턴스를 생성하기 위한 method
         *
         * @author 김지환
         * @param port: 서버를 열 포트
         * @throws RuntimeException: 이미 인스턴스가 생성되었을 때 예외 발생
         * @return 새로 생성된 instance
         */
        fun createInstance(port: Int): SocketServer {
            if (instance == null) {
                instance = SocketServer(port)

                return instance!!
            } else {
                throw RuntimeException("Instance already initialized")
            }
        }
    }

    /**
     * 연결을 대기할 [ServerSocket]
     *
     * @author 김지환
     */
    private val serverSocket: ServerSocket

    /**
     * 연결이 저장된 [IDList]
     *
     * @author 김지환
     */
    private val connections: IDList<Connection>

    init {
        // 현재 실행중인 서버의 IP를 받아온다.
        val host = getLocalIpAddress()
        val address = InetAddress.getByName(host)

        // 주소와 포트 출력
        println("SocketServer: address=$address, port=$port")

        // ServerSocket 생성
        serverSocket = ServerSocket(port, 100, address)
        // 연결 목록 초기화
        connections = IDList()
        // Thread 반복 정의
        loop = {
            // 연결 수락 후 Connection pool에 저장
            connections.add(Connection(serverSocket.accept()).apply { start() })
        }
    }

    /**
     * Connection token에 해당하는 Connection 반환
     *
     * @author 김지환
     * @param token: 탐색할 token
     * @return token에 해당하는 Connection
     */
    fun getConnection(token: UUID): Connection = connections[token]

    /**
     * 저장된 Connection 삭제
     * Connection 삭제가 정지를 의미하지 않음. 이미 정지되었거나 이후에 정지할 필요가 있음.
     *
     * @author 김지환
     * @param connection: 삭제할 connection
     */
    fun removeConnection(connection: Connection) = connections.remove(connection)

    /**
     * Connection token에 해당하는 저장된 Connection 삭제
     * Connection 삭제가 정지를 의미하지 않음. 이미 정지되었거나 이후에 정지할 필요가 있음.
     *
     * @author 김지환
     * @param token: 삭제할 connection
     */
    fun removeConnection(token: UUID) = connections.remove(token)

    /**
     * 현재 pool에 저장된 connection의 수를 반환
     *
     * @author 김지환
     * @return 현재 pool에 저장된 connection의 수
     */
    fun getConnectionCount() = connections.size

    /**
     * 실행중인 서버의 IPv4 주소 반환
     *
     * @author 김지환
     * @throws RuntimeException: IPv4주소를 찾지 못 했을 때 예외 발생. 네트워크 연결 문제 등.
     * @return 실행중인 서버의 IP 주소
     */
    private fun getLocalIpAddress(): String {
        val interfaces = NetworkInterface.getNetworkInterfaces()

        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            val addresses = networkInterface.inetAddresses

            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()

                if (!address.isLoopbackAddress && address !is Inet6Address) {
                    return address.hostAddress
                }
            }
        }

        throw RuntimeException("Address not found")
    }
}