package com.wrbug.developerhelper.ui.activity.appbackup.entity

data class RecoverTimeLineItem(
    val title: String,
    var content: String,
    var lineType: Int,
    var status: Status = Status.Pending
) {
    enum class Status {
        Pending, Running, Done
    }
}
