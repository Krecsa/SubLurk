package com.krecsa.sublurk.ui.domain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.krecsa.sublurk.databinding.FragmentWhoisTabBinding
import com.krecsa.sublurk.databinding.ItemWhoisFieldBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WhoisTabFragment : Fragment() {

    private var _binding: FragmentWhoisTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WhoisViewModel by viewModels()

    companion object {
        private const val ARG_DOMAIN = "domain"
        fun newInstance(domain: String) = WhoisTabFragment().apply {
            arguments = Bundle().apply { putString(ARG_DOMAIN, domain) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWhoisTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val domain = arguments?.getString(ARG_DOMAIN).orEmpty()

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progress.isVisible = loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.errorText.isVisible = error != null
            binding.errorText.text = error
        }

        viewModel.whoisData.observe(viewLifecycleOwner) { data ->
            binding.container.removeAllViews()
            val inflater = LayoutInflater.from(requireContext())

            for ((label, value) in data) {
                val itemBinding = ItemWhoisFieldBinding.inflate(inflater, binding.container, false)
                itemBinding.labelText.text = label
                itemBinding.valueText.text = value
                binding.container.addView(itemBinding.root)
            }
        }

        viewModel.load(domain)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}