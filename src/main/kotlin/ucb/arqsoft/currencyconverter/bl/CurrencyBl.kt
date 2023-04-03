package ucb.arqsoft.currencyconverter.bl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
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
        page: Int,
        size: Int,
        query: Map<String, String>
    ): Page<ExchangeDto> {
        logger.info("Starting business logic");
        if(page < 0 || size <= 0) {
            logger.error("Limit must be greater than zero and offset must be greater or equal than zero");
            throw IllegalArgumentException("Limit must be greater than zero and offset must be greater or equal than zero");
        }
        var spec = Specification.where<Currency>(null);

        for((key, value) in query) {
            if(value != null) {
                var filterName : String? = null;
                if(key == "from") {
                    filterName = "currencyFrom";
                } else if(key == "to") {
                    filterName = "currencyTo";
                }
                if(filterName != null) {
                    val filter = Specification<Currency> { root, _, criteriaBuilder ->
                        criteriaBuilder.equal(root.get<String>(filterName), value)
                    }
                    spec = spec.and(filter);
                }
            }
        }
        var sort : Sort? = null;
        if(query.containsKey("sortBy")) {
            val sortBy = query["sortBy"];
            var sortOrder = query["sortOrder"];
            if(sortOrder == null) {
                sortOrder = "asc";
            }
            sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        }
        if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "requestDate");
        }
        val pageable = PageRequest.of(page, size, sort);
        logger.info(pageable.toString());
        logger.info(sort.toString());
        return currencyRepository.findAll(spec, pageable).map {
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
    }
}
