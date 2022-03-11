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

class SellStockListRecyclerAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((Int) -> Unit)? = null
    companion object {
        var selectedPosition = 0
    }

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
            return oldItem.name == newItem.name
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MoviePostViewHolder(

            LayoutInflater.from(parent.context).inflate(
                R.layout.item_stock,
                parent,
                false
            ),
            onItemClick!!
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is MoviePostViewHolder -> {
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

    class MoviePostViewHolder
    constructor(
        itemView: View,
        private val onItemClick: ((Int) -> Unit)? = null
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BalanceEntity) = with(itemView) {
            itemView.setOnClickListener {
                selectedPosition = layoutPosition
                onItemClick!!.invoke(selectedPosition)
            }

            itemView.findViewById<TextView>(R.id.txtStockName).text = item.name

            if (adapterPosition == selectedPosition) {
                itemView.findViewById<TextView>(R.id.txtStockName)
                    .setTextColor(context.getColor(R.color.colorPrimary))
            } else {
                itemView.findViewById<TextView>(R.id.txtStockName)
                    .setTextColor(context.getColor(R.color.colorWhite))
            }
        }
    }

    fun getSelectedItem(): BalanceEntity? {
        if(differ.currentList.size == 0)
            return null
        return differ.currentList[selectedPosition]
    }

    fun setSelectedItem(position : Int) {
        selectedPosition = position
    }
}


