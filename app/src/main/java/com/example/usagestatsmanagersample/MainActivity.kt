package com.example.usagestatsmanagersample

import android.app.AppOpsManager
import android.app.usage.EventStats
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.icu.util.TimeZone
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.example.usagestatsmanagersample.databinding.ActivityMainBinding
import com.opencsv.bean.StatefulBeanToCsvBuilder
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.StringWriter
import java.time.Instant
import java.util.*
import kotlin.Comparator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val twoMonthButton = mainBinding.twoMonthButton
        val fiveDaysButton = mainBinding.fiveDaysButton
        val oneYearButton = mainBinding.oneYearButton

        if (!checkForPermission()) {
            Toast.makeText(
                this,
                "Failed to retrieve app usage statistics. " +
                        "You may need to enable access for this app through " +
                        "Settings > Security > Apps with usage access",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }else{
            oneYearButton.setOnClickListener {
                Log.i(TAG,"for 1 year")
                showAppUsageStatsLog(setMonthInMillis(-12))
            }
            twoMonthButton.setOnClickListener {
                Log.i(TAG,"for 2 month")
                showAppUsageStatsLog(setMonthInMillis())
            }

            fiveDaysButton.setOnClickListener {
                Log.i(TAG,"for 5 days")
                showAppUsageStatsLog(setDayInMillis())
            }
        }
    }

    /**
     * cal: 現在より前の指定したい日，
     * return : このメソッドを呼び出した今現在までのアプリ使用履歴を含んだUsageStatsのリスト
     *
     */
    private fun getAppUsageStats(beginCal : Calendar): MutableList<UsageStats> {
        Log.d(TAG,(beginCal.timeInMillis).toString() +"::: " +System.currentTimeMillis())

        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, beginCal.timeInMillis, System.currentTimeMillis()
        )
    }

    private fun getAppEventStats(beginCal: Calendar): MutableList<EventStats>? {
        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        return usageStatsManager.queryEventStats(
            UsageStatsManager.INTERVAL_YEARLY, beginCal.timeInMillis, System.currentTimeMillis()
        )
    }

    private fun showAppUsageStatsLog(beginCal : Calendar) {
        val strWriter = StringWriter()
        val beanWriter = StatefulBeanToCsvBuilder<UsageStatsData>(strWriter).build()
        getAppUsageStats(beginCal).forEach {
            if (it.totalTimeVisible > 0) {

                Log.i(
                    TAG, """
                packageName : ${it.packageName}
                totalTimeVisible : ${it.totalTimeInForeground}
                firstTimeStamp : ${it.firstTimeStamp}
                lastTimeVisible : ${it.lastTimeUsed}
                lastTimeForegroundServiceUsed: ${it.lastTimeForegroundServiceUsed}
            """.trimIndent()
                )
            }
            beanWriter.write(
                UsageStatsData(
                    it.firstTimeStamp,
                    it.lastTimeForegroundServiceUsed,
                    it.lastTimeStamp,
                    it.lastTimeUsed,
                    it.lastTimeVisible,
                    it.packageName,
                    it.totalTimeForegroundServiceUsed,
                    it.totalTimeInForeground,
                    it.totalTimeVisible
                )
            )
        }
        try {
            File(
                this.filesDir, ///保存場所は　data/data/[package_name]/files/
                "output_${beginCal.timeInMillis}_${System.currentTimeMillis()}.txt"
                //output_範囲のはじめ_書き込み処理を行った時間.txtの形式
            ).writeText(strWriter.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun showAppEventStatsLog(beginCal : Calendar){
        getAppEventStats(beginCal)?.let{list->
            list.forEach{
                Log.i(TAG,"""
                        count : ${it.count}
                        eventType : ${it.eventType}
                        firstTimeStamp : ${it.firstTimeStamp}
                        lastTimeVisible : ${it.lastTimeStamp}
                        """.trimIndent())

            }
        }
    }

    /**
     * 呼び出した日の0時00分00秒のunixTimeを返す．
     */
    private fun calculateDayBeginningInMillis(): Calendar {
        val cal = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Tokyo")
            add(Calendar.DAY_OF_MONTH,0)
            set(Calendar.HOUR_OF_DAY,0)
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }

        return  cal
    }

    /**
     * dayOffFromNowに指定した日分ずれた日を返す．
     * exp: dayOffFromNow = -5 なら今から5前の今日の00時00分00秒を返す
     * デフォルト引数は5日前になっている
     */

    private fun setDayInMillis(dayOffFromNow: Int = -5): Calendar {
        val cal = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Tokyo")
            add(Calendar.DAY_OF_MONTH,dayOffFromNow)
            set(Calendar.HOUR_OF_DAY,0)
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }

        return  cal
    }

    /**
     * monthOffFromNowに指定した月分ずれた日を返す．
     * exp: monthOffFromNow = -2 なら今から２か月前の今日の00時00分00秒を返す
     * デフォルト引数は２か月前になっている
     */
    private fun setMonthInMillis(monthOffFromNow: Int = -2): Calendar {
        val cal = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Tokyo")
            add(Calendar.MONTH,monthOffFromNow)
            set(Calendar.HOUR_OF_DAY,0)
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }

        return  cal
    }

    private fun checkForPermission() : Boolean{
        // AppOpsManagerを取得
        val aom = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        // GET_USAGE_STATSのステータスを取得
        val mode = aom.unsafeCheckOp(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        if (mode == AppOpsManager.MODE_DEFAULT) {
            // AppOpsの状態がデフォルトなら通常のpermissionチェックを行う。
            // 普通のアプリならfalse
            return checkPermission(
                "android.permission.PACKAGE_USAGE_STATS",
                Process.myPid(),
                Process.myUid()
            ) == PackageManager.PERMISSION_GRANTED;
        }
        // AppOpsの状態がデフォルトでないならallowedのみtrue
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    companion object{
        private const val TAG= "MainActivity"
    }

}