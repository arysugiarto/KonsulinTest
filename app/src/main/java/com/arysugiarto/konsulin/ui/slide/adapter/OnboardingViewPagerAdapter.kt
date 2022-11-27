package com.arysugiarto.konsulin.ui.slide.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arysugiarto.konsulin.ui.slide.*

class OnboardingViewPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    private val pages=
        listOf(
            ConditionFragment(),
            ProblemFragment(),
            YearsFragment(),
            LocationFragment(),
            TakeSelfieFragment()
        )

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }

    private val pagesHash = pages.map { it.hashCode().toLong() }

    override fun getItemId(position: Int): Long = pages[position].hashCode().toLong()

    override fun containsItem(itemId: Long) = pagesHash.contains(itemId)

}