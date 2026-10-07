package com.krecsa.sublurk.ui.domain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.krecsa.sublurk.databinding.FragmentIpTabBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IpTabFragment : Fragment() {

    private var _binding: FragmentIpTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IpTabViewModel by viewModels()

    companion object {
        private const val ARG_DOMAIN = "domain"

        fun newInstance(domain: String) = IpTabFragment().apply {
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

        viewModel.ip.observe(viewLifecycleOwner) { ip ->
            binding.ipValue.text = ip ?: "—"
        }

        viewModel.info.observe(viewLifecycleOwner) { info ->
            if (info == null) return@observe

            binding.countryValue.text = info.country ?: "—"
            binding.cityValue.text = info.city ?: "—"
            binding.regionValue.text = info.region ?: "—"
            binding.orgValue.text = info.org ?: "—"
            binding.timezoneValue.text = info.timezone ?: "—"
        }

        viewModel.load(domain)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}