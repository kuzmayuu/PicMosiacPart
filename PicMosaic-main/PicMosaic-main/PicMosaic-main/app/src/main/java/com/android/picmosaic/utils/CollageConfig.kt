package com.android.picmosaic.utils

import android.graphics.Color
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.android.picmosaic.CollageAdapter

data class CollageConfig(
    val imageUris: List<String>, // List of URI strings (since Uri isn't Parcelable directly)
    val spanCount: Int,
    val borderWidth: Float,
    val cornerRadius: Float,
    val backgroundColor: Int,
    val imageTransformations: Map<Int, CollageAdapter.ImageTransformation> = emptyMap()
) : Parcelable {
    fun getUris(): List<Uri> = imageUris.map { Uri.parse(it) }

    // Constructor for reading from a Parcel
    constructor(parcel: Parcel) : this(
        imageUris = parcel.createStringArrayList() ?: listOf(),
        spanCount = parcel.readInt(),
        borderWidth = parcel.readFloat(),
        cornerRadius = parcel.readFloat(),
        backgroundColor = parcel.readInt(),
        imageTransformations = mutableMapOf<Int, CollageAdapter.ImageTransformation>().apply {
            val size = parcel.readInt()
            repeat(size) {
                val key = parcel.readInt()
                val scale = parcel.readFloat()
                val tx = parcel.readFloat()
                val ty = parcel.readFloat()
                put(key, CollageAdapter.ImageTransformation(scale, tx, ty))
            }
        }
    )

    // Write the object's data to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(imageUris)
        parcel.writeInt(spanCount)
        parcel.writeFloat(borderWidth)
        parcel.writeFloat(cornerRadius)
        parcel.writeInt(backgroundColor)
        parcel.writeInt(imageTransformations.size)
        imageTransformations.forEach { (key, value) ->
            parcel.writeInt(key)
            parcel.writeFloat(value.scaleFactor)
            parcel.writeFloat(value.translationX)
            parcel.writeFloat(value.translationY)
        }
    }

    // Describe the kinds of special objects contained in this Parcelable (none in this case)
    override fun describeContents(): Int = 0

    // Companion object with CREATOR for creating instances from a Parcel
    companion object CREATOR : Parcelable.Creator<CollageConfig> {
        override fun createFromParcel(parcel: Parcel): CollageConfig {
            return CollageConfig(parcel)
        }

        override fun newArray(size: Int): Array<CollageConfig?> {
            return arrayOfNulls(size)
        }
    }
}