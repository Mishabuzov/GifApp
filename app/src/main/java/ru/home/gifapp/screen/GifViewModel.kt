package ru.home.gifapp.screen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.home.gifapp.GifEntity
import ru.home.gifapp.network.ApiFactory

class GifViewModel() : ViewModel() {

    private var pageNum = 0

    var gifIndex: Int = 0

    private val loadedGifs: MutableSet<GifEntity> = mutableSetOf()

    lateinit var refreshCallback: RefreshCallback

    lateinit var category: String

    fun getCurrentGif(): MutableLiveData<GifEntity?> = MutableLiveData(getGifByIndex(gifIndex))

    fun showNextGif() {
        if (gifIndex + 1 < loadedGifs.size) {
            getGifByIndex(++gifIndex)?.let {
                refreshCallback.refreshInterface(it, true)
            }
        } else {
            gifIndex++
            fetchGifs()
        }
    }

    fun showPreviousGif() {
        if (gifIndex - 1 >= 0) {
            getGifByIndex(--gifIndex)?.let { refreshCallback.refreshInterface(it, gifIndex != 0) }
        }
    }

    private fun getGifByIndex(index: Int): GifEntity? =
        if (loadedGifs.isEmpty()) null
        else loadedGifs.elementAt(index)

    fun fetchGifs(): Disposable =
        ApiFactory.gifService
            .fetchGifsByCategory(category, pageNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    loadedGifs.addAll(it.gifList)
                    refreshCallback.refreshInterface(getGifByIndex(gifIndex)!!, gifIndex != 0)
                    pageNum++
                },
                { error ->
                    refreshCallback.showNetworkErrorScreen()
                    Log.w(
                        "M_GifViewModel",
                        "Error getting Gifs from category $category:\n${error.message}",
                        error
                    )
                }
            )

    interface RefreshCallback {
        fun refreshInterface(gifEntity: GifEntity, isNeedToShowBackButton: Boolean)
        fun showNetworkErrorScreen()
    }

}