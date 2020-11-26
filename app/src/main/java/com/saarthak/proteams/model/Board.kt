package com.saarthak.proteams.model

import android.os.Parcel
import android.os.Parcelable

data class Board(
    val name: String = "",
    val img: String = "",
    val author: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val tasks: ArrayList<Task> = ArrayList(),
    var docId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(img)
        parcel.writeString(author)
        parcel.writeStringList(assignedTo)
        parcel.writeTypedList(tasks)
        parcel.writeString(docId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}