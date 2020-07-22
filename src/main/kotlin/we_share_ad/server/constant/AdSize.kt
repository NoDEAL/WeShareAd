package we_share_ad.server.constant

import org.json.JSONObject
import we_share_ad.server.component.WebInterface

enum class AdSize(val description: String, val id: Int, val width: Int, val height: Int): WebInterface {
    LEADERBOARD("리더보드", 0, 728, 90),
    LARGE_RECTANGLE("큰 직사각형", 1, 336, 280),
    MEDIUM_RECTANGLE("중간 직사각형", 2, 300, 250),
    WIDE_SKYSCRAPER("수평형 스카이스크래퍼", 3, 160, 600),
    TABLET_LANDSCAPE_FULL_SCREEN("태블릿 가로 모드 전체 화면", 4, 1024, 768),
    LARGE_LEADERBOARD("큰 리더보드", 5, 970, 90),
    TABLET_PORTRAIT_FULL_SCREEN("태블릿 세로 모드 전체 화면", 6, 768, 1024),
    MOBILE_LANDSCAPE_FULL_SCREEN("모바일 가로 모드 전체 화면", 7, 480, 320),
    BANNER("배너", 8, 468, 60),
    MOBILE_PORTRAIT_FULL_SCREEN("모바일 세로 모드 전체 화면", 9, 320, 480),
    MOBILE_LEADERBOARD("모바일 리더보드", 10, 320, 50),
    HALF_PAGE("반 페이지", 11, 300, 600),
    SQUARE("정사각형", 12, 250, 250),
    HALF_BANNER("하프 배너", 13, 234, 60),
    SMALL_SQUARE("작은 정사각형", 14, 200, 200),
    SMALL_RECTANGLE("작은 직사각형", 15, 180, 150),
    BUTTON("버튼", 16, 125, 125);

    override fun toJSONObject() = JSONObject().apply {
        put("description", description)
        put("id", id)
        put("width", width)
        put("height", height)
    }

    override fun getFilePath() = Files.INTERFACE_SIZE

    companion object {
        private val sizes = values()

        fun findSize(id: Int) = sizes.first { it.id == id }
    }
}