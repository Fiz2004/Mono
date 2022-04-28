package com.fiz.mono.on_boarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.launchAndRepeatWithViewLifecycle
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.feature.on_boarding.databinding.FragmentOnBoardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private val viewModel: OnBoardingViewModel by viewModels()

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.onEvent(OnBoardingUiEvent.ClickBackPress)
        }
    }

    private lateinit var binding: FragmentOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
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
            viewModel.onEvent(OnBoardingUiEvent.ClickNextPages)
        }

        binding.skipOnBoardingButton.setOnClickListener {
            viewModel.onEvent(OnBoardingUiEvent.ClickSkipButton)
        }
    }

    private fun subscribe() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { uiState ->

                if (uiState.page < OnBoardingUiState.MAX_PAGE) {
                    binding.apply {
                        pageNumberOnBoardingTextView.text =
                            getString(R.string.pageNumber, uiState.page + 1, OnBoardingUiState.MAX_PAGE)
                        skipOnBoardingButton.setVisible(uiState.page != OnBoardingUiState.MAX_PAGE-1)
                        headerOnBoardingTextView.text =
                            resources.getStringArray(R.array.header)[uiState.page]
                        descriptionOnBoardingTextView.text =
                            resources.getStringArray(R.array.description)[uiState.page]
                        continueOnBoardingButton.text =
                            resources.getStringArray(R.array.getStarted)[uiState.page]

                        imageOnBoardingImageView
                            .setImageResource(viewModel.getImage())
                    }
                }

            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.navigationUiState.collect { navigationUiState ->

                if (navigationUiState.moveNextScreen) {
                    findNavController().popBackStack()
                }

            }
        }
    }
}
