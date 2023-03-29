package ucb.arqsoft.currencyconverter.dto

data class PaginatedDto<T> (
    val data: List<T>?,
    val limit: Int,
    val offset: Int,
    val total: Long
)