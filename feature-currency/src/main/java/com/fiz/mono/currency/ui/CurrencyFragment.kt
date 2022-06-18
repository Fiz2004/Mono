package com.fiz.mono.currency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.currency.databinding.FragmentCurrencyBinding
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CurrencyFragment : Fragment() {
    private val viewModel: CurrencyViewModel by viewModels()

    private var _binding: FragmentCurrencyBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.currencyScreen.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MdcTheme {
                    CurrencyScreen(viewModel)
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.navigationBarLayout.backButton.setVisible(true)
        binding.navigationBarLayout.actionButton.setVisible(false)
        binding.navigationBarLayout.choiceImageButton.setVisible(false)
        binding.navigationBarLayout.titleTextView.text =
            getString(com.fiz.mono.common.ui.resources.R.string.currency)
    }

    private fun setupListeners() {
        binding.navigationBarLayout.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}