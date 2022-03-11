package ir.hajhosseini.payseracurrencyexchanger.ui.currency_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.hajhosseini.payseracurrencyexchanger.databinding.FragmentCurrencyListBinding
import ir.hajhosseini.payseracurrencyexchanger.ui.dashboard.SharedViewModel
import java.util.ArrayList

class CurrencyListFragment : Fragment(),CurrencyListRecyclerAdapter.Interaction {
    private var _binding: FragmentCurrencyListBinding? = null
    private val binding get() = _binding!!

    private lateinit var currencyListRecyclerAdapter: CurrencyListRecyclerAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // TODO: Rename and change types of parameters
    private var currencyListBundle: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currencyListBundle = it.getStringArrayList(CURRENCY_LIST_BUNDLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCurrencyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: ArrayList<String>) =
            CurrencyListFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(CURRENCY_LIST_BUNDLE, param1)
                }
            }

        const val CURRENCY_LIST_BUNDLE = "currency_list_bundle"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        build()
    }

    private fun build() {
        initRecyclerView()
        setClickListener()
    }

    private fun setClickListener() {
        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initRecyclerView() {
        binding.rclCurrencyList.apply {
            layoutManager =
                LinearLayoutManager(
                    this@CurrencyListFragment.context,
                    RecyclerView.VERTICAL,
                    false
                )
            currencyListRecyclerAdapter = CurrencyListRecyclerAdapter(this@CurrencyListFragment)
            adapter = currencyListRecyclerAdapter

        }

        currencyListRecyclerAdapter.submitList(currencyListBundle as List<String>)
    }

    override fun onItemSelected(position: Int, item: String) {
        sharedViewModel.setClickedPosition(position)
        requireActivity().onBackPressed()
    }

}