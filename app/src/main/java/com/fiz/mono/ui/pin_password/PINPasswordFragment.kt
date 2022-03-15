package com.fiz.mono.ui.pin_password

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentPINPasswordBinding
import com.fiz.mono.ui.input.InputViewModel
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.themeColor


class PINPasswordFragment : Fragment() {
    private val args: PINPasswordFragmentArgs by navArgs()

    private var _binding: FragmentPINPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InputViewModel by activityViewModels()

    private var type = "string"

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

        if ((args.type == "onBoard" || args.type == "start") && viewModel.PIN == "") {
            viewModel.log = true
            val action =
                PINPasswordFragmentDirections
                    .actionPINPasswordFragmentToInputFragment()
            view.findNavController().navigate(action)
            return
        } else {
            type = "login"
        }


        if (args.type == "settings" && viewModel.PIN == "") {
            type = "create"
        }

        if (args.type == "settings" && viewModel.PIN != "") {
            type = "remove"
        }

        updateUI()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editButton.setOnClickListener {
            type = "edit"
            updateUI()
        }

//        binding.editTextNumber1.setOnClickListener {
//            focus = 1
//            binding.editTextNumber1.transformationMethod =
//                HideReturnsTransformationMethod.getInstance()
//            binding.editTextNumber2.transformationMethod =
//                PasswordTransformationMethod.getInstance()
//            binding.editTextNumber3.transformationMethod =
//                PasswordTransformationMethod.getInstance()
//            binding.editTextNumber4.transformationMethod =
//                PasswordTransformationMethod.getInstance()
//        }

        binding.editTextNumber1.addTextChangedListener(
            textWatcher(
                binding.editTextNumber1,
                binding.editTextNumber2
            )
        )
        binding.editTextNumber2.addTextChangedListener(
            textWatcher(
                binding.editTextNumber2,
                binding.editTextNumber3
            )
        )
        binding.editTextNumber3.addTextChangedListener(
            textWatcher(
                binding.editTextNumber3,
                binding.editTextNumber4
            )
        )
        binding.editTextNumber4.addTextChangedListener(
            textWatcher(
                binding.editTextNumber4,
                binding.nextPINPasswordButton
            )
        )

        binding.nextPINPasswordButton.setOnClickListener {

            if (type == "remove") {
                type = "confirmRemove"
                updateUI()
                return@setOnClickListener
            }

            if (type == "confirmRemove") {
                if (viewModel.PIN == getPIN()) {
                    viewModel.log = true
                    viewModel.PIN = ""
                    Toast.makeText(requireContext(), "PIN deleted", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    type = "ErrorConfirmRemove"
                    updateUI()
                }
                return@setOnClickListener
            }

            if (type == "edit") {
                if (viewModel.PIN == getPIN()) {
                    type = "create"
                    updateUI()
                } else {
                    type = "ErrorEdit"
                    updateUI()
                }
                return@setOnClickListener
            }

            viewModel.log = true
            viewModel.PIN = getPIN()

            if (args.type == "settings") {
                findNavController().popBackStack()
                return@setOnClickListener
            }

            val action =
                PINPasswordFragmentDirections
                    .actionPINPasswordFragmentToInputFragment()
            view.findNavController().navigate(action)
        }

        checkPIN()
    }

    private fun textWatcher(editText: EditText, next: View) = object : TextWatcher {
        override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            if (cs.isNotEmpty()) {
                if (type == "ErrorConfirmRemove") {
                    type = "confirmRemove"
                    updateUI()
                }
                if (type == "ErrorEdit") {
                    type = "edit"
                    updateUI()
                }
                next.requestFocus()
            }
            if (cs.length > 1) {
                editText.setText(cs[1].toString())
            }
            checkPIN()
        }

        override fun beforeTextChanged(s: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
        override fun afterTextChanged(arg0: Editable) {}
    }

    private fun getPIN() =
        binding.editTextNumber1.text.toString() + binding.editTextNumber2.text + binding.editTextNumber3.text + binding.editTextNumber4.text

