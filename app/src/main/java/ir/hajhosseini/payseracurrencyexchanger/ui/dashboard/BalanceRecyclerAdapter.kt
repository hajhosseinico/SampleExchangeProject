package ir.hajhosseini.payseracurrencyexchanger.ui.dashboard


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.hajhosseini.payseracurrencyexchanger.R
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceEntity
import ir.hajhosseini.payseracurrencyexchanger.util.KotlinObjects.removeDecimal

class BalanceRecyclerAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BalanceEntity>() {

        override fun areItemsTheSame(
            oldItem: BalanceEntity,
            newItem: BalanceEntity
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: BalanceEntity,
            newItem: BalanceEntity
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BalancePostViewHolder(

            LayoutInflater.from(parent.context).inflate(
                R.layout.item_balance,
                parent,
                false
            ),
            interaction

        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is BalancePostViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<BalanceEntity>) {
        differ.submitList(list)
    }

    class BalancePostViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BalanceEntity) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            itemView.findViewById<TextView>(R.id.txtCurrencyName).text = item.name
            itemView.findViewById<TextView>(R.id.txtCurrencyBalance).text = item.amount.removeDecimal(item.amount).toString()
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: BalanceEntity)
    }
}


