package ucb.arqsoft.currencyconverter.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ucb.arqsoft.currencyconverter.bl.CurrencyBl
import ucb.arqsoft.currencyconverter.dto.ExchangeDto
import ucb.arqsoft.currencyconverter.dto.PaginatedDto
import ucb.arqsoft.currencyconverter.dto.ResponseDto
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/conversions")
class ConversionController @Autowired constructor(
    private val currencyBl: CurrencyBl
) {

    private val logger: Logger = LoggerFactory.getLogger(ConversionController::class.java)

    @GetMapping
    fun getConversion(
        @RequestParam from: String,
        @RequestParam to: String,
        @RequestParam amount: BigDecimal
    ): ResponseEntity<ResponseDto<ExchangeDto>> {
        logger.info("GET: Exchange $amount from $from to $to");
        logger.info("Starting business logic");
        val exchangeDto = currencyBl.exchangeRate(amount, from, to);

        return ResponseEntity.ok(
            ResponseDto(
                data = exchangeDto,
                message = "Success",
                successful = true
            )
        );
    }

    @GetMapping("/all")
    fun getAllExchanges(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam requestParams: Map<String, String>
    ): ResponseEntity<ResponseDto<Page<ExchangeDto>>> {
        logger.info("GET: getting page $page with $size");
        val paginatedExchangeDto: Page<ExchangeDto> =
            currencyBl.getExchangeList(page, size, requestParams);
        logger.info("Sending response");
        return ResponseEntity.ok(
            ResponseDto(
                data = paginatedExchangeDto,
                message = "Success",
                successful = true
            )
        )
    }
}