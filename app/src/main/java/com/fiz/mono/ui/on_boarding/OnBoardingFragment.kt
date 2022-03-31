package com.fiz.mono.ui.on_boarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentOnBoardingBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.MainPreferencesViewModelFactory
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.util.setVisible

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
    private val viewModel: OnBoardingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (viewModel.pages.value != 0)
                viewModel.prevPages()
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

        binding.continueOnBoardingButton.setOnClickListener(::continueOnClickListener)
        binding.skipOnBoardingButton.setOnClickListener(::skipOnClickListener)

        viewModel.pages.observe(viewLifecycleOwner) {

            binding.apply {
                pageNumberOnBoardingTextView.text =
                    getString(R.string.pageNumber, it + 1, 3)
                skipOnBoardingButton.setVisible(it != 2)
                headerOnBoardingTextView.text = resources.getStringArray(R.array.header)[it]
                descriptionOnBoardingTextView.text =
                    resources.getStringArray(R.array.description)[it]
                continueOnBoardingButton.text =
                    resources.getStringArray(R.array.getStarted)[it]

                imageOnBoardingImageView
                    .setImageResource(
                        when (it) {
                            0 -> R.drawable.illustration1
                            1 -> R.drawable.illustration2
                            else -> R.drawable.illustration3
                        }
                    )
            }
        }

    }

    private fun skipOnClickListener(view: View) {
        viewModel.PIN()
        mainPreferencesViewModel.changeFirstTime()
        val action =
            OnBoardingFragmentDirections
                .actionToPINPasswordFragment(PINPasswordFragment.START)
        view.findNavController().navigate(action)
    }

    private fun continueOnClickListener(view: View) {
        viewModel.nextPages()

        if (viewModel.pages.value == 3) {
            viewModel.PIN()
            mainPreferencesViewModel.changeFirstTime()
            val action =
                OnBoardingFragmentDirections
                    .actionToPINPasswordFragment(PINPasswordFragment.START)
            view.findNavController().navigate(action)
        }
    }
}