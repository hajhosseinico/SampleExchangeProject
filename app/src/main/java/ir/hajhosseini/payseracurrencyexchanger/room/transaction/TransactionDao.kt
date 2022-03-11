package ir.hajhosseini.payseracurrencyexchanger.room.transaction

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Movie detail room Dao
 */
@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transactionEntity: TransactionEntity): Long

    @Query("SELECT * FROM transaction_list")
    fun getTransactions(): List<TransactionEntity>

}