package com.krecsa.sublurk.ui.domain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.krecsa.sublurk.databinding.FragmentIpTabBinding

class IpTabFragment : Fragment() {

    private var _binding: FragmentIpTabBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_DOMAIN = "domain"
        fun newInstance(domain: String) = DnsTabFragment().apply {
            arguments = Bundle().apply { putString(ARG_DOMAIN, domain) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentIpTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}