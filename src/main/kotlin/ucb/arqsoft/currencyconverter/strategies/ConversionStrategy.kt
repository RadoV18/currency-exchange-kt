package ucb.arqsoft.currencyconverter.strategies

import ucb.arqsoft.currencyconverter.dto.ExchangeDto
import java.math.BigDecimal

interface ConversionStrategy {
    fun exchangeCurrency(amount: BigDecimal, from: String, to: String): ExchangeDto;
    fun toExchangeDto(response: String): ExchangeDto;
}