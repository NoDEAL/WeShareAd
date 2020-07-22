package we_share_ad.server.util

object CategoryUtil {
    private const val UPPER_CATEGORY_BYTE = 20
    private const val UPPER_CATEGORY_INT = 1048576

    fun compareParentCategory(category1: Int, category2: Int)
            = ((category1 shr UPPER_CATEGORY_BYTE) and (category2 shr UPPER_CATEGORY_BYTE)) != 0

    fun compareChildCategory(category1: Int, category2: Int) = category1 and category2

    /**
     * @param category: 상위 카테고리가 지워진 카테고리
     */
    fun getSameCategories(category: Int): List<Int> {
        assert(category < UPPER_CATEGORY_INT)

        val result = arrayListOf<Int>()
        var comparor = 1
        for (i in 0 until UPPER_CATEGORY_BYTE) {
            if ((category and comparor) != 0) {
                result.add(comparor)
            }
            comparor = comparor shl 1
        }

        return result
    }

    fun removeParentCategory(category: Int) = category and (UPPER_CATEGORY_INT - 1)
}