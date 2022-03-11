package ir.hajhosseini.payseracurrencyexchanger.room.balance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room balance list table object model
 */
@Entity(tableName = "balance_list")
data class BalanceEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "amount")
    var amount: Double,
)