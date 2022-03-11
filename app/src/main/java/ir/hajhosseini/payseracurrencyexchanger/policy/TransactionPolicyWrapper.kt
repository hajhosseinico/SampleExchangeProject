package ir.hajhosseini.payseracurrencyexchanger.policy

import ir.hajhosseini.payseracurrencyexchanger.commissionMoreThanThisAmountIsFreeOfCommission
import ir.hajhosseini.payseracurrencyexchanger.commissionPercent
import ir.hajhosseini.payseracurrencyexchanger.everyXTransactionIsFreeOfCommission
import ir.hajhosseini.payseracurrencyexchanger.firstXTransactionIsFreeOfCommission
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceEntity
import ir.hajhosseini.payseracurrencyexchanger.room.transaction.TransactionEntity
import ir.hajhosseini.payseracurrencyexchanger.util.KotlinObjects.removeDecimal

class TransactionPolicyWrapper(_balanceList: List<BalanceEntity>, _transactionList : List<TransactionEntity>, _transactionEntity : TransactionEntity) {

    private val balanceList = _balanceList
    private val transactionList = _transactionList
    private val transactionEntity = _transactionEntity

    fun checkTransaction(): TransactionResult {
        val commission = calculateCommission()

        val transactionResult = TransactionResult(true, 0.0, "")

        if ((getBalance(transactionEntity.sold) - (commission + transactionEntity.soldAmount)) < 0) {
            transactionResult.isTransactionValid = false
            transactionResult.commission = commission
            transactionResult.message = "Insufficient inventory!"
        } else {
            transactionResult.isTransactionValid = true
            transactionResult.commission = commission
            transactionResult.message =
                "You have converted ${transactionEntity.soldAmount.removeDecimal(transactionEntity.soldAmount)} ${transactionEntity.sold} to ${
                    transactionEntity.boughtAmount.removeDecimal(transactionEntity.boughtAmount)
                } ${transactionEntity.bought}. Commission fee $commission ${transactionEntity.sold}"
        }

        return transactionResult
    }

    private fun getTransactionNumber(): Int {
        return transactionList.size + 1
    }

    private fun getBalance(name: String): Double {
        balanceList.find { it.name == name }
        val myMap = balanceList.associate { it.name to it.amount }
        return if (myMap[name] != null && myMap[name] != 0.0)
            myMap[name]!!
        else
            0.0
    }

    private fun calculateCommission(): Double {
        val tNumber = getTransactionNumber()

        // for example first 5 transactions are free of commission
        if (tNumber <= firstXTransactionIsFreeOfCommission) {
            return 0.0
        }

        // for example every 10 transaction is free of commission
        if (tNumber >= everyXTransactionIsFreeOfCommission && tNumber % everyXTransactionIsFreeOfCommission == 0) {
            return 0.0
        }

        // conversion of up to 200 input is free of commission
        if (transactionEntity.soldAmount > commissionMoreThanThisAmountIsFreeOfCommission) {
            return 0.0
        }

        return (transactionEntity.soldAmount * commissionPercent) / 100
    }

    data class TransactionResult(
        var isTransactionValid: Boolean,
        var commission: Double,
        var message: String
    )
}