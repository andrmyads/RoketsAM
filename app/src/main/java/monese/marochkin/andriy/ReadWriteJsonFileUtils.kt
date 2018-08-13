package monese.marochkin.andriy

import android.app.Activity
import android.content.Context

import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException


// Cache for Json
class ReadWriteJsonFileUtils(internal var context: Context) {
    internal var activity: Activity? = null

    fun createJsonFileData(filename: String, mJsonResponse: String) {
        try {
            val checkFile = File(context.applicationInfo.dataDir + "/roketchache/")
            if (!checkFile.exists()) {
                checkFile.mkdir()
            }
            val file = FileWriter(checkFile.absolutePath + "/" + filename)
            file.write(mJsonResponse)
            file.flush()
            file.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun readJsonFileData(filename: String): String? {
        try {
            val f = File(context.applicationInfo.dataDir + "/roketchache/" + filename)
            if (!f.exists()) {
                //onNoResult();
                return null
            }
            val `is` = FileInputStream(f)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //onNoResult();
        return null
    }


    fun deleteFile(fileName: String) {
        val f = File(context.applicationInfo.dataDir + "/roketchache/" + fileName)
        if (f.exists()) {
            f.delete()
        }
    }
}