package ucb.arqsoft.currencyconverter.dto

import java.math.BigDecimal

data class QueryDto (
    val from: String,
    val to: String,
    val amount: BigDecimal
)