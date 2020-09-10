package ru.home.gifapp.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_gif.*
import ru.home.gifapp.R

class GifActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif)
        fragment_pager.adapter = ViewPagerFragmentAdapter(this)
        TabLayoutMediator(categories_tabs, fragment_pager) { tab, position ->
            tab.text = ViewPagerFragmentAdapter.categories[position]
        }.attach()
    }

    class ViewPagerFragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        companion object Categories {
            const val CATEGORY_KEY = "category_key"
            val categories: List<String> = listOf("latest", "top")
        }

        override fun getItemCount(): Int = categories.size

        override fun createFragment(position: Int): Fragment = GifFragment().apply {
            arguments = Bundle().apply {
                putString(CATEGORY_KEY, categories[position])
            }
        }

    }

}
