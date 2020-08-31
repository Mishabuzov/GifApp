package ru.home.gifapp

import android.util.Log
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class GifRequestListener(private val gifLoadingCallback: GifLoadingCallback) :
    RequestListener<GifDrawable> {

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<GifDrawable>?,
        isFirstResource: Boolean
    ): Boolean {
        Log.w("M_GifRequestListener", "Error Loading gif\n${e?.message}", e)
        gifLoadingCallback.showNetworkErrorScreen()
        return false
    }

    override fun onResourceReady(
        resource: GifDrawable?,
        model: Any?,
        target: Target<GifDrawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        gifLoadingCallback.hideGifProgressBar()
        return false
    }

    interface GifLoadingCallback {
        fun hideGifProgressBar()
        fun showNetworkErrorScreen()
    }

}