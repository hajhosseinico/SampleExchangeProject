package ir.hajhosseini.payseracurrencyexchanger.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.hajhosseini.payseracurrencyexchanger.model.retrofit.responsemodels.DataState
import ir.hajhosseini.payseracurrencyexchanger.model.retrofit.responsemodels.GetRatesResponseModel
import ir.hajhosseini.payseracurrencyexchanger.policy.TransactionPolicyWrapper
import ir.hajhosseini.payseracurrencyexchanger.repository.MainRepository
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceEntity
import ir.hajhosseini.payseracurrencyexchanger.room.transaction.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel
@Inject
constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

    private val _rateList: MutableLiveData<DataState<GetRatesResponseModel>> =
        MutableLiveData()
    private val _exChangeStock: MutableLiveData<TransactionPolicyWrapper.TransactionResult> =
        MutableLiveData()
    private val _transactionList: MutableLiveData<List<TransactionEntity>> =
        MutableLiveData()
    private val _getBalance: MutableLiveData<List<BalanceEntity>> =
        MutableLiveData()

    val exChangeStock: LiveData<TransactionPolicyWrapper.TransactionResult>
        get() = _exChangeStock
    val rateList: LiveData<DataState<GetRatesResponseModel>>
        get() = _rateList
    val transactionList: LiveData<List<TransactionEntity>>
        get() = _transactionList
    val getBalance: LiveData<List<BalanceEntity>>
        get() = _getBalance

    fun setRateStateEvent(mainStateEvent: MainStateEvent, accessKey: String, format: String) {
        viewModelScope.launch {
            when (mainStateEvent) {
                is MainStateEvent.GetRates -> {
                    // getting data from repository and passing it to fragment
                    mainRepository.getRates(accessKey, format)
                        .onEach { dataState ->
                            _rateList.value = dataState
                        }
                        .launchIn(viewModelScope)
                }
                else -> {
                    // Do Nothing
                }
            }
        }
    }

    fun setMainStateEvent(mainStateEvent: MainStateEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (mainStateEvent) {
                is MainStateEvent.FirstAppOpen -> {
                    // getting data from repository and passing it to fragment
                    mainRepository.appOpenedForFirstTime()
                }
                is MainStateEvent.GetTransactions -> {
                    // getting data from repository and passing it to fragment
                    mainRepository.getTransactions()
                        .onEach { transactionList ->
                            _transactionList.value = transactionList
                        }
                        .launchIn(viewModelScope)
                }
                is MainStateEvent.GetBalances -> {
//                     getting data from repository and passing it to fragment
                    mainRepository.getBalances()
                        .onEach { getBalance ->
                            _getBalance.value = getBalance
                        }
                        .launchIn(viewModelScope)
                }
                else -> {
                    // Do Nothing
                }
            }
        }
    }

    fun setExchangeStateEvent(
        mainStateEvent: MainStateEvent, transactionEntity: TransactionEntity
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactionRuleWrapper = TransactionPolicyWrapper(
                mainRepository.getAllBalances(),
                mainRepository.getAllTransactions(),
                transactionEntity
            ).checkTransaction()

            if (transactionRuleWrapper.isTransactionValid) {
                when (mainStateEvent) {
                    is MainStateEvent.ExchangeCurrency -> {
                        transactionEntity.commissionFee = getCommissionFee(transactionEntity)
                        mainRepository.exchangeCurrency(transactionEntity)
                        withContext(Dispatchers.Main) {
                            _exChangeStock.value = transactionRuleWrapper
                        }
                    }
                    else -> {
                        // Do Nothing
                    }
                }
            } else {
                viewModelScope.launch(Dispatchers.Main) {
                    _exChangeStock.value = transactionRuleWrapper
                }
            }
        }
    }

    fun getCommissionFee(
        transactionEntity: TransactionEntity
    ): Double {
        val checkResult = TransactionPolicyWrapper(
            mainRepository.getAllBalances(),
            mainRepository.getAllTransactions(),
            transactionEntity
        ).checkTransaction()
        return checkResult.commission

    }

    fun getBuyAmount(
        rateList: ArrayList<BalanceEntity>,
        sellAmount: Double,
        selectedSellStock: String,
        selectedBuyStock: String
    ): Double {
        val rateMap = rateList.associateBy({ it.name }, { it.amount })
        val eurAmount = sellAmount / rateMap[selectedSellStock]!!
        return eurAmount * rateMap[selectedBuyStock]!!
    }
}


sealed class MainStateEvent {
    object GetRates : MainStateEvent()
    object GetBalances : MainStateEvent()
    object GetTransactions : MainStateEvent()
    object ExchangeCurrency : MainStateEvent()
    object FirstAppOpen : MainStateEvent()
}

