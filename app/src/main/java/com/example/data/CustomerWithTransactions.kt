package com.example.data

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation

@Immutable
data class CustomerWithTransactions(
    @Embedded val customer: CustomerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val transactions: List<TransactionEntity> = emptyList()
) {
    val totalGiven: Double
        get() = transactions.filter { !it.isDeleted && it.isCredit }.sumOf { it.amount }

    val totalGot: Double
        get() = transactions.filter { !it.isDeleted && !it.isCredit }.sumOf { it.amount }

    val netBalance: Double
        get() = totalGiven - totalGot
}
