package monese.marochkin.andriy

import android.content.Context
import android.content.Intent
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.roket.view.*

 // Adapter for RecyclerView on main screen


class RoketAdapter(val items : ArrayList<RoketModel>, val context: Context) : RecyclerView.Adapter<ViewHolder_R>()  {
    private var filteredRokets = ArrayList<RoketModel>()
    private var filtering = false


    override fun getItemCount(): Int {
        if (filtering) {
            return filteredRokets.size
        }
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_R {
        return ViewHolder_R(LayoutInflater.from(context).inflate(R.layout.roket, parent, false))
    }
    private fun setAnimation(viewToAnimate: View) {
        if (viewToAnimate.animation == null) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.animation = animation
        }
    }


    fun filterRokets(active: Boolean, update: Int) {
        if(update == 0) {
            if (active == false) {
                filtering = false;
                DiffUtil.calculateDiff(RoketRowDiffCallback(items, filteredRokets), false).dispatchUpdatesTo(this)
            } else {
                filtering = true;
                val newRokets = items.filter { items -> items.active == active } as ArrayList<RoketModel>
                DiffUtil.calculateDiff(RoketRowDiffCallback(newRokets, items), false).dispatchUpdatesTo(this)
                filteredRokets = newRokets
            }
        }else{
            val newRokets = items.filter { items -> items.active == active } as ArrayList<RoketModel>
            DiffUtil.calculateDiff(RoketRowDiffCallback(items, newRokets), false).dispatchUpdatesTo(this)
            filtering = false;
        }
    }


    override fun onBindViewHolder(holder: ViewHolder_R, position: Int) {

        val RoketRow : RoketModel = if (filtering) {
            filteredRokets[position]
        } else {
            items[position]
        }
        holder?.nameroket?.text = RoketRow.name
        holder?.country?.text = RoketRow.country
        holder?.enginescount?.text = RoketRow.engcount.toString()

        setAnimation(holder.roketmainlT)

        holder.roketmainlT.setOnClickListener {

            val intent = Intent(context, RoketDetailsActivity::class.java)
            intent.putExtra("name", RoketRow.name)
            intent.putExtra("desc", RoketRow.description)
            intent.putExtra("id", RoketRow.id)
            context.startActivity(intent)
        }
    }


}

class ViewHolder_R (view: View) : RecyclerView.ViewHolder(view)  {
    val nameroket = view.nameroket
    val country = view.country
    val enginescount = view.enginescount
    val roketmainlT = view.roketmainlT
}
class RoketRowDiffCallback(private val newRows : List<RoketModel>, private val oldRows : List<RoketModel>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRow = oldRows[oldItemPosition]
        val newRow = newRows[newItemPosition]
        return oldRow.name == newRow.name
    }

    override fun getOldListSize(): Int = oldRows.size

    override fun getNewListSize(): Int = newRows.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRow = oldRows[oldItemPosition]
        val newRow = newRows[newItemPosition]
        return oldRow == newRow
    }
}
