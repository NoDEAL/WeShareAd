package we_share_ad.server.constant

import org.json.JSONObject
import we_share_ad.server.component.WebInterface

object AdCategory {
    /**
     * 첫번째 범주의 카테고리가 포함된 enum
     *
     * @author 김지환
     * @param categoryId: 카테고리 ID
     * @param description: 해당하는 한글 실제 이름
     */
    enum class First(val categoryId: Int, val description: String): WebInterface {
        ELECTRONIC(1, "전자제품");

        constructor(jsonObject: JSONObject): this(
                jsonObject.getInt("category_id"),
                jsonObject.getString("description")
        )

        override fun toJSONObject() = JSONObject().apply {
            put("id", categoryId)
            put("name", name)
            put("title", description)
        }

        override fun getFilePath() = Files.INTERFACE_CATEGORY_FIRST

        override fun toString(): String {
            return categoryId.toString()
        }
    }

    /**
     * 두번째 범주의 카테고리가 포함된 enum
     *
     * @author 김지환
     * @param firstCategory: 첫번째 카테고리
     * @param categoryId: 카테고리 ID
     * @param description: 해당하는 한글 실제 이름
     */
    enum class Second(val firstCategory: First, val categoryId: Int, val description: String): WebInterface {
        MOBILE_PHONE(First.ELECTRONIC, 1, "휴대전화");

        constructor(jsonObject: JSONObject): this(
                getFirstAdCategory(jsonObject.getInt("first_category")),
                jsonObject.getInt("category_id"),
                jsonObject.getString("description")
        )

        override fun toJSONObject() = JSONObject().apply {
            put("id", categoryId)
            put("category_first", firstCategory.categoryId)
            put("name", name)
            put("title", description)
        }

        override fun getFilePath() = Files.INTERFACE_CATEGORY_SECOND

        override fun toString(): String {
            return categoryId.toString()
        }
    }

    private val firstValues = First.values()
    private val secondValues = Second.values()

    /**
     * 카테고리 ID로부터 첫번째 범주의 카테고리 탐색
     *
     * @author 김지환
     * @param firstCategoryId: 첫번째 범주의 카테고리 ID
     * @return 탐색된 카테고리
     */
    fun getFirstAdCategory(firstCategoryId: Int) = firstValues.first { it.categoryId == firstCategoryId }

    /**
     * 첫번째 카테고리 ID와 두번째 카테고리 ID로부터 두번째 범주의 카테고리 탐색
     *
     * @author 김지환
     * @param firstCategoryId: 첫번째 범주의 카테고리 ID
     * @param secondCategoryId: 두번째 범주의 카테고리 ID
     * @return 탐색된 카테고리
     */
    fun getSecondAdCategory(firstCategoryId: Int, secondCategoryId: Int) =
            getSecondAdCategory(getFirstAdCategory(firstCategoryId), secondCategoryId)

    /**
     * 첫번째 카테고리와 두번째 카테고리 ID로부터 두번째 범주의 카테고리 탐색
     *
     * @author 김지환
     * @param firstCategory: 첫번째 범주의 카테고리
     * @param secondCategoryId: 두번째 범주의 카테고리 ID
     * @return 탐색된 카테고리
     */
    fun getSecondAdCategory(firstCategory: First, secondCategoryId: Int) = secondValues.first {
        it.firstCategory == firstCategory && it.categoryId == secondCategoryId
    }
}