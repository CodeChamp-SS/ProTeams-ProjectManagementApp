package com.saarthak.proteams.model

import android.os.Parcel
import android.os.Parcelable

data class SelectedMembers (
    var id: String = "",
    var img: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectedMembers> {
        override fun createFromParcel(parcel: Parcel): SelectedMembers {
            return SelectedMembers(parcel)
        }

        override fun newArray(size: Int): Array<SelectedMembers?> {
            return arrayOfNulls(size)
        }
    }
}