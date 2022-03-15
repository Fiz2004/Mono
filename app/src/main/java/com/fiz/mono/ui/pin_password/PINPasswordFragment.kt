package com.fiz.mono.ui.pin_password

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPINPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.fromCome == START && viewModel.PIN.isBlank()) {
            viewModel.log = true
            val action =
                PINPasswordFragmentDirections
                    .actionPINPasswordFragmentToInputFragment()
            view.findNavController().navigate(action)
            return
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editButton.setOnClickListener {
            viewModel.statePIN = STATE_EDIT
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

        binding.editTextNumber1.onFocusChangeListener = onFocusChangeEditText()
        binding.editTextNumber2.onFocusChangeListener = onFocusChangeEditText()
        binding.editTextNumber3.onFocusChangeListener = onFocusChangeEditText()
        binding.editTextNumber4.onFocusChangeListener = onFocusChangeEditText()

        binding.nextPINPasswordButton.setOnClickListener {

            if (viewModel.statePIN == STATE_REMOVE) {
                viewModel.statePIN = STATE_CONFIRM_REMOVE
                updateUI()
                return@setOnClickListener
            }

            if (viewModel.statePIN == STATE_CONFIRM_REMOVE) {
                if (viewModel.PIN == getPIN()) {
                    viewModel.log = true
                    viewModel.PIN = ""
                    Toast.makeText(requireContext(), "PIN deleted", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    viewModel.statePIN = STATE_CONFIRM_REMOVE_ERROR
                    updateUI()
                }
                return@setOnClickListener
            }

            if (viewModel.statePIN == STATE_EDIT) {
                if (viewModel.PIN == getPIN()) {
                    viewModel.statePIN = STATE_CREATE
                    updateUI()
                } else {
                    viewModel.statePIN = STATE_EDIT_ERROR
                    updateUI()
                }
                return@setOnClickListener
            }

            viewModel.log = true
            viewModel.PIN = getPIN()

            if (args.fromCome == SETTINGS) {
                findNavController().popBackStack()
                return@setOnClickListener
            }

            val action =
                PINPasswordFragmentDirections
                    .actionPINPasswordFragmentToInputFragment()
            view.findNavController().navigate(action)
        }


        viewModel.statePIN = getState()

        updateUI()
        checkPIN()
    }

    private fun onFocusChangeEditText() = { view: View, b: Boolean ->
        if (view !is EditText) throw error("Error")
        if (b) {
            view.transformationMethod = HideReturnsTransformationMethod.getInstance()
            view.background = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.background_pin_focused_edittext
            )
        } else {
            view.transformationMethod = PasswordTransformationMethod.getInstance()
            view.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.background_pin_edittext)
        }
    }

    private fun getState(): String {
        if (args.fromCome == START && viewModel.PIN.isNotBlank()) {
            return STATE_LOGIN
        }

        if (args.fromCome == SETTINGS && viewModel.PIN.isBlank()) {
            return STATE_CREATE
        }

        if (args.fromCome == SETTINGS && viewModel.PIN.isNotBlank()) {
            return STATE_REMOVE
        }

        return STATE_ERROR
    }

    private fun textWatcher(editText: EditText, next: View) = object : TextWatcher {
        override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            if (cs.isNotEmpty()) {
                if (viewModel.statePIN == STATE_CONFIRM_REMOVE_ERROR) {
                    viewModel.statePIN = STATE_CONFIRM_REMOVE
                    updateUI()
                }
                if (viewModel.statePIN == STATE_EDIT_ERROR) {
                    viewModel.statePIN = STATE_EDIT
                    updateUI()
                }
                if (next is EditText)
                    if (next.text.isBlank())
                        next.requestFocus()
            }
            if (cs.length > 1) {
                editText.setText(cs[1].toString())
                editText.setSelection(1)
            }
            checkPIN()
        }

        override fun beforeTextChanged(s: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
        override fun afterTextChanged(arg0: Editable) {}
    }

    private fun getPIN() =
        binding.editTextNumber1.text.toString() + binding.editTextNumber2.text + binding.editTextNumber3.text + binding.editTextNumber4.text

    private fun updateUI() {
        when (viewModel.statePIN) {
            STATE_LOGIN, STATE_CREATE -> {
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
            STATE_EDIT, STATE_CONFIRM_REMOVE -> {
                binding.decsriptionTextView.text = getString(R.string.confirm_PIN)
                binding.nextPINPasswordButton.text = getString(R.string.set_pin_password)
                binding.editButton.visibility = View.GONE
                binding.warningTextView.visibility = View.GONE
                binding.editTextNumber1.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.background_pin_edittext
                )
                binding.editTextNumber2.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.background_pin_edittext
                )
                binding.editTextNumber3.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.background_pin_edittext
                )
                binding.editTextNumber4.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.background_pin_edittext
                )
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
            STATE_REMOVE -> {
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
            STATE_CONFIRM_REMOVE_ERROR, STATE_EDIT_ERROR -> {
                binding.warningTextView.visibility = View.VISIBLE
                binding.editTextNumber1.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.background_pin_error_edittext
                )
                binding.editTextNumber2.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.background_pin_error_edittext
                )
                binding.editTextNumber3.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.background_pin_error_edittext
                )
                binding.editTextNumber4.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.background_pin_error_edittext
                )
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
        if (viewModel.statePIN == STATE_REMOVE) return
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

    companion object {
        const val START = "start"
        const val SETTINGS = "settings"

        const val STATE_LOGIN = "login"
        const val STATE_CREATE = "create"
        const val STATE_REMOVE = "remove"
        const val STATE_ERROR = "ERROR"
        const val STATE_CONFIRM_REMOVE_ERROR = "errorConfirmRemove"
        const val STATE_CONFIRM_REMOVE = "confirmRemove"
        const val STATE_EDIT_ERROR = "errorEdit"
        const val STATE_EDIT = "edit"

    }

}