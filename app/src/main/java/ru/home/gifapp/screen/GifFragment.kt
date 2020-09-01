package ru.home.gifapp.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.buttons_layout.*
import kotlinx.android.synthetic.main.fragment_gif.*
import kotlinx.android.synthetic.main.gif_layout.*
import kotlinx.android.synthetic.main.network_error_layout.*
import ru.home.gifapp.GifEntity
import ru.home.gifapp.GifRequestListener
import ru.home.gifapp.R
import ru.home.gifapp.screen.GifActivity.ViewPagerFragmentAdapter.Categories.CATEGORY_KEY

class GifFragment : Fragment(), GifViewModel.RefreshCallback,
    GifRequestListener.GifLoadingCallback {

    private val gifViewModel by lazy { ViewModelProvider(this).get(GifViewModel::class.java) }

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
        hideNetworkErrorScreen()
        loadGif(gifEntity.gifURL)
        description_text_view.text = gifEntity.description
        if (isNeedToShowBackButton) {
            back_button.visibility = View.VISIBLE
        } else {
            back_button.visibility = View.INVISIBLE
        }
    }

    private fun loadGif(url: String) {
        showGifProgressBar()
        Glide.with(this)
            .asGif()
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .listener(GifRequestListener(this))
            .centerCrop()
            .into(gif_image_view)
    }

    private fun initActionButtons() {
        next_button.setOnClickListener { gifViewModel.showNextGif() }
        back_button.setOnClickListener { gifViewModel.showPreviousGif() }
        repeat_connection_button.setOnClickListener { gifViewModel.fetchGifs() }
    }

    private fun showGifProgressBar() {
        gif_progress_bar.visibility = View.VISIBLE
    }

    override fun hideGifProgressBar() {
        gif_progress_bar.visibility = View.INVISIBLE
    }

    override fun showNetworkErrorScreen() {
        network_error_layout.visibility = View.VISIBLE
    }

    private fun hideNetworkErrorScreen() {
        network_error_layout.visibility = View.GONE
    }

}