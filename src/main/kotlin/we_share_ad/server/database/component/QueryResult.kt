package we_share_ad.server.database.component

import we_share_ad.server.component.IDContent
import java.sql.ResultSet
import java.util.*

class QueryResult(private val query: Query, resultSet: ResultSet): IDContent {
    val content = parseContent(resultSet)

    override fun getUUID() = query.getUUID()

    private fun parseContent(resultSet: ResultSet): ArrayList<Row> {
        val metadata = resultSet.metaData
        val columns = metadata.columnCount
        val contents = ArrayList<Row>()

        while (resultSet.next()) {
            val row = Row()
            for (i in 1 .. columns) {
                row[metadata.getColumnName(i)] = resultSet.getObject(i)
            }
            contents.add(row)
        }

        return contents
    }

    fun getRowCount() = content.size

    fun getRow(rowIndex: Int) = content[rowIndex]
}