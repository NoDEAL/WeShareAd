package we_share_ad.server.util.json.model

import org.json.JSONArray
import org.json.JSONObject

/**
 * [JSONArray]와 [ArrayList]가 대응하기 위한 wrapper class. Containing 대상 타입은 [JSONParsable]을 상속해야함.
 *
 * @author 김지환
 * @see JSONParsable
 */
class JSONList<T: JSONContent>() : ArrayList<T>() {
    /**
     * [List]로부터 초기화
     *
     * @author 김지환
     * @param list: 초기화할 원본 List
     */
    constructor(list: List<T>): this() {
        addAll(list)
    }

    /**
     * [AbstractMutableList]로부터 초기화
     *
     * @author 김지환
     * @param mutableList: 초기화할 원본 MutableList
     */
    constructor(mutableList: AbstractMutableList<T>): this() {
        addAll(mutableList)
    }

    /**
     * [Collection]로부터 초기화
     *
     * @author 김지환
     * @param collection: 초기화할 원본 Collection
     */
    constructor(collection: Collection<T>): this() {
        addAll(collection)
    }

    /**
     * [JSONArray]로부터 초기화
     *
     * @author 김지환
     * @param jsonArray: 초기화할 원본 JSONArray
     * @param clazz: JSONArray에 포함된 대상 타입 class
     */
    constructor(jsonArray: JSONArray, clazz: Class<T>): this() {
        addAll((0 until jsonArray.length()).mapTo(JSONList<T>()) {
            clazz.getDeclaredConstructor(JSONObject::class.java).newInstance(jsonArray.getJSONObject(it))
        })
    }

    /**
     * [JSONArray]로 변환. 변환시 타입 정보는 상실됨.
     *
     * @author 김지환
     * @return 변환된 JSONArray
     */
    fun toJSONArray(): JSONArray {
        val jsonArray = JSONArray()
        forEach {
            it.updateJSONObject()
            jsonArray.put(it.toJSONObject())
        }

        return jsonArray
    }

    /**
     * 원소들의 JSONObject update
     *
     * @author 김지환
     */
    fun updateJSONObject() = forEach { it.updateJSONObject() }
}

/**
 * [arrayListOf]에 대응하는 가변인자 생성 method
 *
 * @author 김지환
 * @param elements: 초기 삽입할 원소
 * @return 인자 원소로 생성된 JSONList
 */
fun <T: JSONContent> jsonListOf(vararg elements: T): JSONList<T> {
    return if (elements.isEmpty()) {
        JSONList()
    } else {
        JSONList<T>().apply { addAll(elements) }
    }
}

/**
 * [Collection]으로부터 직접 생성 method
 *
 * @author 김지환
 * @return Collection으로부터 생성된 JSONList
 */
fun <T: JSONContent> Collection<T>.toJSONList(): JSONList<T> {
    return JSONList(this)
}

/**
 * [JSONArray]로부터 초기화된 JSONList 생성
 *
 * @author 김지환
 * @return JSONArray로부터 초기화된 JSONList
 */
inline fun <reified T: JSONContent> JSONArray.toJSONList(): JSONList<T> {
    return JSONList(this, T::class.java)
}

fun toJSONArray() = JSONArray().apply { forEach { put(it) } }