package com.a2l.software.myapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.a2l.software.myapplication.model.ImageEntity

class ScreenSlidePagerAdapter(
    fa: FragmentActivity,
    private val listUriImageSelected: ArrayList<ImageEntity>
) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = listUriImageSelected.size

    override fun createFragment(position: Int): Fragment {
        return ImageFragment.instance(listUriImageSelected[position])
    }
}