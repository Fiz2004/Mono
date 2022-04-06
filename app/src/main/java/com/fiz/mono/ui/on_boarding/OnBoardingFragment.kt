package com.fiz.mono.ui.on_boarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentOnBoardingBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.MainPreferencesViewModelFactory
import com.fiz.mono.util.setVisible
import kotlinx.coroutines.launch

class OnBoardingFragment : Fragment() {
    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    private val mainPreferencesViewModel: MainPreferencesViewModel by activityViewModels {
        MainPreferencesViewModelFactory(
            requireActivity().getSharedPreferences(
                getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            )
        )
    }
    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (viewModel.onBoardingUiState.value.pages != 0)
                viewModel.clickBackPress()
            else
                activity?.onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                viewModel.onBoardingUiState.collect { onBoardingUiState ->

                    binding.apply {
                        if (onBoardingUiState.pages < 3) {
                            pageNumberOnBoardingTextView.text =
                                getString(R.string.pageNumber, onBoardingUiState.pages + 1, 3)
                            skipOnBoardingButton.setVisible(onBoardingUiState.pages != 2)
                            headerOnBoardingTextView.text =
                                resources.getStringArray(R.array.header)[onBoardingUiState.pages]
                            descriptionOnBoardingTextView.text =
                                resources.getStringArray(R.array.description)[onBoardingUiState.pages]
                            continueOnBoardingButton.text =
                                resources.getStringArray(R.array.getStarted)[onBoardingUiState.pages]

                            imageOnBoardingImageView
                                .setImageResource(
                                    when (onBoardingUiState.pages) {
                                        0 -> R.drawable.illustration1
                                        1 -> R.drawable.illustration2
                                        else -> R.drawable.illustration3
                                    }
                                )
                        }

                        if (onBoardingUiState.isNextScreen) {
                            mainPreferencesViewModel.changeFirstTime()
                            findNavController().popBackStack()
                        }
                    }

                }
            }
        }
    }
}