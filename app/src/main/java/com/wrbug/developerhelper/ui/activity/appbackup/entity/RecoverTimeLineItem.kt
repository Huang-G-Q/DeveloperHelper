package com.wrbug.developerhelper.ui.activity.appbackup.entity

data class RecoverTimeLineItem(
    val title: String,
    var lineType: Int,
    var status: Status = Status.Pending
) {
    enum class Status(val status: Int) {
        Pending(-1), Running(1), Done(0), Failed(2);

        companion object {
            fun get(status: Int): Status {
                return entries.find { it.status == status } ?: Pending
            }
        }
    }
}
