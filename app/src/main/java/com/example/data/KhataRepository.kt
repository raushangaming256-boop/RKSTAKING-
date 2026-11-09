package com.example.data

import kotlinx.coroutines.flow.Flow

class KhataRepository(private val khataDao: KhataDao) {
    val allCustomers: Flow<List<CustomerEntity>> = khataDao.getAllCustomersFlow()
    val allTransactions: Flow<List<TransactionEntity>> = khataDao.getAllTransactionsFlow()

    fun getTransactionsForCustomer(customerId: Int): Flow<List<TransactionEntity>> {
        return khataDao.getTransactionsForCustomerFlow(customerId)
    }

    suspend fun getCustomerById(customerId: Int): CustomerEntity? {
        return khataDao.getCustomerById(customerId)
    }

    fun searchCustomers(query: String): Flow<List<CustomerEntity>> {
        return khataDao.searchCustomersFlow(query)
    }

    fun getCustomersCount(): Flow<Int> {
        return khataDao.getCustomersCountFlow()
    }

    suspend fun insertCustomer(customer: CustomerEntity): Long {
        return khataDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: CustomerEntity) {
        khataDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(id: Int) {
        // First clean up all transactions for this customer
        khataDao.deleteTransactionsByCustomer(id)
        // Then delete the customer
        khataDao.deleteCustomer(id)
    }

    suspend fun getTransactionById(id: Int): TransactionEntity? {
        return khataDao.getTransactionById(id)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        khataDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        khataDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Int) {
        khataDao.deleteTransaction(id)
    }

    suspend fun clearCustomerHistory(customerId: Int) {
        khataDao.deleteTransactionsByCustomer(customerId)
    }

    suspend fun deleteAllData() {
        khataDao.deleteAllTransactions()
        khataDao.deleteAllCustomers()
    }

    fun getCustomerWithTransactionsFlow(customerId: Int): Flow<CustomerWithTransactions?> {
        return khataDao.getCustomerWithTransactionsFlow(customerId)
    }

    suspend fun getCustomerWithTransactions(customerId: Int): CustomerWithTransactions? {
        return khataDao.getCustomerWithTransactions(customerId)
    }

    fun getAllCustomersWithTransactionsFlow(): Flow<List<CustomerWithTransactions>> {
        return khataDao.getAllCustomersWithTransactionsFlow()
    }

    fun getRecentTransactions(limit: Int = 20): Flow<List<TransactionEntity>> {
        return khataDao.getRecentTransactionsFlow(limit)
    }

    fun getCustomerTransactionsCount(customerId: Int): Flow<Int> {
        return khataDao.getCustomerTransactionsCount(customerId)
    }

    suspend fun insertCustomers(customers: List<CustomerEntity>): List<Long> {
        return khataDao.insertCustomers(customers)
    }

    suspend fun insertTransactions(transactions: List<TransactionEntity>): List<Long> {
        return khataDao.insertTransactions(transactions)
    }
}
