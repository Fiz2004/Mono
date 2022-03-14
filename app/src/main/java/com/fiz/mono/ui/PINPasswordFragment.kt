package com.fiz.mono.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentPINPasswordBinding
import com.fiz.mono.ui.input.InputViewModel
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor


class PINPasswordFragment : Fragment() {

    val args: PINPasswordFragmentArgs by navArgs()

    private var _binding: FragmentPINPasswordBinding? = null
    private val binding get() = _binding!!

    val inputViewModel: InputViewModel by activityViewModels()

    var focus: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPINPasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextNumber1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (cs.isNotEmpty() && focus == null) {
                    binding.editTextNumber2.requestFocus()
                }
                if (cs.length > 1) {
                    binding.editTextNumber1.setText(cs[1].toString())
                }
                checkPIN()
            }

            override fun beforeTextChanged(s: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })

        binding.editTextNumber1.setOnClickListener {
            focus = 1
            binding.editTextNumber1.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.editTextNumber2.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.editTextNumber3.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.editTextNumber4.transformationMethod =
                PasswordTransformationMethod.getInstance()
        }

        binding.editTextNumber2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (cs.isNotEmpty() && focus == null) {
                    binding.editTextNumber3.requestFocus()
                }
                if (cs.length > 1) {
                    binding.editTextNumber2.setText(cs[1].toString())
                }
                checkPIN()
            }

            override fun beforeTextChanged(s: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })

        binding.editTextNumber2.setOnClickListener {
            focus = 2
            binding.editTextNumber1.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.editTextNumber2.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.editTextNumber3.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.editTextNumber4.transformationMethod =
                PasswordTransformationMethod.getInstance()
        }

        binding.editTextNumber3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (cs.isNotEmpty() && focus == null) {
                    binding.editTextNumber4.requestFocus()
                }
                if (cs.length > 1) {
                    binding.editTextNumber3.setText(cs[1].toString())
                }
                checkPIN()
            }

            override fun beforeTextChanged(s: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })

        binding.editTextNumber3.setOnClickListener {
            focus = 2
            binding.editTextNumber1.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.editTextNumber2.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.editTextNumber3.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.editTextNumber4.transformationMethod =
                PasswordTransformationMethod.getInstance()
        }

        binding.editTextNumber4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (cs.isNotEmpty() && focus == null) {
                    binding.nextPINPasswordButton.requestFocus()
                }
                if (cs.length > 1) {
                    binding.editTextNumber4.setText(cs[1].toString())
                }
                checkPIN()
            }

            override fun beforeTextChanged(s: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })

        binding.editTextNumber4.setOnClickListener {
            focus = 4
            binding.editTextNumber1.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.editTextNumber2.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.editTextNumber3.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.editTextNumber4.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
        }


        binding.nextPINPasswordButton.setOnClickListener {
            inputViewModel.log = true
            val action =
                PINPasswordFragmentDirections
                    .actionPINPasswordFragmentToInputFragment()
            view.findNavController().navigate(action)
        }

        checkPIN()
    }

    private fun checkPIN() {
        binding.nextPINPasswordButton.isEnabled =
            binding.editTextNumber1.text?.isNotEmpty() == true &&
                    binding.editTextNumber2.text?.isNotEmpty() == true &&
                    binding.editTextNumber3.text?.isNotEmpty() == true &&
                    binding.editTextNumber4.text?.isNotEmpty() == true

        if (!binding.nextPINPasswordButton.isEnabled) {
            binding.nextPINPasswordButton.backgroundTintList =
                context?.themeColor(R.attr.colorGray)?.let {
                    ColorStateList.valueOf(
                        it
                    )
                }
            context?.themeColor(com.google.android.material.R.attr.colorSecondary)
                ?.let { binding.nextPINPasswordButton.setTextColor(it) }
        } else {
            binding.nextPINPasswordButton.backgroundTintList =
                context?.getColorCompat(R.color.blue)?.let {
                    ColorStateList.valueOf(
                        it
                    )
                }
            context?.themeColor(R.attr.colorMain)
                ?.let {
                    binding.nextPINPasswordButton.setTextColor(it)
                }
        }
    }

}