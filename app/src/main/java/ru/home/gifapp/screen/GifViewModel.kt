package ru.home.gifapp.screen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.home.gifapp.Config.DEFAULT_CATEGORY
import ru.home.gifapp.GifEntity
import ru.home.gifapp.network.ApiFactory

class GifViewModel(private val refreshCallback: RefreshCallback) : ViewModel() {

    private var pageNum = 0

    var gifIndex: Int = 0

    private val loadedGifs: MutableSet<GifEntity> = mutableSetOf()

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

    fun fetchGifs(category: String = DEFAULT_CATEGORY) {
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
                {
                    refreshCallback.showNetworkErrorScreen()
                    Log.w(
                        "M_GifViewModel",
                        "Error getting Gifs from category $category:\n${it.message}",
                        it
                    )
                }
            )
    }

    class GifViewModelFactory(private val refreshCallback: RefreshCallback) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(RefreshCallback::class.java)
                .newInstance(refreshCallback)
        }
    }

    interface RefreshCallback {
        fun refreshInterface(gifEntity: GifEntity, isNeedToShowBackButton: Boolean)
        fun showNetworkErrorScreen()
    }

}