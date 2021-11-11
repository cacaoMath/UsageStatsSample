package com.example.usagestatsmanagersample

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.nio.charset.StandardCharsets

class DataFileManager(context: Context) {
    private val TAG = this::class.java.simpleName
    private var dataFile: File? = null
    private var context :Context? = null

    init{
        this.context = context
    }

    fun saveData(dataStr : String){
        checkFile()
        dataFile?.appendText("$dataStr\n", StandardCharsets.UTF_8)
    }

    fun deleteFile(){
        checkFile()
        dataFile?.delete()
    }

    private fun checkFile(){
        val dataDir = File(this.context?.getExternalFilesDir(null), "measuredData")

        if(!isExternalStorageWritable()){
            Log.d(TAG,"ストレージ使用不可")
            return
        }
        if(!(dataDir.exists() and dataDir.isDirectory)){
            if(!dataDir.mkdir()) return
        }


        if(!File(dataDir, "measurementData.csv").exists()){
            dataFile = File(dataDir, "measurementData.csv")
            dataFile?.appendText("AnsweredNum,RightNum,AnswerTime,Note,Pattern,Label,Date\n", StandardCharsets.UTF_8)
        }else{
            dataFile = File(dataDir, "measurementData.csv")
        }
    }

    //ストレージが使用可能かをチェック
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
}