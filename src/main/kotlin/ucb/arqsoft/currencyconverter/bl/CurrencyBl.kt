package ucb.arqsoft.currencyconverter.bl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ucb.arqsoft.currencyconverter.dao.Currency
import ucb.arqsoft.currencyconverter.dao.repository.CurrencyRepository
import ucb.arqsoft.currencyconverter.dto.ExchangeDto
import ucb.arqsoft.currencyconverter.dto.InfoDto
import ucb.arqsoft.currencyconverter.dto.PaginatedDto
import ucb.arqsoft.currencyconverter.dto.QueryDto
import ucb.arqsoft.currencyconverter.exception.ServiceException
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.*

@Service
class CurrencyBl {

    @Value("\${api.key}")
    private lateinit var apiKey: String;
    private val logger: Logger = LoggerFactory.getLogger(CurrencyBl::class.java)
    @Autowired
    private lateinit var currencyRepository : CurrencyRepository;

    fun exchangeRate(amount: BigDecimal, from: String, to: String): ExchangeDto {
        if(amount < BigDecimal.ZERO) {
            logger.error("Amount must be greater than zero");
            throw IllegalArgumentException("Amount must be greater than zero");
        }

        val client = OkHttpClient();

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
            val body = response.body?.string();
            val objectMapper = jacksonObjectMapper();
            val dto = objectMapper.readValue(body, ExchangeDto::class.java);
            // change timestamp to milliseconds
            dto.info.timestamp *= 1000;

            val currency = Currency(
                currencyFrom = from,
                currencyTo = to,
                amount = amount,
                result = dto.result,
                requestDate = Timestamp(dto.info.timestamp)
            )
            logger.info("Saving response in database");
            currencyRepository.save(currency);
            logger.info("Response saved.");
            return dto;
        } catch (e: Exception) {
            throw ServiceException("Error calling external service");
        }
    }

    fun getExchangeList(
        limit: Int,
        offset: Int,
        query: Map<String, String>
    ): PaginatedDto<ExchangeDto> {
        logger.info("Starting business logic");
        if(limit <= 0 || offset < 0) {
            logger.error("Invalid limit or offset");
            throw IllegalArgumentException("Limit must be greater than zero and offset must be greater or equal than zero");
        }
        // data validation of query
        val from: String? = query["from"];
        val to: String? = query["to"];
        val sortBy = query["sortBy"];
        val sortDirection = query["sortDirection"];
        if(from != null && from.length != 3) {
            logger.error("Invalid from currency");
            throw IllegalArgumentException("Invalid from currency");
        }
        if(to != null && to.length != 3) {
            logger.error("Invalid to currency");
            throw IllegalArgumentException("Invalid to currency");
        }
        if(sortBy != null && sortBy != "amount" && sortBy != "result" && sortBy != "requestDate") {
            logger.error("Invalid sortBy");
            throw IllegalArgumentException("Invalid sortBy");
        }
        if(sortDirection != null && sortDirection != "asc" && sortDirection != "desc") {
            logger.error("Invalid sortDirection");
            throw IllegalArgumentException("Invalid sortDirection");
        }
        // check if there are query parameters
        var hasQueryParameters = false;
        if(query.size > 2) {
            hasQueryParameters = true;
        }
        logger.info("Getting data from database");
        val exchangeList = currencyRepository
            .findAllWithLimitAndOffset(limit, offset, from, to, sortBy, sortDirection);
        val exchangeDtoList = exchangeList.map {
            ExchangeDto(
                query = QueryDto(
                    from = it.currencyFrom,
                    to = it.currencyTo,
                    amount = it.amount
                ),
                info = InfoDto(
                    timestamp = it.requestDate.time
                ),
                result = it.result
            )
        }
        val total = if(hasQueryParameters) exchangeList.size.toLong() else currencyRepository.count();
        return PaginatedDto(
            data = exchangeDtoList,
            total = total,
            limit = limit,
            offset = offset
        )
    }
}
