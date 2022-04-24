package com.fiz.mono.feature_on_boarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.ui.on_boarding.OnBoardingViewModel
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.databinding.FragmentOnBoardingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private val mainPreferencesViewModel: com.fiz.mono.core.ui.MainPreferencesViewModel by activityViewModels()

    private val viewModel: OnBoardingViewModel by viewModels()

    private lateinit var binding: FragmentOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (viewModel.uiState.value.pages != 0)
                viewModel.clickBackPress()
            else
                activity?.onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindListener()
        subscribe()
    }

    private fun bindListener() {
        binding.continueOnBoardingButton.setOnClickListener {
            viewModel.clickNextPages()
        }

        binding.skipOnBoardingButton.setOnClickListener {
            viewModel.clickSkipButton()
        }
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->

                        binding.apply {
                            if (uiState.pages < 3) {
                                pageNumberOnBoardingTextView.text =
                                    getString(R.string.pageNumber, uiState.pages + 1, 3)
                                skipOnBoardingButton.setVisible(uiState.pages != 2)
                                headerOnBoardingTextView.text =
                                    resources.getStringArray(R.array.header)[uiState.pages]
                                descriptionOnBoardingTextView.text =
                                    resources.getStringArray(R.array.description)[uiState.pages]
                                continueOnBoardingButton.text =
                                    resources.getStringArray(R.array.getStarted)[uiState.pages]

                                imageOnBoardingImageView
                                    .setImageResource(
                                        when (uiState.pages) {
                                            0 -> R.drawable.illustration1
                                            1 -> R.drawable.illustration2
                                            else -> R.drawable.illustration3
                                        }
                                    )
                            }
                        }

                    }
                }

                launch {
                    viewModel.navigationUiState.collect { navigationUiState ->

                        if (navigationUiState.isNextScreen) {
                            mainPreferencesViewModel.changeFirstTime()
                            findNavController().popBackStack()
                        }

                    }
                }
            }
        }
    }
}