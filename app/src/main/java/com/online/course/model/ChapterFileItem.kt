package com.online.course.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.online.course.R
import com.online.course.manager.Utils.toBoolean

class ChapterFileItem() : Parcelable, CourseCommonItem {

    enum class Storage(val value: String) {
        LOCAL("local"),
        ONLINE("online")
    }

    enum class Accessibility(val value: String) {
        FREE("free"),
        PAID("paid")
    }

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    var title: String = ""

    @SerializedName("description")
    var description: String? = null

    @SerializedName("file")
    lateinit var file: String

    @SerializedName("volume")
    lateinit var volume: String

    @SerializedName("file_type")
    lateinit var fileType: String

    @SerializedName("storage")
    lateinit var storage: String

    @SerializedName("status")
    lateinit var status: String

    @SerializedName("accessibility")
    lateinit var accessibility: String

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("downloadable")
    var downloadable = 0

    @SerializedName("auth_has_read")
    var authHasRead = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        description = parcel.readString()
        file = parcel.readString()!!
        volume = parcel.readString()!!
        fileType = parcel.readString()!!
        storage = parcel.readString()!!
        status = parcel.readString()!!
        accessibility = parcel.readString()!!
        createdAt = parcel.readLong()
        downloadable = parcel.readInt()
        authHasRead = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(file)
        parcel.writeString(volume)
        parcel.writeString(fileType)
        parcel.writeString(storage)
        parcel.writeString(status)
        parcel.writeString(accessibility)
        parcel.writeLong(createdAt)
        parcel.writeInt(downloadable)
        parcel.writeByte(if (authHasRead) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChapterFileItem> {
        const val VIDEO_FILE_TYPE = "VIDEO"

        override fun createFromParcel(parcel: Parcel): ChapterFileItem {
            return ChapterFileItem(parcel)
        }

        override fun newArray(size: Int): Array<ChapterFileItem?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        return title
    }

    override fun desc(context: Context): String {
        return volume
    }

    override fun imgResource(context: Context): Int {
        if (storage == Storage.LOCAL.value && downloadable.toBoolean()) {
            return R.drawable.ic_download_white
        }
        return R.drawable.ic_play2
    }

    override fun imgBgResource(context: Context): Int {
        return R.drawable.round_view_light_green_corner10
    }

    override fun passed(): Boolean {
        return authHasRead
    }
}