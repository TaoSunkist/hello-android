package com.example.tatame_frontline.ui.home.viewholders

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.tatame_frontline.R
import com.example.tatame_frontline.ui.home.adapters.IdolsListViewPagerAdapter
import com.example.tatame_frontline.ui.home.models.IdolsViewPagerUIModel
import com.flyco.tablayout.CommonTabLayout

class IdolsViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val ITEM_TYPE = 1
    }

    private var commonTabLayout: CommonTabLayout = itemView.findViewById(R.id.pager_fragment_home_idols_common_tab_layout)
    private var viewPager: ViewPager2 = itemView.findViewById(R.id.pager_fragment_home_idols_view_pager)

    init {
        val uiModel = IdolsViewPagerUIModel.fake()
        commonTabLayout.setTabData(uiModel.tabEntities)
    }

    fun bind() {
        viewPager.adapter = IdolsListViewPagerAdapter()
    }
}

class ProfileViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val ITEM_TYPE = 2
    }

    fun bind() {
    }
}

/* idol的列表 */
class IdolsListViewHolder(itemView: RecyclerView) : RecyclerView.ViewHolder(itemView) {
    companion object {
        val colors = listOf(Color.BLUE, Color.GREEN)
    }

    private val recyclerView = itemView

    init {
        recyclerView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        recyclerView.layoutManager = GridLayoutManager(itemView.context, 2, GridLayoutManager.HORIZONTAL, false)
    }

    fun bind(position: Int) {
        recyclerView.setBackgroundColor(colors[position])
    }
}

class IdolListCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
