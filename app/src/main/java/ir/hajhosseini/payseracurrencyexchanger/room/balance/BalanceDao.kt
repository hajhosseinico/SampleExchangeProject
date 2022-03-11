package ir.hajhosseini.payseracurrencyexchanger.room.balance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Balance room Dao
 */
@Dao
interface BalanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(balanceEntity: BalanceEntity): Long

    @Query("SELECT * FROM balance_list")
    suspend fun getAllBoughtStocks(): List<BalanceEntity>

    @Query("SELECT * FROM balance_list")
    fun getBoughtStocks(): List<BalanceEntity>

    @Query("UPDATE balance_list SET amount = :amount WHERE name = :name")
    suspend fun updateStock(amount : Double, name : String): Int
}