package monese.marochkin.andriy


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.android.synthetic.main.activity_roket_details.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

//   Second screen with graph, description, launches's list


class RoketDetailsActivity : BaseActivity() {
    var roketname:String = ""
    var roketdesc:String = ""
    var roketid:String = ""
    val client = OkHttpClient()
    val lpy = ArrayList<Entry>()
    val misssions = ArrayList<MissionsModel>()
    var MISSIONCAHE = "missionwtihroket"
    lateinit var dialog:ProgressDialog
    private var mRecyclerView: RecyclerView? = null
    private var adapter: MissionAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roket_details)
        val bundle=intent.extras

        if(bundle!=null)
        {
            roketname = bundle.getString("name")
            roketdesc = bundle.getString("desc")
            roketid = bundle.getString("id")
        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // load data
        dialog= ProgressDialog.show(this@RoketDetailsActivity,"","Loading")
        textView2.setText(roketname)
        Descrip.setText(roketdesc)

        //  Get data from URL
        run("https://api.spacexdata.com/v2/launches?order=asc&rocket_id="+roketid, roketid);



        mRecyclerView =rv_roket_mission

    }
    private fun getSectionCallback(mission: ArrayList<MissionsModel>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            override fun isSection(position: Int): Boolean {
                val yearCur = getDateTime(mission[position].date.toString())
                val yearPrev = yearCur
                if(position > 0) {
                    val yearPrev = getDateTime(mission[position-1].date.toString())
                }
                return position == 0 || yearCur != yearPrev
            }

            override fun getSectionHeader(position: Int): CharSequence {
                return getDateTime(mission[position].date.toString())
            }
        }
    }
    // Date format function
    fun getDateTime(s: String): String {
        try {
            val sdf = SimpleDateFormat("yyyy")
            val netDate = Date(s.toLong()*1000L)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
    // Graph Settings
    fun setupLineChartData() {
        val lineDataSet = LineDataSet(lpy, "Roket Launches")
        lineDataSet.color = ContextCompat.getColor(this, R.color.colorAccent)
        lineDataSet.valueTextColor = ContextCompat.getColor(this, android.R.color.white)
        val lineData = LineData(lineDataSet)
        lineChart.getDescription().setEnabled(false);


        lineChart.data = lineData
        val xAxis = lineChart.getXAxis()
        val leftAxis = lineChart.getAxisLeft()
        val rightAxis = lineChart.getAxisRight()
        rightAxis.setGranularity(1f)
        leftAxis.setGranularity(1f)
        xAxis.setGranularity(1f)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter( MyYAxisValueFormatter())

        lineChart.animateXY(2000, 2000)


        lineChart.invalidate()


    }
    // Class for Graph Year format
    inner class MyYAxisValueFormatter : IAxisValueFormatter {

        private val mFormat: DecimalFormat


        init {
            mFormat = DecimalFormat("####")
        }
        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            return mFormat.format(value)
        }
    }
    // okhttp3 main function
    fun run(url: String, roket_id: String) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if(ReadWriteJsonFileUtils(this@RoketDetailsActivity).readJsonFileData(MISSIONCAHE+roket_id) != null){
                    parseJson(ReadWriteJsonFileUtils(this@RoketDetailsActivity).readJsonFileData(MISSIONCAHE+roket_id))
                }
                else{
                    val intent = Intent(this@RoketDetailsActivity, MainActivity::class.java)
                    this@RoketDetailsActivity.startActivity(intent)
                }
                dialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response){
                val jsonData = response.body()?.string()

                try {
                    ReadWriteJsonFileUtils(this@RoketDetailsActivity).deleteFile(MISSIONCAHE+roket_id);
                    ReadWriteJsonFileUtils(this@RoketDetailsActivity).createJsonFileData(MISSIONCAHE+roket_id, jsonData.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                parseJson(jsonData)
                dialog.dismiss()
            }

        })
    }

    // parse Json data function
    private fun parseJson(myJson: String?){
        val Jobject = JSONArray(myJson)
        var lpyo: Int  = 0;
        var count: Int  = 0;

        val mainHandler = Handler(this@RoketDetailsActivity.getMainLooper())
        for (i in 0 until Jobject.length()) {
            val e = Jobject.getJSONObject(i)
            val links =  e.getJSONObject("links")
            misssions.add(MissionsModel(
                    links.getString("mission_patch_small"),
                    e.getString("mission_name"),
                    e.getString("launch_date_unix").toLong(),
                    e.getString("launch_success").toBoolean()
            ))

            val year = e.getString("launch_year").toInt()

            if (lpyo === 0) {
                lpyo = year
                count = 1;
            } else if (lpyo == year) {
                count++;
            } else {
                lpy.add(Entry(lpyo.toFloat(), count.toFloat()))

                count = 1;
                lpyo = year;
            }
            if (i == Jobject.length() - 1) {
                lpy.add(Entry(lpyo.toFloat(), count.toFloat()))
            }
        }
        mainHandler.post(Runnable {
            if(Jobject.length() > 0){
                setupLineChartData()
                mRecyclerView!!.layoutManager = LinearLayoutManager(this)
                adapter = MissionAdapter(this, misssions)
                mRecyclerView!!.adapter = adapter

                val sectionItemDecoration = RecyclerSectionItemDecoration(resources.getDimensionPixelSize(R.dimen.header),
                        true,
                        getSectionCallback(misssions))
                mRecyclerView!!.addItemDecoration(sectionItemDecoration)
            }
            else{
                rv_roket_mission.layoutManager = LinearLayoutManager(this@RoketDetailsActivity)
                rv_roket_mission.setVisibility(View.GONE);
            }
        })
    }
}





