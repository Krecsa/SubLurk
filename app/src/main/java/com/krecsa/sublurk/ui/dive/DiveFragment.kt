package com.krecsa.sublurk.ui.dive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.krecsa.sublurk.R
import com.krecsa.sublurk.databinding.FragmentDiveBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiveFragment : Fragment() {

    private var _binding: FragmentDiveBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiveViewModel by viewModels()

    private val adapter = SubdomainAdapter { subdomain ->
        val bundle = Bundle().apply {
            putString("domain", subdomain)
        }
        findNavController().navigate(
            R.id.action_diveFragment_to_domainDetailsFragment,
            bundle,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                findNavController().navigate(R.id.action_diveFragment_to_settingsFragment)
                true
            } else {
                false
            }
        }

        binding.subdomainsList.adapter = adapter

        binding.diveButton.setOnClickListener {
            val input = binding.inputEdit.text?.toString()?.trim().orEmpty()
            viewModel.dive(input)
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progress.isVisible = loading
            if (loading) binding.progress.startAnimation() else binding.progress.stopAnimation()
            binding.diveButton.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorCode ->
            if (errorCode != null) {
                val message = when (errorCode) {
                    "error_empty_domain" -> getString(R.string.error_empty_domain)
                    "error_no_subdomains" -> getString(R.string.error_no_subdomains)
                    else -> getString(R.string.error_network)
                }
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.subdomains.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
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