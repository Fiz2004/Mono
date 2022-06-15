package com.fiz.mono.on_boarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.feature.on_boarding.databinding.FragmentOnBoardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private val viewModel: OnBoardingViewModel by viewModels()

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.onEvent(OnBoardingEvent.BackPressClicked)
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

        setupListeners()
        observeViewStateUpdates()
        observeViewEffects()
    }

    private fun setupListeners() {
        binding.continueOnBoardingButton.setOnClickListener {
            viewModel.onEvent(OnBoardingEvent.NextPagesClicked)
        }

        binding.skipOnBoardingButton.setOnClickListener {
            viewModel.onEvent(OnBoardingEvent.SkipButtonClicked)
        }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun observeViewEffects() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEffects.collect { viewEffect ->
                when (viewEffect) {
                    OnBoardingViewEffect.MoveNextScreen -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun updateScreenState(newState: OnBoardingViewState) {
        if (newState.page < OnBoardingViewState.MAX_PAGE) {
            binding.apply {
                pageNumberOnBoardingTextView.text =
                    getString(R.string.pageNumber, newState.page + 1, OnBoardingViewState.MAX_PAGE)
                skipOnBoardingButton.setVisible(newState.page != OnBoardingViewState.MAX_PAGE - 1)
                headerOnBoardingTextView.text =
                    resources.getStringArray(R.array.header)[newState.page]
                descriptionOnBoardingTextView.text =
                    resources.getStringArray(R.array.description)[newState.page]
                continueOnBoardingButton.text =
                    resources.getStringArray(R.array.getStarted)[newState.page]

                imageOnBoardingImageView
                    .setImageResource(viewModel.getImage())
            }
        }
    }
}
