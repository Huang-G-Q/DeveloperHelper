package com.wrbug.developerhelper.ui.activity.appbackup.worker

import android.os.Parcelable
import com.wrbug.developerhelper.model.entity.BackupAppItemInfo
import kotlinx.parcelize.Parcelize

@Parcelize
class AppRecoverWorkerData(
    val appItemInfo: BackupAppItemInfo?,
    val appName: String,
    val recoverApk: Boolean,
    val recoverData: Boolean,
    val recoverAndroidData: Boolean
) : Parcelable
