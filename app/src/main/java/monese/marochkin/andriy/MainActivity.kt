package monese.marochkin.andriy

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

// Main screen


class MainActivity : BaseActivity() {


    val client = OkHttpClient();
    val mainrokets = ArrayList<RoketModel>()
    lateinit var dialog:ProgressDialog
    var filtredMA: Boolean = true
    val WelcomeDialog = "wd"
    private var mSnackBar: Snackbar? = null
    var messageToUser = ""
    val MAINCACHE = "mainpage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //my_first_time
        val wd = getSharedPreferences(WelcomeDialog, 0)
        if (wd.getBoolean("my_first_time", true)) {
            firstdialog()
            wd.edit().putBoolean("my_first_time", false).commit();
        }
        // active filter
        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (rv_animal_list.adapter != null) {
                    (rv_animal_list.adapter as RoketAdapter).filterRokets(filtredMA, 0)
                }
                if(filtredMA == true){
                    filtredMA = false
                    messageToUser = "Only active Rokets"
                }else{
                    filtredMA = true
                    messageToUser = "All Rokets"
                }


                mSnackBar = Snackbar.make(findViewById(R.id.rootLayout), messageToUser, Snackbar.LENGTH_LONG)
                mSnackBar?.duration = Snackbar.LENGTH_SHORT
                mSnackBar?.show()
            }
        })
        // load data
        dialog= ProgressDialog.show(this,"","Loading")


        run("https://api.spacexdata.com/v2/rockets")

        rv_animal_list.layoutManager = LinearLayoutManager(this@MainActivity)
        rv_animal_list.adapter = RoketAdapter(mainrokets, this@MainActivity)


        if (rv_animal_list.adapter != null) {
            (rv_animal_list.adapter as RoketAdapter).filterRokets(false, 1)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_exit -> {
                finish();
                System.exit(0);
                return true
            }
            R.id.action_update-> {

                // TODO Need check intenet connection
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                this@MainActivity.startActivity(intent)
                mSnackBar = Snackbar.make(findViewById(R.id.rootLayout), "Updated", Snackbar.LENGTH_LONG)
                mSnackBar?.duration = Snackbar.LENGTH_SHORT
                mSnackBar?.show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun run(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if(ReadWriteJsonFileUtils(this@MainActivity).readJsonFileData(MAINCACHE) != null){
                    parseJson(ReadWriteJsonFileUtils(this@MainActivity).readJsonFileData(MAINCACHE))
                }
                dialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response){
                val jsonData = response.body()?.string()
                try {
                    ReadWriteJsonFileUtils(this@MainActivity).deleteFile(MAINCACHE);
                    ReadWriteJsonFileUtils(this@MainActivity).createJsonFileData(MAINCACHE, jsonData.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                parseJson(jsonData)

            }
        })
    }
    private fun parseJson(myJson: String?){
        val Jobject = JSONArray(myJson)
        val mainHandler = Handler(this@MainActivity.getMainLooper())

        for (i in 0 until Jobject.length()) {
            val e = Jobject.getJSONObject(i)
            val enginecount =  e.getJSONObject("engines")
            mainrokets.add(RoketModel(e.getString("id"), e.getString("name"),   e.getString("country"),  e.getString("active").toBoolean(), enginecount.getString("number").toInt(), e.getString("description")))
        }


        dialog.dismiss()
        mainHandler.post(Runnable {
            rv_animal_list.layoutManager = LinearLayoutManager(this@MainActivity)
            rv_animal_list.adapter = RoketAdapter(mainrokets, this@MainActivity)
        })
    }
    private fun firstdialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.welcome_dialog, null)
        dialogBuilder.setView(dialogView)

         dialogBuilder.setTitle("Hello")

        dialogBuilder.setNegativeButton("Welcome", DialogInterface.OnClickListener { dialog, whichButton -> })

        val b = dialogBuilder.create()
        b.show()
    }

}
