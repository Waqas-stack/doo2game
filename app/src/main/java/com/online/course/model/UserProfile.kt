package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserProfile : User, Parcelable {

    @SerializedName("students_count")
    var studentsCount = 0

    @SerializedName("courses_count")
    var coursesCount = 0

    @SerializedName("reviews_count")
    var reviewsCount = 0

    @SerializedName("appointments_count")
    var appointmentsCount = 0

    @SerializedName("verified")
    var verified = 0

    @SerializedName("followers")
    lateinit var followers: List<User>

    @SerializedName("webinars")
    lateinit var courses: List<Course>

    @SerializedName("badges")
    lateinit var badges: List<UserBadge>

    @SerializedName("organization_teachers")
    lateinit var organizationTeachers: List<User>

    @SerializedName("auth_user_is_follower")
    var userIsFollower = false

    @SerializedName("offline_message")
    var offlineMessage: String? = null

    @SerializedName("education")
    lateinit var educations: List<String>

    @SerializedName("experience")
    lateinit var experiences: List<String>

    @SerializedName("occupations")
    lateinit var occupations: List<String>

    @SerializedName("meeting")
    var meetingReserve: MeetingReserve? = null

    @SerializedName("user_group")
    var userGroup: UserGroup? = null

    @SerializedName("account_type")
    var accountType: String? = null

    @SerializedName("iban")
    var iban: String? = null

    @SerializedName("account_id")
    var accountId: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("identity_scan")
    var identityScan: String? = null

    @SerializedName("certificate")
    var certificate: String? = null

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        studentsCount = parcel.readInt()
        coursesCount = parcel.readInt()
        reviewsCount = parcel.readInt()
        verified = parcel.readInt()
        appointmentsCount = parcel.readInt()
        meetingReserve = parcel.readParcelable(MeetingReserve::class.java.classLoader)
        educations = parcel.createStringArrayList()!!
        experiences = parcel.createStringArrayList()!!
        occupations = parcel.createStringArrayList()!!
        userIsFollower = parcel.readByte() != 0.toByte()
        userGroup = parcel.readParcelable(UserGroup::class.java.classLoader)
        accountType = parcel.readString()
        iban = parcel.readString()
        accountId = parcel.readString()
        address = parcel.readString()
        identityScan = parcel.readString()
        certificate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(studentsCount)
        parcel.writeInt(coursesCount)
        parcel.writeInt(reviewsCount)
        parcel.writeInt(appointmentsCount)
        parcel.writeStringList(educations)
        parcel.writeStringList(experiences)
        parcel.writeStringList(occupations)
        parcel.writeParcelable(meetingReserve, flags)
        parcel.writeInt(verified)
        parcel.writeString(accountType)
        parcel.writeString(iban)
        parcel.writeString(accountId)
        parcel.writeString(address)
        parcel.writeString(certificate)
        parcel.writeString(identityScan)
        parcel.writeByte(if (userIsFollower) 1 else 0)
        parcel.writeParcelable(userGroup, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserProfile> {
        override fun createFromParcel(parcel: Parcel): UserProfile {
            return UserProfile(parcel)
        }

        override fun newArray(size: Int): Array<UserProfile?> {
            return arrayOfNulls(size)
        }
    }

}