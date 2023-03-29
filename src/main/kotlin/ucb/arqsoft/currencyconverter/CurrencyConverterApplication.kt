package ucb.arqsoft.currencyconverter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CurrencyConverterApplication

fun main(args: Array<String>) {
	runApplication<CurrencyConverterApplication>(*args)
}
