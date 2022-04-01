package com.online.course.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.online.course.R
import com.online.course.manager.Utils

class ChapterSessionItem() : Parcelable, CourseCommonItem {

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("description")
    lateinit var description: String

    @SerializedName("status")
    lateinit var status: String

    @SerializedName("zoom_start_link")
    var zoomStartLink: String? = null

    @SerializedName("link")
    var link: String? = null

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("date")
    var date = 0L

    @SerializedName("duration")
    var duration = 0

    @SerializedName("auth_has_read")
    var authHasRead: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        description = parcel.readString()!!
        status = parcel.readString()!!
        zoomStartLink = parcel.readString()
        link = parcel.readString()
        createdAt = parcel.readLong()
        date = parcel.readLong()
        duration = parcel.readInt()
        authHasRead = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(status)
        parcel.writeString(zoomStartLink)
        parcel.writeString(link)
        parcel.writeLong(createdAt)
        parcel.writeLong(date)
        parcel.writeInt(duration)
        parcel.writeByte(if (authHasRead) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChapterSessionItem> {
        override fun createFromParcel(parcel: Parcel): ChapterSessionItem {
            return ChapterSessionItem(parcel)
        }

        override fun newArray(size: Int): Array<ChapterSessionItem?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        return title
    }

    override fun desc(context: Context): String {
        return Utils.getDateTimeFromTimestamp(date)
    }

    override fun imgResource(context: Context): Int {
        return R.drawable.ic_video_white
    }

    override fun imgBgResource(context: Context): Int {
        return R.drawable.round_view_blue_corner10
    }

    override fun passed(): Boolean {
        return authHasRead
    }
}