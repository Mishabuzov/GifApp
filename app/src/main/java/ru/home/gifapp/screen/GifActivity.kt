package ru.home.gifapp.screen

import android.os.Bundle
import android.view.View
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_gif.*
import kotlinx.android.synthetic.main.buttons_layout.*
import kotlinx.android.synthetic.main.gif_layout.*
import ru.home.gifapp.GifEntity
import ru.home.gifapp.GifRequestListener
import ru.home.gifapp.R

class GifActivity : AppCompatActivity(), GifViewModel.RefreshCallback,
    GifRequestListener.GifLoadingCallback {

    private val gifViewModel by lazy {
        ViewModelProvider(this, GifViewModel.GifViewModelFactory(this))
            .get(GifViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif)

        val gifLiveData: MutableLiveData<GifEntity?> = gifViewModel.getCurrentGif()
        gifLiveData.observe(this, {
            it?.let { refreshInterface(it, gifViewModel.gifIndex != 0) }
        })
        if (gifLiveData.value == null) {
            gifViewModel.fetchGifs()
        }
    }

    override fun refreshInterface(gifEntity: GifEntity, isNeedToShowBackButton: Boolean) {
        hideNetworkErrorScreen()
        loadGif(gifEntity.gifURL)
        description_text_view.text = gifEntity.description
        if (isNeedToShowBackButton) {
            back_button.visibility = VISIBLE
        } else {
            back_button.visibility = INVISIBLE
        }
    }

    fun onNextButtonClick(view: View) {
        gifViewModel.showNextGif()
    }

    fun onBackButtonClick(view: View) {
        gifViewModel.showPreviousGif()
    }

    fun onRepeatConnectionButtonPressed(view: View) {
        gifViewModel.fetchGifs()
    }

    private fun loadGif(url: String) {
        showGifProgressBar()
        Glide.with(this).asGif()
            .load(url)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .listener(GifRequestListener(this))
            .centerCrop()
            .into(gif_image_view)
    }

    private fun showGifProgressBar() {
        gif_progress_bar.visibility = VISIBLE
    }

    override fun hideGifProgressBar() {
        gif_progress_bar.visibility = INVISIBLE
    }

    override fun showNetworkErrorScreen() {
        network_error_layout.visibility = VISIBLE
    }

    private fun hideNetworkErrorScreen() {
        network_error_layout.visibility = GONE
    }

}
