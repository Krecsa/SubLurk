package com.krecsa.sublurk.ui.domain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.krecsa.sublurk.R
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
            if (loading) binding.progress.startAnimation() else binding.progress.stopAnimation()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.errorText.isVisible = error != null
            binding.errorText.text = error
        }

        viewModel.whoisData.observe(viewLifecycleOwner) { data ->
            binding.container.removeAllViews()
            val inflater = LayoutInflater.from(requireContext())

            for ((key, value) in data) {
                val itemBinding = ItemWhoisFieldBinding.inflate(inflater, binding.container, false)
                itemBinding.labelText.text = getLocalizedKey(key)
                itemBinding.valueText.text = value
                binding.container.addView(itemBinding.root)
            }
        }

        viewModel.load(domain)
    }

    private fun getLocalizedKey(key: String): String {
        return when (key) {
            "domain" -> getString(R.string.whois_domain)
            "state" -> getString(R.string.whois_state)
            "nserver" -> getString(R.string.whois_nserver)
            "registrar" -> getString(R.string.whois_registrar)
            "admin-contact" -> getString(R.string.whois_admin_contact)
            "created" -> getString(R.string.whois_created)
            "paid-till" -> getString(R.string.whois_paid_till)
            "free-date" -> getString(R.string.whois_free_date)
            "org" -> getString(R.string.whois_org)
            "source" -> getString(R.string.whois_source)
            else -> key.replaceFirstChar { it.uppercase() }
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