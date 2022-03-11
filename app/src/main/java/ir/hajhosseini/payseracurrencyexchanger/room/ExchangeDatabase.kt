package ir.hajhosseini.payseracurrencyexchanger.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceEntity
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceDao
import ir.hajhosseini.payseracurrencyexchanger.room.transaction.TransactionEntity
import ir.hajhosseini.payseracurrencyexchanger.room.transaction.TransactionDao

/**
 * Instantiating room database and setting database name
 * Used by Provider Modules
 */
@Database(entities = [TransactionEntity::class,BalanceEntity::class],version = 1)
abstract class ExchangeDatabase : RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
    abstract fun balanceDao(): BalanceDao

    companion object{
        const val DATABASE_NAME = "exchange_db"
    }
}