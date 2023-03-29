package ucb.arqsoft.currencyconverter.dao

import javax.persistence.*
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.Date

@Entity
@Table(name = "currency")
class Currency (
    var currencyFrom: String,
    var currencyTo: String,
    var amount: BigDecimal,
    var result: BigDecimal,
    @Temporal(TemporalType.TIMESTAMP)
    var requestDate: Date,
    @Id
    @GeneratedValue
    var id: Long = 0
) {
    constructor() : this("", "", BigDecimal.ZERO, BigDecimal.ZERO, Timestamp(System.currentTimeMillis()))
}