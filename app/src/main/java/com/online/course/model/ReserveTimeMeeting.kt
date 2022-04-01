package com.online.course.model

import com.google.gson.annotations.SerializedName

class ReserveTimeMeeting() {
    @SerializedName("date")
    lateinit var date: String

    @SerializedName("time_id")
    var timeId = 0
}