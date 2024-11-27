package com.wrbug.developerhelper.ui.activity.appbackup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import com.wrbug.developerhelper.R
import com.wrbug.developerhelper.base.BaseActivity
import com.wrbug.developerhelper.base.ExtraKey
import com.wrbug.developerhelper.base.versionCodeLong
import com.wrbug.developerhelper.commonutil.AppInfoManager
import com.wrbug.developerhelper.commonutil.getParcelableCompat
import com.wrbug.developerhelper.databinding.ActivityAppRecoverBinding
import com.wrbug.developerhelper.model.entity.BackupAppItemInfo
import com.wrbug.developerhelper.ui.activity.appbackup.entity.RecoverTimeLineItem
import com.wrbug.developerhelper.ui.adapter.ExMultiTypeAdapter
import com.wrbug.developerhelper.util.getString
import com.wrbug.developerhelper.util.setOnDoubleCheckClickListener

class AppRecoverActivity : BaseActivity() {

    companion object {
        fun start(context: Context, backupAppItemInfo: BackupAppItemInfo) {
            context.startActivity(Intent(context, AppRecoverActivity::class.java).apply {
                putExtra(ExtraKey.KEY_1, backupAppItemInfo as Parcelable)
            })
        }
    }

    private val backupAppItemInfo: BackupAppItemInfo? by lazy {
        intent?.getParcelableCompat(ExtraKey.KEY_1)
    }
    private val apkInfo by lazy {
        AppInfoManager.getAppByPackageName(backupAppItemInfo?.packageName.orEmpty())
    }
    private val adapter by ExMultiTypeAdapter.get()

    private val binding by lazy {
        ActivityAppRecoverBinding.inflate(layoutInflater)
    }
    private val initItem = RecoverTimeLineItem("初始化准备", "等待执行", 1)
    private val timeLineList: ArrayList<RecoverTimeLineItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initListener()
        loadData()
    }

    private fun loadData() {
        if (apkInfo == null && backupAppItemInfo?.backupApk == true) {
            binding.cbApk.isChecked = true
            binding.cbApk.isEnabled = false
        }
    }

    private fun initListener() {
        binding.cbApk.setOnCheckedChangeListener { _, _ ->
            updateItemList()
        }
        binding.cbData.setOnCheckedChangeListener { _, _ ->
            updateItemList()
        }
        binding.cbAndroidData.setOnCheckedChangeListener { _, _ ->
            updateItemList()
        }
        binding.btnRecover.setOnDoubleCheckClickListener {
            apkVersionCheck()
        }
    }

    private fun apkVersionCheck() {
        if (!binding.cbApk.isChecked && apkInfo != null && backupAppItemInfo?.versionCode != apkInfo?.packageInfo?.versionCodeLong) {
            showDialog(
                R.string.notice,
                R.string.apk_version_backup_version_not_same_notice,
                R.string.ok,
                R.string.cancel, {
                    systemAppCheck()
                    dismiss()
                }, {
                    dismiss()
                }
            )
            return
        }
        systemAppCheck()
    }

    private fun systemAppCheck() {
        startRecover()
    }

    private fun startRecover() {

    }

    private fun updateItemList() {
        timeLineList.clear()
        if (!binding.cbApk.isChecked && !binding.cbData.isChecked && !binding.cbAndroidData.isChecked) {
            binding.btnRecover.isEnabled = false
            adapter.loadData(timeLineList)
            return
        }
        binding.btnRecover.isEnabled = true
        timeLineList.add(initItem)
        if (binding.cbApk.isChecked) {
            timeLineList.add(
                RecoverTimeLineItem(
                    R.string.time_line_recover_title.getString(R.string.item_backup_apk.getString()),
                    "等待执行",
                    0
                )
            )
        }
        if (binding.cbData.isChecked) {
            timeLineList.add(
                RecoverTimeLineItem(
                    R.string.time_line_recover_title.getString(R.string.item_backup_data.getString()),
                    "等待执行",
                    0
                )
            )
        }
        if (binding.cbAndroidData.isChecked) {
            timeLineList.add(
                RecoverTimeLineItem(
                    R.string.time_line_recover_title.getString(R.string.item_backup_android_data.getString()),
                    "等待执行",
                    0
                )
            )
        }
        timeLineList.last().lineType = 2
        adapter.loadData(timeLineList)
    }

    private fun initView() {
        binding.cbApk.isEnabled = backupAppItemInfo?.backupApk == true
        binding.cbData.isEnabled = backupAppItemInfo?.backupData == true
        binding.cbAndroidData.isEnabled = backupAppItemInfo?.backupAndroidData == true
        binding.rvTimeLine.layoutManager = LinearLayoutManager(this)
        binding.rvTimeLine.adapter = adapter
        adapter.register(RecoverTimeLineDelegate())
        adapter.loadData(timeLineList)
    }
}