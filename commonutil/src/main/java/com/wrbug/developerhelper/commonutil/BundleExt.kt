package com.wrbug.developerhelper.commonutil

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import java.io.Serializable


inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String): T? {
    return BundleCompat.getParcelable(this, key, T::class.java)
}

inline fun <reified T : Serializable> Bundle.getSerializableCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= 33) {
        getSerializable(key, T::class.java)
    } else {
        getSerializable(key) as T?
    }
}

inline fun <reified T : Parcelable> Intent.getParcelableCompat(key: String): T? {
    return IntentCompat.getParcelableExtra(this, key, T::class.java)
}

inline fun <reified T : Parcelable> Intent.getParcelableArrayListCompat(key: String): List<T>? {
    return IntentCompat.getParcelableArrayListExtra(this, key, T::class.java)
}

inline fun <reified T : Parcelable> Bundle.getParcelableArrayListCompat(key: String): List<T>? {
    return BundleCompat.getParcelableArrayList(this, key, T::class.java)
}