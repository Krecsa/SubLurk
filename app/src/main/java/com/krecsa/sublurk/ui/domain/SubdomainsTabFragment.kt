package com.krecsa.sublurk.ui.domain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.krecsa.sublurk.databinding.FragmentSubdomainsTabBinding
import com.krecsa.sublurk.ui.dive.SubdomainAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubdomainsTabFragment : Fragment() {

    private var _binding: FragmentSubdomainsTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubdomainsViewModel by viewModels()
    private val adapter = SubdomainAdapter()

    companion object {
        private const val ARG_DOMAIN = "domain"

        fun newInstance(domain: String) = SubdomainsTabFragment().apply {
            arguments = Bundle().apply { putString(ARG_DOMAIN, domain) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSubdomainsTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.adapter = adapter

        val domain = arguments?.getString(ARG_DOMAIN).orEmpty()

        viewModel.subdomains.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.load(domain)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}