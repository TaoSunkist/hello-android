package com.zhimeng.battery.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.zhimeng.battery.R
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.ui.reusable.views.SpacesItemDecoration
import com.zhimeng.battery.utilities.Dimens
import com.zhimeng.battery.utilities.PermissionRequester
import com.zhimeng.battery.utilities.mediachooser.LocalMediaInfo
import com.zhimeng.battery.utilities.mediachooser.LocalMediaInfos
import com.zhimeng.battery.utilities.mediachooser.MediaChooser
import com.zhimeng.battery.utilities.mediachooser.MediaChooser.Companion.IMAGE_TYPE
import com.zhimeng.battery.utilities.observeOnMainThread
import com.zhimeng.battery.utilities.weak
import io.reactivex.rxkotlin.addTo
import java.io.File

interface GalleryViewControllerDelegate {
    fun galleryViewControllerShouldDismiss()
    fun galleryViewControllerDidSelect(file: File)
}

class GalleryViewController : BaseViewController(), GalleryAdapter.OnSelectedItem {

    companion object {
        const val TAG = "tagGallery"
    }

    @ViewRes(res = R.id.common_id_toolbar)
    lateinit var toolbar: Toolbar

    @ViewRes(res = R.id.common_id_recycler_view)
    lateinit var recyclerView: RecyclerView

    @ViewRes(res = R.id.view_controller_gallery_no_permission_button)
    lateinit var popupGalleryNoPermissionButton: AppCompatButton

    lateinit var adapter: GalleryAdapter

    var delegate: GalleryViewControllerDelegate? by weak()

    init {
        tag = TAG
        presentationStyle.animation = PresentingAnimation.BOTTOM
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_gallery, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)
        setupToolbar()

//        view.setPadding(0, Dimens.safeArea.top, 0, 0)
        recyclerView.addItemDecoration(
            SpacesItemDecoration(
                Dimens.marginSmall,
                Dimens.marginSmall,
                3
            )
        )
        adapter = GalleryAdapter(context)
        recyclerView.adapter = adapter
        adapter.setOnSelectedItem(this)

        PermissionRequester.getReadExternalStorage(context as AppCompatActivity)
            .observeOnMainThread(
                onSuccess = { loadLocalMedia() },
                onError = { popupGalleryNoPermissionButton.visibility = View.VISIBLE }
            ).addTo(compositeDisposable = compositeDisposable)
    }

    private fun loadLocalMedia() {
        MediaChooser(IMAGE_TYPE, context as AppCompatActivity).loadAllMedia(object :
            MediaChooser.LocalMediaLoadListener {
            override fun localMediaLoadComplete(localMediaInfos: LocalMediaInfos) {
                adapter.notifyDataSetChanged(localMediaInfos)
            }
        })
    }

    private fun setupToolbar() {
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black)
            setNavigationOnClickListener {
                delegate?.galleryViewControllerShouldDismiss()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        delegate?.galleryViewControllerShouldDismiss()
        return true
    }

    override fun onSelectedItem(localMediaInfo: LocalMediaInfo) {
        delegate?.galleryViewControllerDidSelect(File(localMediaInfo.path))
    }

}