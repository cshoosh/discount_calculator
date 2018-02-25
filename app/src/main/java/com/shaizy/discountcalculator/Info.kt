package com.shaizy.discountcalculator

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by syedshahnawazali on 25/02/2018.
 *
 */

data class Info(var retail: Int = 0, var discountStore: Int = 15, var client: Int = 0, var discountGiven: Int = 0,
                var uri: String = "", var details:String = "") : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(retail)
        parcel.writeInt(discountStore)
        parcel.writeInt(client)
        parcel.writeInt(discountGiven)
        parcel.writeString(uri)
        parcel.writeString(details)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Info> {
        override fun createFromParcel(parcel: Parcel): Info {
            return Info(parcel)
        }

        override fun newArray(size: Int): Array<Info?> {
            return arrayOfNulls(size)
        }
    }
}