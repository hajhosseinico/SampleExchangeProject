package ir.hajhosseini.payseracurrencyexchanger.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ir.hajhosseini.payseracurrencyexchanger.*
import ir.hajhosseini.payseracurrencyexchanger.databinding.FragmentDashboardBinding
import ir.hajhosseini.payseracurrencyexchanger.model.retrofit.responsemodels.DataState
import ir.hajhosseini.payseracurrencyexchanger.model.retrofit.responsemodels.GetRatesResponseModel
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceEntity
import ir.hajhosseini.payseracurrencyexchanger.room.transaction.TransactionEntity
import ir.hajhosseini.payseracurrencyexchanger.ui.currency_list.CurrencyListFragment
import ir.hajhosseini.payseracurrencyexchanger.util.KotlinObjects.removeDecimal
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class DashboardFragment : Fragment(), BalanceRecyclerAdapter.Interaction {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var isGettingRatesActive = true

    private lateinit var balanceRecyclerAdapter: BalanceRecyclerAdapter
    private lateinit var sellStockListRecyclerAdapter: SellStockListRecyclerAdapter
    private lateinit var rateListRecyclerAdapterSell: RateStockListRecyclerAdapter

    private lateinit var listSelection: ListSelection

    private var rateList = ArrayList<BalanceEntity>()
    private var balanceListString = ArrayList<String>()
    private var rateListString = ArrayList<String>()
    private lateinit var rateResponse: GetRatesResponseModel
    private lateinit var transactionEntity: TransactionEntity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add 10000 EUR for first time login
        initFirstAppOpen()

        // Getting user balances from database
        getBalances()

        // Setting fragment observers
        setObservers()

        // Initializing recycler views
        initRecyclerViews()


        setClickListeners()

        // Changing buy amount and commission fee with text change event
        setAmountTextChangeListener()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setClickListeners() {
        binding.addTransactionBtnAdd.setOnClickListener {
            if (binding.edtAmount.text!!.isNotEmpty()) {
                // Updating buy amount, commission and date inputs
                updateAddTransactionInputs()

                // Validate and save exchange into DataBase
                viewModel.setExchangeStateEvent(
                    MainStateEvent.ExchangeCurrency, transactionEntity
                )

            } else {
                showDialog("Please put sell amount!", false)
            }

        }

        binding.pairOptionBuy.setOnClickListener {
            listSelection = ListSelection.RATE
            navigateToOtherCurrencyList(rateListString)
        }
        binding.pairOptionSell.setOnClickListener {
            listSelection = ListSelection.BALANCE
            navigateToOtherCurrencyList(balanceListString)
        }
    }


    private fun getBalances() {
        viewModel.setMainStateEvent(MainStateEvent.GetBalances)

    }

    private fun setAmountTextChangeListener() {
        binding.edtAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.edtAmount.text!!.isNotEmpty())
                    updateAddTransactionInputs()
            }
        })
    }

    private fun initFirstAppOpen() {
        if (prefs.firstTimeOpenApp) {
            viewModel.setMainStateEvent(MainStateEvent.FirstAppOpen)
            prefs.firstTimeOpenApp = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerViews() {
        binding.rclBalance.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            balanceRecyclerAdapter = BalanceRecyclerAdapter(this@DashboardFragment)
            adapter = balanceRecyclerAdapter
        }

        binding.rclSellList.apply {
            layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    RecyclerView.HORIZONTAL,
                    false
                )
            sellStockListRecyclerAdapter = SellStockListRecyclerAdapter()
            sellStockListRecyclerAdapter.onItemClick = { selectedPosition ->
                sellStockListRecyclerAdapter.notifyDataSetChanged()
                if (binding.edtAmount.text!!.isNotEmpty())
                    updateAddTransactionInputs()
            }
            adapter = sellStockListRecyclerAdapter
        }

        binding.rclBuyList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL,
                false
            )

            rateListRecyclerAdapterSell = RateStockListRecyclerAdapter()
            rateListRecyclerAdapterSell.onItemClick = {
                rateListRecyclerAdapterSell.notifyDataSetChanged()
                if (binding.edtAmount.text!!.isNotEmpty())
                    updateAddTransactionInputs()
            }
            adapter = rateListRecyclerAdapterSell
        }
    }

    private fun startGettingRatesEveryXSeconds() {
        isGettingRatesActive = true
        startRepeatingJob()
    }

    private fun stopGettingRatesEveryXSeconds() {
        isGettingRatesActive = false
    }

    private fun startRepeatingJob(): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (isGettingRatesActive) {
                viewModel.setRateStateEvent(MainStateEvent.GetRates, accessKey, format)
                delay(callRateApiInterval)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {
        viewModel.getBalance.observe(viewLifecycleOwner) { balances ->
            balanceRecyclerAdapter.submitList(balances)
        }

        viewModel.getBalance.observe(viewLifecycleOwner) { balances ->
            sellStockListRecyclerAdapter.submitList(balances)
            balanceListString.clear()
            balances.forEach {
                balanceListString.add(it.name)
            }
        }

        viewModel.exChangeStock.observe(viewLifecycleOwner) { result ->
            if (result.isTransactionValid) {
                getBalances()
                updateAddTransactionInputs()
                showDialog(result.message, true)
            } else {
                showDialog(result.message, false)
            }
        }

        viewModel.rateList.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success<GetRatesResponseModel> -> {
                    val balanceEntities = ArrayList<BalanceEntity>()

                    val jsonObject = JSONObject(Gson().toJson(dataState.data))
                    val rates: JSONObject = jsonObject.getJSONObject("rates")
                    val keys: Iterator<String> = rates.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        balanceEntities.add(BalanceEntity(key, rates.getDouble(key)))
                    }
                    rateList.addAll(balanceEntities)
                    rateResponse = dataState.data
                    rateListRecyclerAdapterSell.submitList(balanceEntities)
                    rateListString.clear()
                    balanceEntities.forEach {
                        rateListString.add(it.name)
                    }
                }
                is DataState.Error -> {
                }

                is DataState.Loading -> {
                }
            }
        }
        sharedViewModel.clickedPosition.observe(viewLifecycleOwner) { position ->
            when (listSelection) {
                ListSelection.RATE -> {
                    binding.rclBuyList.postDelayed(Runnable {
                        rateListRecyclerAdapterSell.setSelectedItem(position)
                        rateListRecyclerAdapterSell.notifyDataSetChanged()
                        binding.rclBuyList.smoothScrollToPosition(position)
                    }, 50)
                }
                ListSelection.BALANCE -> {
                    binding.rclSellList.postDelayed(Runnable {
                        sellStockListRecyclerAdapter.setSelectedItem(position)
                        sellStockListRecyclerAdapter.notifyDataSetChanged()
                        binding.rclSellList.smoothScrollToPosition(position)
                    }, 50)
                }
            }
        }
    }

    private fun showDialog(message: String, isSuccess: Boolean) {
        //default config is success style
        var style = R.style.CustomAlertDialogSuccess
        var title: String = requireContext().getString(R.string.success)

        if (!isSuccess) {
            style = R.style.CustomAlertDialogError
            title = requireContext().getString(R.string.error)
        }
        val builder = AlertDialog.Builder(requireContext(), style)
            .create()
        val view = layoutInflater.inflate(R.layout.layout_dialog, null)
        builder.setView(view)
        builder.setCanceledOnTouchOutside(false)
        builder.show()

        builder.findViewById<TextView>(R.id.txtDialogTitle)?.text = title
        builder.findViewById<TextView>(R.id.txtDialogMessage)?.text = message
        builder.findViewById<TextView>(R.id.txtDialogOk)?.setOnClickListener {
            builder.dismiss()
        }
        builder.findViewById<ImageView>(R.id.imgDialogClose)?.setOnClickListener {
            builder.dismiss()
        }

    }


    private fun navigateToOtherCurrencyList(list: ArrayList<String>) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(
                R.id.mainFragmentContainer,
                CurrencyListFragment.newInstance(list)
            )
            .addToBackStack(CurrencyListFragment::class.simpleName.toString())
            .commit()
    }

    private fun updateAddTransactionInputs() {
        runBlocking {

            val sellAmount = binding.edtAmount.text.toString().toDouble()
            launch(Dispatchers.Default) {
                val selectedSellStock = sellStockListRecyclerAdapter.getSelectedItem()
                val selectedBuyStock = rateListRecyclerAdapterSell.getSelectedItem()

                val buyAmount = viewModel.getBuyAmount(
                    rateList,
                    sellAmount,
                    selectedSellStock!!.name,
                    selectedBuyStock!!.name
                )
                binding.txtBuyPriceAmount.text =
                    buyAmount.removeDecimal(buyAmount).toString()

                val date = SimpleDateFormat("MM-dd-yyyy").format(Date())
                binding.txtDate.text = date


                transactionEntity = TransactionEntity(
                    selectedSellStock.name,
                    selectedBuyStock.name,
                    System.currentTimeMillis(),
                    sellAmount,
                    buyAmount,
                    0.0,
                    date
                )

                binding.txtCommissionFee.text =
                    viewModel.getCommissionFee(transactionEntity).toString()

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(position: Int, item: BalanceEntity) {
    }

    override fun onStop() {
        super.onStop()
        stopGettingRatesEveryXSeconds()
    }

    override fun onResume() {
        super.onResume()
        startGettingRatesEveryXSeconds()
    }

    enum class ListSelection {
        RATE, BALANCE
    }
}