package com.fiz.mono.ui.pin_password

import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentPINPasswordBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setVisible

class PINPasswordFragment : Fragment() {
    private val args: PINPasswordFragmentArgs by navArgs()

    private var _binding: FragmentPINPasswordBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: PINPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPINPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNotNeedLog(view)) return

        init()
        bind()
        updateUI()
        checkPIN()
    }

    private fun isNotNeedLog(view: View): Boolean {
        if (args.fromCome == START && mainViewModel.PIN.isBlank()) {
            val action =
                PINPasswordFragmentDirections
                    .actionPINPasswordFragmentToInputFragment(true)
            view.findNavController().navigate(action)
            return true
        }
        return false
    }

    private fun init() {
        viewModel.getState(args.fromCome, mainViewModel.PIN)
    }

    private fun bind() {
        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.actionButton.setTextColor(requireContext().getColorCompat(R.color.blue))
            navigationBarLayout.actionButton.text = getString(R.string.edit)
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.PIN_password)

            navigationBarLayout.backButton.setOnClickListener(::backButtonOnClickListener)
            navigationBarLayout.actionButton.setOnClickListener(::editOnClickListener)

            editTextNumber1.addTextChangedListener(
                textWatcher(
                    editTextNumber1,
                    editTextNumber2
                )
            )
            editTextNumber2.addTextChangedListener(
                textWatcher(
                    editTextNumber2,
                    editTextNumber3
                )
            )
            editTextNumber3.addTextChangedListener(
                textWatcher(
                    editTextNumber3,
                    editTextNumber4
                )
            )
            editTextNumber4.addTextChangedListener(
                textWatcher(
                    editTextNumber4,
                    nextPINPasswordButton
                )
            )
            binding.editTextNumber1.onFocusChangeListener = onFocusChangeEditText()
            binding.editTextNumber2.onFocusChangeListener = onFocusChangeEditText()
            binding.editTextNumber3.onFocusChangeListener = onFocusChangeEditText()
            binding.editTextNumber4.onFocusChangeListener = onFocusChangeEditText()
            binding.nextPINPasswordButton.setOnClickListener(::nextPINOnClickListener)
        }
    }

    private fun nextPINOnClickListener(view: View) {
        if (viewModel.statePIN == STATE_REMOVE) {
            viewModel.statePIN = STATE_CONFIRM_REMOVE
            updateUI()
            return
        }

        if (viewModel.statePIN == STATE_CONFIRM_REMOVE) {
            if (mainViewModel.PIN == getPIN()) {
                val sharedPreferences = requireActivity().getSharedPreferences(
                    getString(R.string.preferences),
                    AppCompatActivity.MODE_PRIVATE
                ).edit()
                mainViewModel.PIN = ""
                sharedPreferences.putString("PIN", mainViewModel.PIN)
                sharedPreferences.apply()
                Toast.makeText(requireContext(), "PIN deleted", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                viewModel.statePIN = STATE_CONFIRM_REMOVE_ERROR
                updateUI()
            }
            return
        }

        if (viewModel.statePIN == STATE_EDIT) {
            if (mainViewModel.PIN == getPIN()) {
                viewModel.statePIN = STATE_CREATE
                updateUI()
            } else {
                viewModel.statePIN = STATE_EDIT_ERROR
                updateUI()
            }
            return
        }

        val sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.preferences),
            AppCompatActivity.MODE_PRIVATE
        ).edit()
        mainViewModel.PIN = getPIN()
        sharedPreferences.putString("PIN", mainViewModel.PIN)
        sharedPreferences.apply()

        if (args.fromCome == SETTINGS) {
            findNavController().popBackStack()
            return
        }

        val action =
            PINPasswordFragmentDirections
                .actionPINPasswordFragmentToInputFragment(true)
        view.findNavController().navigate(action)
    }

    private fun editOnClickListener(view: View) {
        viewModel.statePIN = STATE_EDIT
        updateUI()
    }

    private fun backButtonOnClickListener(view: View) {
        findNavController().popBackStack()
    }

    private fun onFocusChangeEditText() = object : View.OnFocusChangeListener {
        override fun onFocusChange(view: View, isFocus: Boolean) {
            if (view !is EditText) return

            view.transformationMethod =
                if (isFocus)
                    HideReturnsTransformationMethod.getInstance()
                else
                    PasswordTransformationMethod.getInstance()

            view.background = AppCompatResources.getDrawable(
                requireContext(),
                if (isFocus)
                    R.drawable.background_pin_focused_edittext
                else
                    R.drawable.background_pin_edittext
            )

            if (isFocus) {
                if (view.text.length == 1) {
                    view.setSelection(1)
                }
            }
        }
    }

    private fun textWatcher(editText: EditText, next: View) = object : TextWatcher {
        override fun onTextChanged(cs: CharSequence, start: Int, before: Int, count: Int) {
            if (before == count) return
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

        override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }

    private fun getPIN() =
        binding.editTextNumber1.text.toString() + binding.editTextNumber2.text + binding.editTextNumber3.text + binding.editTextNumber4.text

    private fun updateUI() {
        when (viewModel.statePIN) {
            STATE_LOGIN, STATE_CREATE -> {
                binding.decsriptionTextView.text = getString(R.string.enter_PIN)
                binding.nextPINPasswordButton.text = getString(R.string.next)
                binding.navigationBarLayout.actionButton.visibility = View.INVISIBLE
                binding.nextPINPasswordButton.isCheckable = false
                binding.nextPINPasswordButton.isEnabled = false
                binding.editTextNumber1.setText("")
                binding.editTextNumber2.setText("")
                binding.editTextNumber3.setText("")
                binding.editTextNumber4.setText("")
                binding.editTextNumber1.requestFocus()
                showKeyboard()
            }
            STATE_EDIT, STATE_CONFIRM_REMOVE -> {
                binding.decsriptionTextView.text = getString(R.string.confirm_PIN)
                binding.nextPINPasswordButton.text = getString(R.string.set_pin_password)
                binding.navigationBarLayout.actionButton.visibility = View.INVISIBLE
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
                binding.nextPINPasswordButton.isCheckable = false
                binding.nextPINPasswordButton.isEnabled = false
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
                binding.navigationBarLayout.actionButton.visibility = View.VISIBLE
                binding.navigationBarLayout.actionButton.text = getString(R.string.edit)
                binding.nextPINPasswordButton.isCheckable = true
                binding.nextPINPasswordButton.isEnabled = true
                binding.editTextNumber1.setText(mainViewModel.PIN[0].toString())
                binding.editTextNumber2.setText(mainViewModel.PIN[1].toString())
                binding.editTextNumber3.setText(mainViewModel.PIN[2].toString())
                binding.editTextNumber4.setText(mainViewModel.PIN[3].toString())
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