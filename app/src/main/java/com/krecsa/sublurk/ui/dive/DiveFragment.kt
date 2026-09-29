package com.krecsa.sublurk.ui.dive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.krecsa.sublurk.databinding.FragmentDiveBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiveFragment : Fragment() {

    private var _binding: FragmentDiveBinding? = null
    private val binding get() = _binding!!

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

        // TODO: обработка кнопки Dive
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}