package com.krecsa.sublurk.ui.dive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.krecsa.sublurk.databinding.FragmentDiveBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiveFragment : Fragment() {

    private var _binding: FragmentDiveBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiveViewModel by viewModels()

    private val adapter = SubdomainAdapter()

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

        binding.subdomainsList.adapter = adapter

        binding.diveButton.setOnClickListener {
            val input = binding.inputEdit.text?.toString()?.trim().orEmpty()
            viewModel.dive(input)
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progress.isVisible = loading
            binding.diveButton.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.subdomains.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}