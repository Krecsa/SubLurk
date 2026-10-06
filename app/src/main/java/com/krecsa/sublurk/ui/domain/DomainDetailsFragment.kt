package com.krecsa.sublurk.ui.domain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.krecsa.sublurk.databinding.FragmentDomainDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DomainDetailsFragment : Fragment() {

    private var _binding: FragmentDomainDetailsBinding? = null
    private val binding get() = _binding!!

    private var domain: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDomainDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        domain = arguments?.getString("domain").orEmpty()
        binding.toolbar.title = domain

        val adapter = DomainPagerAdapter(this, domain)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "DNS"
                1 -> "IP"
                2 -> "WHOIS"
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}