    private fun updateUI() {
        when (type) {
            "login", "create" -> {
                binding.decsriptionTextView.text = getString(R.string.enter_PIN)
                binding.nextPINPasswordButton.text = getString(R.string.next)
                binding.editButton.visibility = View.GONE
                binding.nextPINPasswordButton.isEnabled = false
                binding.editTextNumber1.setText("")
                binding.editTextNumber2.setText("")
                binding.editTextNumber3.setText("")
                binding.editTextNumber4.setText("")
                nextPINPasswordButtonSetDisabled()
                binding.editTextNumber1.requestFocus()
                showKeyboard()
            }
            "edit", "confirmRemove" -> {
                binding.decsriptionTextView.text = getString(R.string.confirm_PIN)
                binding.nextPINPasswordButton.text = getString(R.string.set_pin_password)
                binding.editButton.visibility = View.GONE
                binding.warningTextView.visibility = View.GONE
                binding.editTextNumber1.setBackgroundColor(requireContext().themeColor(R.attr.colorBackground))
                binding.editTextNumber2.setBackgroundColor(requireContext().themeColor(R.attr.colorBackground))
                binding.editTextNumber3.setBackgroundColor(requireContext().themeColor(R.attr.colorBackground))
                binding.editTextNumber4.setBackgroundColor(requireContext().themeColor(R.attr.colorBackground))
                binding.nextPINPasswordButton.isEnabled = false
                nextPINPasswordButtonSetDisabled()
                binding.editTextNumber1.isEnabled = true
                binding.editTextNumber2.isEnabled = true
                binding.editTextNumber3.isEnabled = true
                binding.editTextNumber4.isEnabled = true
                binding.editTextNumber1.setText("")
                binding.editTextNumber2.setText("")
                binding.editTextNumber3.setText("")
                binding.editTextNumber4.setText("")
                binding.editTextNumber1.requestFocus()
                showKeyboard()
            }
            "remove" -> {
                binding.decsriptionTextView.text = getString(R.string.delete_PIN)
                binding.nextPINPasswordButton.text = getString(R.string.remove_PIN)
                binding.editButton.visibility = View.VISIBLE
                binding.nextPINPasswordButton.isEnabled = true
                nextPINPasswordButtonSetRemove()
                binding.editTextNumber1.setText(viewModel.PIN[0].toString())
                binding.editTextNumber2.setText(viewModel.PIN[1].toString())
                binding.editTextNumber3.setText(viewModel.PIN[2].toString())
                binding.editTextNumber4.setText(viewModel.PIN[3].toString())
                binding.editTextNumber1.isEnabled = false
                binding.editTextNumber2.isEnabled = false
                binding.editTextNumber3.isEnabled = false
                binding.editTextNumber4.isEnabled = false
            }
            "ErrorConfirmRemove", "ErrorEdit" -> {
                binding.warningTextView.visibility = View.VISIBLE
                binding.editTextNumber1.setBackgroundColor(requireContext().getColorCompat(R.color.red))
                binding.editTextNumber2.setBackgroundColor(requireContext().getColorCompat(R.color.red))
                binding.editTextNumber3.setBackgroundColor(requireContext().getColorCompat(R.color.red))
                binding.editTextNumber4.setBackgroundColor(requireContext().getColorCompat(R.color.red))

            }
        }
    }

    private fun showKeyboard() {
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            binding.editTextNumber1,
            InputMethodManager.SHOW_IMPLICIT
        )
    }

    private fun checkPIN() {
        if (type == "remove") return
        binding.nextPINPasswordButton.isEnabled =
            binding.editTextNumber1.text?.isNotEmpty() == true &&
                    binding.editTextNumber2.text?.isNotEmpty() == true &&
                    binding.editTextNumber3.text?.isNotEmpty() == true &&
                    binding.editTextNumber4.text?.isNotEmpty() == true

        if (binding.nextPINPasswordButton.isEnabled)
            nextPINPasswordButtonSetEnabled()
        else
            nextPINPasswordButtonSetDisabled()
    }

    private fun nextPINPasswordButtonSetDisabled() {
        binding.nextPINPasswordButton.backgroundTintList =
            context?.themeColor(R.attr.colorGray)?.let {
                ColorStateList.valueOf(
                    it
                )
            }
        context?.themeColor(com.google.android.material.R.attr.colorSecondary)
            ?.let { binding.nextPINPasswordButton.setTextColor(it) }
    }

    private fun nextPINPasswordButtonSetEnabled() {
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

    private fun nextPINPasswordButtonSetRemove() {
        binding.nextPINPasswordButton.backgroundTintList =
            context?.getColorCompat(R.color.red)?.let {
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