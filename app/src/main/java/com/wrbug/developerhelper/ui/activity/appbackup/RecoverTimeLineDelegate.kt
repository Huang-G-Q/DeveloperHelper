package com.wrbug.developerhelper.ui.activity.appbackup

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.github.vipulasri.timelineview.TimelineView
import com.wrbug.developerhelper.R
import com.wrbug.developerhelper.databinding.ItemRecoverAppTimeLineBinding
import com.wrbug.developerhelper.ui.activity.appbackup.entity.RecoverTimeLineItem
import com.wrbug.developerhelper.ui.adapter.delegate.BaseItemViewBindingDelegate

class RecoverTimeLineDelegate :
    BaseItemViewBindingDelegate<RecoverTimeLineItem, ItemRecoverAppTimeLineBinding>() {
    override fun onBindViewHolder(
        binding: ItemRecoverAppTimeLineBinding,
        item: RecoverTimeLineItem
    ) {
        binding.textTimelineTitle.text = item.title
        binding.textTimelineContent.text = item.content
        binding.timeMarker.initLine(item.lineType)
        when (item.status) {
            RecoverTimeLineItem.Status.Pending -> {
                binding.timeMarker.setMarker(
                    R.drawable.ic_marker_inactive,
                    R.color.material_color_grey_500
                )
            }

            RecoverTimeLineItem.Status.Running -> {
                binding.timeMarker.setMarker(
                    R.drawable.ic_marker_active,
                    R.color.material_color_grey_500
                )
            }

            RecoverTimeLineItem.Status.Done -> {
                binding.timeMarker.setMarker(
                    R.drawable.ic_marker,
                    R.color.material_color_grey_500
                )
            }
        }
    }

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemRecoverAppTimeLineBinding {
        return ItemRecoverAppTimeLineBinding.inflate(inflater, parent, false)
    }

    private fun TimelineView.setMarker(drawableResId: Int, colorFilter: Int) {
        marker = VectorDrawableCompat.create(
            context.resources,
            drawableResId,
            context.theme
        )?.apply {
            setColorFilter(colorFilter, PorterDuff.Mode.DST_IN)
        }
    }
}