package ir.hajhosseini.payseracurrencyexchanger.room.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Room transaction list table object model
 */
@Entity(tableName = "transaction_list")
data class TransactionEntity(

    @ColumnInfo(name = "sold")
    var sold: String,

    @ColumnInfo(name = "bought")
    var bought: String,

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "sold_amount")
    var soldAmount: Double,

    @ColumnInfo(name = "bought_amount")
    var boughtAmount: Double,

    @ColumnInfo(name = "commission_fee")
    var commissionFee: Double,

    @ColumnInfo(name = "date")
    var date: String,
    )