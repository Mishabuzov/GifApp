package ru.home.gifapp.screen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.buttons_layout.*
import kotlinx.android.synthetic.main.fragment_gif.*
import kotlinx.android.synthetic.main.gif_error_layout.*
import kotlinx.android.synthetic.main.gif_layout.*
import kotlinx.android.synthetic.main.network_error_layout.*
import ru.home.gifapp.Config.SMALL_DELAY_MS
import ru.home.gifapp.GifEntity
import ru.home.gifapp.GifRequestListener
import ru.home.gifapp.R
import ru.home.gifapp.screen.GifActivity.ViewPagerFragmentAdapter.Categories.CATEGORY_KEY

class GifFragment : Fragment(), GifViewModel.RefreshCallback,
    GifRequestListener.GifLoadingCallback {

    private val gifViewModel by lazy { ViewModelProvider(this).get(GifViewModel::class.java) }

    private val gifProgressDrawable by lazy {
        val circularProgressDrawable = CircularProgressDrawable(context!!)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.start()
        circularProgressDrawable
    }

    private val gifRequestListener = GifRequestListener(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_gif, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModelArguments()
        initLiveData()
        initActionButtons()
    }

    private fun setViewModelArguments() {
        gifViewModel.category = arguments?.getString(CATEGORY_KEY)!!
        gifViewModel.refreshCallback = this
    }

    private fun initLiveData() {
        val gifLiveData: MutableLiveData<GifEntity?> = gifViewModel.getCurrentGif()
        gifLiveData.observe(viewLifecycleOwner, {
            it?.let {
                refreshInterface(it, gifViewModel.gifIndex != 0)
            }
        })
        if (gifLiveData.value == null) {
            gifViewModel.fetchGifs()
        }
    }

    override fun refreshInterface(gifEntity: GifEntity, isNeedToShowBackButton: Boolean) {
        loadGif(gifEntity.gifURL)
        description_text_view.text = gifEntity.description
        if (isNeedToShowBackButton) {  // TODO: Is need to show next button!
            back_button.visibility = VISIBLE
        } else {
            back_button.visibility = INVISIBLE
        }
    }

    private fun loadGif(url: String) =
        Glide.with(this)
            .asGif()
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .skipMemoryCache(true)
            .listener(gifRequestListener)
            .centerCrop()
            .placeholder(gifProgressDrawable)
            .into(gif_image_view)

    private fun initActionButtons() {
        next_button.setOnClickListener { gifViewModel.showNextGif() }
        back_button.setOnClickListener { gifViewModel.showPreviousGif() }
    }

    override fun showNetworkErrorScreen() {
        gif_request_loading.visibility = GONE
        repeat_connection_button.visibility = VISIBLE
        network_error_layout.visibility = VISIBLE
        repeat_connection_button.setOnClickListener {
            repeat_connection_button.visibility = GONE
            gif_request_loading.visibility = VISIBLE
            makeActionWithSmallDelay { gifViewModel.fetchGifs() }
        }
    }

    override fun hideReloadGifScreen() {
        gif_error_layout.visibility = GONE
    }

    private fun makeActionWithSmallDelay(action: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({ action() }, SMALL_DELAY_MS)
    }

    override fun showReloadGifScreen(url: String) {
        gif_error_progress_bar.visibility = GONE
        reload_gif_button.visibility = VISIBLE
        gif_error_layout.visibility = VISIBLE
        reload_gif_button.setOnClickListener {
            it.visibility = GONE
            gif_error_progress_bar.visibility = VISIBLE
            makeActionWithSmallDelay { if (isAdded) loadGif(url) }
        }
    }

    override fun hideNetworkErrorScreen() {
        network_error_layout.visibility = GONE
    }

}