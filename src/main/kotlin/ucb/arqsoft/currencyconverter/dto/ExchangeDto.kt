package ucb.arqsoft.currencyconverter.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExchangeDto (
    val query: QueryDto,
    val info: InfoDto,
    val result: BigDecimal
)