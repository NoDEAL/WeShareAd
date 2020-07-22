package we_share_ad.server.util

import java.util.*

object TimeUtil {
    fun getTimeStamp(): String {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH] + 1
        val date = calendar[Calendar.DATE]
        val hour = calendar[Calendar.HOUR]
        val minute = calendar[Calendar.MINUTE]
        val second = calendar[Calendar.SECOND]
        val millis = (System.currentTimeMillis() % 1000).toInt()

        return String.format("%04d-%02d-%02d %02d:%02d:%02d %4d", year, month, date, hour, minute, second, millis)
    }
}