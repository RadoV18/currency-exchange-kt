package ucb.arqsoft.currencyconverter.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExchangeRateApiDto (
    val result: String,
    val documentation: String,
    @JsonProperty("terms_of_use")
    val termsOfUse: String,
    @JsonProperty("time_last_update_unix")
    val timeLastUpdateUnix: Long,
    @JsonProperty("time_last_update_utc")
    val timeLastUpdateUtc: String,
    @JsonProperty("time_next_update_unix")
    val timeNextUpdateUnix: Long,
    @JsonProperty("time_next_update_utc")
    val timeNextUpdateUtc: String,
    @JsonProperty("base_code")
    val baseCode: String,
    @JsonProperty("target_code")
    val targetCode: String,
    @JsonProperty("conversion_rate")
    val conversionRate: BigDecimal,
    @JsonProperty("conversion_result")
    val conversionResult: BigDecimal
)