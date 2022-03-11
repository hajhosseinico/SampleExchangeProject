package ir.hajhosseini.payseracurrencyexchanger.repository

import ir.hajhosseini.payseracurrencyexchanger.model.retrofit.ExchangeRetrofitInterface
import ir.hajhosseini.payseracurrencyexchanger.model.retrofit.responsemodels.DataState
import ir.hajhosseini.payseracurrencyexchanger.model.retrofit.responsemodels.GetRatesResponseModel
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceDao
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceEntity
import ir.hajhosseini.payseracurrencyexchanger.room.transaction.TransactionDao
import ir.hajhosseini.payseracurrencyexchanger.room.transaction.TransactionEntity
import ir.hajhosseini.payseracurrencyexchanger.startAmount
import ir.hajhosseini.payseracurrencyexchanger.startAmountBase
import ir.hajhosseini.payseracurrencyexchanger.util.InternetStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainRepository
constructor(
    private val exchangeRetrofitInterface: ExchangeRetrofitInterface,
    private val internetStatus: InternetStatus,
    private val transactionDao: TransactionDao,
    private val balanceDao: BalanceDao,
) {

    suspend fun getRates(
        accessKey: String,
        format: String
    ): Flow<DataState<GetRatesResponseModel>> =
        flow {
            emit(DataState.Loading)

            // checking internet availability
            if (internetStatus.isInternetAvailable()) {
                try {
                    // getting data from server
                    val baseNetworkExchange = exchangeRetrofitInterface.getRates(accessKey, format)
                    emit(DataState.Success(baseNetworkExchange))
                } catch (e: Exception) {
                    emit(DataState.Error(e))
                }
            } else {
                emit(DataState.Error(Exception("Internet is not available!!!")))
            }
        }

    suspend fun exchangeCurrency(transactionEntity: TransactionEntity) {
        val balances = balanceDao.getAllBoughtStocks()
        val map = balances.associate { Pair(it.name, it.amount) }

        // minus the sold amount from first stock
        if(map.containsKey(transactionEntity.sold)){
            balanceDao.updateStock(map[transactionEntity.sold]!!.minus(transactionEntity.soldAmount.plus(transactionEntity.commissionFee)),transactionEntity.sold)
        }
        // plus the bought amount from second stock
        if(map.containsKey(transactionEntity.bought)){
            balanceDao.updateStock(map[transactionEntity.bought]!!.plus(transactionEntity.boughtAmount),transactionEntity.bought)
        }else{
            val balanceEntity = BalanceEntity(transactionEntity.bought,transactionEntity.boughtAmount)
            balanceDao.insert(balanceEntity)
        }

        // add transaction into database
        transactionDao.insert(transactionEntity)
    }

    fun getBalances(): Flow<List<BalanceEntity>> =
        flow {
            emit(balanceDao.getAllBoughtStocks())
        }

    fun getAllBalances(): List<BalanceEntity> {
           return balanceDao.getBoughtStocks()
        }

    fun getTransactions(): Flow<List<TransactionEntity>> =
        flow {
            emit(transactionDao.getTransactions())
        }

    fun getAllTransactions(): List<TransactionEntity> {
        return transactionDao.getTransactions()
    }

    suspend fun appOpenedForFirstTime() {
        val balanceEntity = BalanceEntity(startAmountBase,startAmount)
        balanceDao.insert(balanceEntity)
    }


}
