package com.wrbug.developerhelper.ui.activity.appbackup

import android.view.LayoutInflater
import android.view.ViewGroup
import com.wrbug.developerhelper.R
import com.wrbug.developerhelper.commonutil.AppInfoManager
import com.wrbug.developerhelper.commonutil.SpannableBuilder
import com.wrbug.developerhelper.commonutil.byteToShowSize
import com.wrbug.developerhelper.databinding.ItemBackupAppInfoBinding
import com.wrbug.developerhelper.model.entity.BackupAppData
import com.wrbug.developerhelper.ui.adapter.delegate.BaseItemViewBindingDelegate
import com.wrbug.developerhelper.util.BackupUtils
import com.wrbug.developerhelper.util.format
import com.wrbug.developerhelper.util.getColor
import com.wrbug.developerhelper.util.getString
import com.wrbug.developerhelper.util.loadImage
import com.wrbug.developerhelper.util.setOnDoubleCheckClickListener
import java.io.File

class BackupInfoItemDelegate(private val listener: (BackupAppData) -> Unit) :
    BaseItemViewBindingDelegate<BackupAppData, ItemBackupAppInfoBinding>() {
    override fun onBindViewHolder(binding: ItemBackupAppInfoBinding, item: BackupAppData) {
        val appInfo = AppInfoManager.getAppByPackageName(item.packageName)
        val title = if (appInfo == null) {
            val tag = R.string.backup_uninstalled_app_tag.getString()
            SpannableBuilder.with(binding.root.context, tag + " " + item.appName)
                .addSpanWithColor(tag, R.color.material_color_red_600.getColor()).build()
        } else if (appInfo.isSystemApp()) {
            val tag = R.string.backup_system_app_tag.getString()
            SpannableBuilder.with(binding.root.context, tag + " " + item.appName)
                .addSpanWithColor(tag, R.color.colorAccent.getColor()).build()
        } else {
            item.appName
        }

        binding.tvAppName.text = title
        binding.tvAppPackageName.text = item.packageName
        val size = item.backupMap.size
        val fileSize = item.backupMap.values.sumOf {
            File(
                BackupUtils.getAppBackupDir(it.packageName),
                it.backupFile
            ).length()
        }.byteToShowSize()
        binding.tvBackupCount.text = SpannableBuilder.with(
            binding.root.context,
            R.string.app_info_backup_count.getString(size, fileSize)
        ).addSpanWithBold(size.toString()).addSpanWithBold(fileSize).build()
        val time = item.backupMap.values.maxByOrNull { it.time }?.time ?: 0
        binding.tvLastBackupTime.text = R.string.last_backup_time.getString(time.format())
        binding.ivIcon.loadImage(item.iconPath, R.drawable.ic_default_app_ico_place_holder)
        binding.root.setOnDoubleCheckClickListener {
            listener(item)
        }
    }

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemBackupAppInfoBinding {
        return ItemBackupAppInfoBinding.inflate(inflater, parent, false)
    }
}