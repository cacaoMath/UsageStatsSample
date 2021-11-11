package com.example.usagestatsmanagersample

import com.opencsv.bean.CsvBindByName

data class EventStatsData(
    @CsvBindByName(column = "Count", required = true)
    val count: Int = 0,

    @CsvBindByName(column = "EventType", required = true)
    val eventType: Int = 0,

    @CsvBindByName(column = "FirstTimeStamp", required = true)
    val firstTimeStamp: Long = 0,

    @CsvBindByName(column = "LastEventTime", required = true)
    val lastEventTime: Long = 0,

    @CsvBindByName(column = "LastTimeStamp", required = true)
    val lastTimeStamp: Long = 0,

    @CsvBindByName(column = "TotalTime", required = true)
    val totalTime: Long = 0,
)
