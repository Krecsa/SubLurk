package com.krecsa.sublurk.ui.domain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.krecsa.sublurk.R
import com.krecsa.sublurk.databinding.FragmentDnsTabBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DnsTabFragment : Fragment() {

    private var _binding: FragmentDnsTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DnsTabViewModel by viewModels()
    private val adapter = DnsRecordAdapter()

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
        _binding = FragmentDnsTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val domain = arguments?.getString(ARG_DOMAIN).orEmpty()
        binding.list.adapter = adapter

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progress.isVisible = loading
            if (loading) binding.progress.startAnimation() else binding.progress.stopAnimation()
        }

        viewModel.error.observe(viewLifecycleOwner) { errorCode ->
            if (errorCode != null) {
                binding.errorText.isVisible = true
                binding.errorText.text = getString(
                    when (errorCode) {
                        "error_no_dns" -> R.string.error_no_dns
                        else -> R.string.error_network
                    }
                )
            } else {
                binding.errorText.isVisible = false
            }
        }

        viewModel.records.observe(viewLifecycleOwner) { records ->
            adapter.submitList(records)
        }

        viewModel.load(domain)
    }

    override fun onPause() {
        super.onPause()
        _binding?.progress?.stopAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}