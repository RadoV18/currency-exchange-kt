package ucb.arqsoft.currencyconverter.dto

data class ResponseDto<T>(
    val data: T?,
    val message: String,
    val successful: Boolean
)