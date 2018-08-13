package monese.marochkin.andriy

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.missions.view.*
import java.text.SimpleDateFormat
import java.util.*
// Adapter for RecyclerView on Second Screen(with Sticky header )
class MissionAdapter(internal var mContext: Context, internal var mArrayListString: ArrayList<MissionsModel>) : RecyclerView.Adapter<ViewHolder_M>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_M {
        val view = LayoutInflater.from(mContext).inflate(R.layout.missions, parent, false)
        return ViewHolder_M(view)
    }

    override fun onBindViewHolder(holder: ViewHolder_M, position: Int) {
        val MissionRow = mArrayListString[position]
        holder?.missionname.text = MissionRow.name
        holder?.launchdate?.text = getDateTime(MissionRow.date.toString())
        if(MissionRow.successful == true){
            holder?.successful?.text = "success"
        }
        else{
            holder?.successful?.text = "unsuccess"
        }

        setAnimation(holder.missionmainlT)

        Picasso.get().load(MissionRow.picture).into(holder?.pictureIMG)
    }
    private fun getDateTime(s: String): String {
        try {
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val netDate = Date(s.toLong()*1000L)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
    override fun getItemCount(): Int {
        return mArrayListString.size
    }
    private fun setAnimation(viewToAnimate: View) {
        if (viewToAnimate.animation == null) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.animation = animation
        }
    }



}
class ViewHolder_M (view: View) : RecyclerView.ViewHolder(view)  {
    val pictureIMG = view.pictureIMG
    val missionname = view.missionname
    val launchdate = view.launchdate
    val successful = view.successful
    val missionmainlT = view.missionmainlT
}

