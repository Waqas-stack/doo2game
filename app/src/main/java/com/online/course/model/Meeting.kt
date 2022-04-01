package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Meeting() : Parcelable {


    @SerializedName("id")
    var id = 0

    @SerializedName("status")
    lateinit var status: String

    @SerializedName("H")
    var userPaidAmount = 0.0

    @SerializedName("amount")
    var amount = 0.0

    @SerializedName("date")
    var date = 0L

    @SerializedName("day")
    lateinit var day: String

    @SerializedName("time")
    lateinit var time: Time

    @SerializedName("user")
    lateinit var user: User

    @SerializedName("link")
    var link: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        status = parcel.readString()!!
        userPaidAmount = parcel.readDouble()
        amount = parcel.readDouble()
        date = parcel.readLong()
        day = parcel.readString()!!
        time = parcel.readParcelable(Time::class.java.classLoader)!!!!
        link = parcel.readString()
        user = parcel.readParcelable(User::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(status)
        parcel.writeDouble(userPaidAmount)
        parcel.writeDouble(amount)
        parcel.writeLong(date)
        parcel.writeString(day)
        parcel.writeParcelable(time, flags)
        parcel.writeString(link)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Meeting> {
        const val FINISHED = "finished"
        const val CANCELED = "canceled"

        override fun createFromParcel(parcel: Parcel): Meeting {
            return Meeting(parcel)
        }

        override fun newArray(size: Int): Array<Meeting?> {
            return arrayOfNulls(size)
        }
    }
}