package com.zhimeng.battery.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.zhimeng.battery.R
import com.zhimeng.battery.utilities.Dimens
import com.zhimeng.battery.utilities.mediachooser.LocalMediaInfo
import com.zhimeng.battery.utilities.mediachooser.LocalMediaInfos
import java.io.File

class GalleryAdapter(private val context: Context, private val localMediaInfos: LocalMediaInfos = LocalMediaInfos()) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cell_gallery_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = localMediaInfos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(File(localMediaInfos[position].path)).resize(Dimens.screenWidth / 3, Dimens.screenWidth / 3).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.placeholder_user).into(holder.ivPicture)
        holder.ivPicture.setOnClickListener { onSelectedItem.onSelectedItem(localMediaInfos[position]) }
    }

    inner class ViewHolder(contentView: View) : RecyclerView.ViewHolder(contentView) {
        val ivPicture: ImageView = contentView.findViewById<View>(R.id.iv_picture) as ImageView
    }

    fun notifyDataSetChanged(localMediaInfos: LocalMediaInfos) {
        this.localMediaInfos.clear()
        this.localMediaInfos.addAll(localMediaInfos)
        this.notifyDataSetChanged()
    }

    interface OnSelectedItem {
        fun onSelectedItem(localMediaInfo: LocalMediaInfo)
    }

    private lateinit var onSelectedItem: OnSelectedItem

    fun setOnSelectedItem(onSelectedItem: OnSelectedItem) {
        this.onSelectedItem = onSelectedItem
    }
}
