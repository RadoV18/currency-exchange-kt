package ucb.arqsoft.currencyconverter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class CurrencyConverterApplication

fun main(args: Array<String>) {
	runApplication<CurrencyConverterApplication>(*args)
}
