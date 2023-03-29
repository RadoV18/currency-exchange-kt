package ucb.arqsoft.currencyconverter.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/conversion")
class ConversionController {

    private val logger: Logger = LoggerFactory.getLogger(ConversionController::class.java)

    @GetMapping
    fun getConversion() : String {
        logger.info("GET: Conversion")
        return "Hello World!"
    }

}