package com.example.myfirstapp.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myfirstapp.Presentation.Fragments.GuestFragment.FloorFragment

class FloorPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = arrayOf(
        FloorFragment.newInstance(1),
        FloorFragment.newInstance(2),
        FloorFragment.newInstance(3)
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}