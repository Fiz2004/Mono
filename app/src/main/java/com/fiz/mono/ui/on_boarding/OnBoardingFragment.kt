package com.fiz.mono.ui.on_boarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentOnBoardingBinding
import com.fiz.mono.ui.input.InputViewModel
import com.fiz.mono.ui.pin_password.PINPasswordFragment

class OnBoardingFragment : Fragment() {
    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OnBoardingViewModel by activityViewModels()
    private val inputViewModel: InputViewModel by activityViewModels()

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


        binding.continueOnBoardingButton.setOnClickListener {
            viewModel.nextPages()

            if (viewModel.pages.value == 3) {
                viewModel.PIN()
                inputViewModel.firstTime=false
                val action =
                    OnBoardingFragmentDirections
                        .actionOnBoardingFragmentToPINPasswordFragment(PINPasswordFragment.START)
                view.findNavController().navigate(action)
            }
        }

        binding.skipOnBoardingButton.setOnClickListener {
            viewModel.PIN()
            inputViewModel.firstTime=false
            val action =
                OnBoardingFragmentDirections
                    .actionOnBoardingFragmentToPINPasswordFragment(PINPasswordFragment.START)
            view.findNavController().navigate(action)
        }

        viewModel.pages.observe(viewLifecycleOwner) {
            when (it) {
                0 -> {
                    binding.imageOnBoardingImageView
                        .setImageResource(R.drawable.illustration1)
                    binding.pageNumberOnBoardingTextView.text = getString(R.string.pageNumber,1,3)
                    binding.skipOnBoardingButton.visibility=View.VISIBLE

                    binding.headerOnBoardingTextView.text = getString(R.string.header1)
                    binding.descriptionOnBoardingTextView.text = getString(R.string.description1)

                    binding.continueOnBoardingButton.text = getString(R.string.word_continue)
                }
                1 -> {
                    binding.imageOnBoardingImageView
                        .setImageResource(R.drawable.illustration2)
                    binding.pageNumberOnBoardingTextView.text = getString(R.string.pageNumber,2,3)
                    binding.skipOnBoardingButton.visibility=View.VISIBLE

                    binding.headerOnBoardingTextView.text = getString(R.string.header2)
                    binding.descriptionOnBoardingTextView.text = getString(R.string.description2)

                    binding.continueOnBoardingButton.text = getString(R.string.word_continue)
                }
                2 -> {
                    binding.imageOnBoardingImageView
                        .setImageResource(R.drawable.illustration3)
                    binding.pageNumberOnBoardingTextView.text = getString(R.string.pageNumber,3,3)
                    binding.skipOnBoardingButton.visibility=View.GONE

                    binding.headerOnBoardingTextView.text = getString(R.string.header3)
                    binding.descriptionOnBoardingTextView.text = getString(R.string.description3)

                    binding.continueOnBoardingButton.text = getString(R.string.getStarted)
                }
            }
        }


    }
}