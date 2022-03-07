package com.fiz.mono.ui.on_boarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentOnBoardingBinding
import com.fiz.mono.ui.input.InputViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnBoardingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnBoardingFragment : Fragment() {
    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    val viewModel: OnBoardingViewModel by activityViewModels()
    val inputViewModel: InputViewModel by activityViewModels()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

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
    ): View? {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
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
                        .actionOnBoardingFragmentToPINPasswordFragment()
                view.findNavController().navigate(action)
            }
        }

        binding.skipOnBoardingButton.setOnClickListener {
            viewModel.PIN()
            inputViewModel.firstTime=false
            val action =
                OnBoardingFragmentDirections
                    .actionOnBoardingFragmentToPINPasswordFragment()
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnBoardingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnBoardingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}