package com.fiz.mono.on_boarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
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

    private lateinit var binding: FragmentOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!viewModel.clickBackPress())
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
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { uiState ->

                if (uiState.pages < 3) {
                    binding.apply {
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
                            .setImageResource(viewModel.getImage())
                    }
                }

            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.navigationUiState.collect { navigationUiState ->

                if (navigationUiState.isNextScreen) {
                    viewModel.changeFirstTime()
                    findNavController().popBackStack()
                }

            }
        }
    }
}
