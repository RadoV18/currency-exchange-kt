package ucb.arqsoft.currencyconverter.dao.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ucb.arqsoft.currencyconverter.dao.Currency

interface CurrencyRepository: CrudRepository<Currency, Long> {
    override fun <S : Currency> save(entity: S): S

    @Query("""
        SELECT * FROM currency c
        WHERE
            (:from IS NULL OR c.currency_from = :from) AND
            (:to IS NULL OR c.currency_to = :to)
        ORDER BY
            CASE WHEN :sortBy = 'requestDate' AND :sortOrder = 'asc' THEN c.request_date END ASC,
            CASE WHEN :sortBy = 'requestDate' AND :sortOrder = 'desc' THEN c.request_date END DESC,
            CASE WHEN :sortBy = 'amount' AND :sortOrder = 'asc' THEN c.amount END ASC,
            CASE WHEN :sortBy = 'amount' AND :sortOrder = 'desc' THEN c.amount END DESC,
            CASE WHEN :sortBy = 'result' AND :sortOrder = 'asc' THEN c.result END ASC,
            CASE WHEN :sortBy = 'result' AND :sortOrder = 'desc' THEN c.result END DESC
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    fun findAllWithLimitAndOffset(
        @Param("limit") limit: Int,
        @Param("offset") offset: Int,
        @Param("from") from: String?,
        @Param("to") to: String?,
        @Param("sortBy") sortBy: String?,
        @Param("sortOrder") sortOrder: String?
    ): List<Currency>
}