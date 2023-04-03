package ucb.arqsoft.currencyconverter.strategies

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ucb.arqsoft.currencyconverter.bl.CurrencyBl
import ucb.arqsoft.currencyconverter.dao.Currency
import ucb.arqsoft.currencyconverter.dto.ExchangeDto
import ucb.arqsoft.currencyconverter.exception.ServiceException
import java.math.BigDecimal
import java.sql.Timestamp

@Component
class ApiLayerConversion : ConversionStrategy {

    @Value("\${api.key}")
    private lateinit var apiKey: String;
    private val logger: Logger = LoggerFactory.getLogger(ApiLayerConversion::class.java);
    private val client = OkHttpClient();

    override fun exchangeCurrency(amount: BigDecimal, from: String, to: String): ExchangeDto {
        val request: Request = Request.Builder()
            .url(
                "https://api.apilayer.com/exchangerates_data/convert?" +
                        "to=$to" +
                        "&from=$from" +
                        "&amount=$amount"
            )
            .addHeader("apikey", apiKey)
            .build();

        try {
            logger.info("Calling external service");
            val response = client.newCall(request).execute();

            if(!response.isSuccessful) {
                logger.info("Unsuccessful response from external service");
                throw Exception("Error calling external service");
            }

            logger.info("Parsing response");
            val body = response.body!!.string();
            logger.info("$response");
            return toExchangeDto(body);
        } catch (e: Exception) {
            throw ServiceException("Error calling external service");
        }
    }

    override fun toExchangeDto(response: String): ExchangeDto {
        val objectMapper = jacksonObjectMapper();
        val dto = objectMapper.readValue(response, ExchangeDto::class.java);
        // change timestamp to milliseconds
        dto.info.timestamp *= 1000;
        return dto;
    }
}