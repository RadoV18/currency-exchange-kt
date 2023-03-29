package ucb.arqsoft.currencyconverter.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.math.BigInteger

@JsonIgnoreProperties(ignoreUnknown = true)
data class InfoDto (
        var timestamp: Long
)