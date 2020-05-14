package com.wrbug.developerhelper.ui.widget.layoutinfoview.infopage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wrbug.developerhelper.R
import com.wrbug.developerhelper.commonutil.print
import kotlinx.android.synthetic.main.item_view_info.view.*

class InfoAdapter(val context: Context) : RecyclerView.Adapter<InfoAdapter.ViewHolder>() {
    private val list = arrayListOf<ItemInfo>()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_info, p0, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setItems(list: ArrayList<ItemInfo>) {
        this.list.clear()
        if (list.isEmpty().not()) {
            this.list.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun addItem(index: Int, item: ItemInfo) {
        if (this.list.size < index) {
            return
        }
        list.add(index, item)
        notifyItemInserted(index)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val itemInfo = list[p1]
        p0.itemView.titleTv.text = itemInfo.title
        p0.itemView.contentTv.text = itemInfo.content.toString()
        p0.itemView.contentTv.setTextColor(itemInfo.textColor)
        p0.itemView.contentTv.setTextIsSelectable(itemInfo.clickListener == null)
        p0.itemView.contentTv.setOnClickListener {
            itemInfo.content.print()
            itemInfo.clickListener?.run {
                onClick(it)
            }
        }
    }

    fun notifyItemChanged(enforceItem: ItemInfo) {
        val index = list.indexOf(enforceItem)
        if (index >= 0) {
            notifyItemChanged(index)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}