package com.example.usagestatsmanagersample

import com.opencsv.bean.CsvBindByName

data class UsageStatsData(
    @CsvBindByName(column = "FirstTimeStamp", required = true)
    val firstTimeStamp: Long = 0L,

    @CsvBindByName(column = "LastTimeForegroundServiceUsed", required = true)
    val lastTimeForegroundServiceUsed: Long = 0L,

    @CsvBindByName(column = "LastTimeStamp", required = true)
    val lastTimeStamp: Long = 0,

    @CsvBindByName(column = "LastTimeUsed", required = true)
    val lastTimeUsed: Long = 0,

    @CsvBindByName(column = "LastTimeVisible", required = true)
    val lastTimeVisible: Long = 0,

    @CsvBindByName(column = "PackageName", required = true)
    val packageName: String = "",

    @CsvBindByName(column = "TotalTimeForegroundServiceUsed", required = true)
    val totalTimeForegroundServiceUsed: Long = 0,

    @CsvBindByName(column = "TotalTimeInForeground", required = true)
    val totalTimeInForeground: Long = 0,

    @CsvBindByName(column = "TotalTimeVisible", required = true)
    val totalTimeVisible: Long = 0,

)
