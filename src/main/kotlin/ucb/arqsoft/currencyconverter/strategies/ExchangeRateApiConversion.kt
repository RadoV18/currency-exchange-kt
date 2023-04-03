package ucb.arqsoft.currencyconverter.strategies

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ucb.arqsoft.currencyconverter.dto.ExchangeDto
import ucb.arqsoft.currencyconverter.dto.ExchangeRateApiDto
import ucb.arqsoft.currencyconverter.dto.InfoDto
import ucb.arqsoft.currencyconverter.dto.QueryDto
import ucb.arqsoft.currencyconverter.exception.ServiceException
import java.math.BigDecimal

@Component
class ExchangeRateApiConversion : ConversionStrategy {

    @Value("\${exchangerate.key}")
    private lateinit var apiKey: String;
    private val logger: Logger = LoggerFactory.getLogger(ExchangeRateApiConversion::class.java);
    private val client = OkHttpClient();
    private var amount : BigDecimal = BigDecimal.ZERO;

    override fun exchangeCurrency(amount: BigDecimal, from: String, to: String): ExchangeDto {
        val request : Request = Request.Builder()
            .url("https://v6.exchangerate-api.com/v6/$apiKey/pair/$from/$to/$amount")
            .build();
        this.amount = amount;
        try {
            logger.info("Calling ExchangeRate-Api");
            val response = client.newCall(request).execute();

            if(!response.isSuccessful) {
                logger.info("Unsuccessful response from ExchangeRate-Api");
                throw Exception("Error calling ExchangeRate-Api");
            }

            logger.info("Parsing response");
            val body = response.body!!.string();
            logger.info("Response: $body");
            return toExchangeDto(body);
        } catch (e: Exception) {
            throw ServiceException("Error calling ExchangeRate-Api");
        }
    }

    override fun toExchangeDto(response: String): ExchangeDto {
        try {
            val objectMapper = jacksonObjectMapper();
            val dto = objectMapper.readValue(response, ExchangeRateApiDto::class.java);
            return ExchangeDto(
                query = QueryDto(
                    from = dto.baseCode,
                    to = dto.targetCode,
                    amount = this.amount
                ),
                info = InfoDto(
                    timestamp = dto.timeLastUpdateUnix,
                ),
                result = dto.conversionResult
            );
        } catch (e: Exception) {
            // show stack trace
            e.printStackTrace();
            throw ServiceException("Error parsing response from ExchangeRate-Api");
        }
    }
}