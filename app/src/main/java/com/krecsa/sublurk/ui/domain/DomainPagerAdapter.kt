package com.krecsa.sublurk.ui.domain

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DomainPagerAdapter(
    fragment: Fragment,
    private val domain: String,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubdomainsTabFragment.newInstance(domain)
            1 -> DnsTabFragment.newInstance(domain)
            2 -> IpTabFragment.newInstance(domain)
            3 -> WhoisTabFragment.newInstance(domain)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }
}