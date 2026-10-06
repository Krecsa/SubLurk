package com.krecsa.sublurk.ui.domain

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DomainPagerAdapter(
    fragment: Fragment,
    private val domain: String,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DnsTabFragment.newInstance(domain)
            1 -> IpTabFragment.newInstance(domain)
            2 -> WhoisTabFragment.newInstance(domain)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }
}