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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentPINPasswordBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.MainPreferencesViewModelFactory
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setVisible

class PINPasswordFragment : Fragment() {
    private val args: PINPasswordFragmentArgs by navArgs()

    private var _binding: FragmentPINPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var numbersEditText: List<EditText>

    private val mainPreferencesViewModel: MainPreferencesViewModel by activityViewModels {
        MainPreferencesViewModelFactory(
            requireActivity().getSharedPreferences(
                getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            )
        )
    }

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

        init()
        bind()
        subscribe()
        checkPIN()
    }

    private fun subscribe() {
        viewModel.statePIN.observe(viewLifecycleOwner) {
            binding.navigationBarLayout.actionButton.setVisible(it == State_Pin.REMOVE)
            binding.navigationBarLayout.backButton.setVisible(it.isNotLogin())

            // change background on red for Remove
            binding.nextPINPasswordButton.isCheckable = it == State_Pin.REMOVE

            // change background on Error
            numbersEditText.forEach { editText -> editText.isSelected = it.isError() }

            binding.nextPINPasswordButton.isEnabled = it == State_Pin.REMOVE

            binding.warningTextView.setVisible(it.isError())
            numbersEditText.forEach { editText -> editText.isEnabled = it != State_Pin.REMOVE }


            when (it) {
                State_Pin.LOGIN, State_Pin.CREATE -> {
                    binding.decsriptionTextView.text = getString(R.string.enter_PIN)
                    binding.nextPINPasswordButton.text = getString(R.string.next)
                    numbersEditText.forEach { it.setText("") }
                    numbersEditText[0].requestFocus()
                    showKeyboard()
                }
                State_Pin.EDIT, State_Pin.REMOVE_CONFIRM -> {
                    binding.decsriptionTextView.text = getString(R.string.confirm_PIN)
                    binding.nextPINPasswordButton.text = getString(R.string.set_pin_password)
                    numbersEditText.forEach { it.setText("") }
                    numbersEditText[0].requestFocus()
                    showKeyboard()
                }
                State_Pin.REMOVE -> {
                    binding.decsriptionTextView.text = getString(R.string.delete_PIN)
                    binding.nextPINPasswordButton.text = getString(R.string.remove_PIN)
                    val pin = mainPreferencesViewModel.pin.value ?: ""
                    numbersEditText.forEachIndexed { index, editText ->
                        editText.setText(pin[index].toString())
                    }
                }
                State_Pin.REMOVE_CONFIRM_FINISH -> {
                    mainPreferencesViewModel.deletePin()
                    Toast.makeText(requireContext(), "PIN deleted", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                State_Pin.LOGIN_FINISH, State_Pin.CREATE_FINISH -> {
                    mainPreferencesViewModel.confirmPin()
                    val action =
                        PINPasswordFragmentDirections
                            .actionPINPasswordFragmentToInputFragment(true)
                    findNavController().navigate(action)
                }
                State_Pin.EDIT_FINISH -> {
                    mainPreferencesViewModel.setPin(getPIN())
                    mainPreferencesViewModel.confirmPin()

                    if (args.fromCome == SETTINGS) {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun init() {
        viewModel.initState(args.fromCome, mainPreferencesViewModel.pin.value ?: "")
    }

    private fun bind() {
        binding.apply {
            numbersEditText = listOf(
                number1EditText,
                number2EditText,
                number3EditText,
                number4EditText,
            )

            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.PIN_password)

            binding.navigationBarLayout.actionButton
                .setTextColor(requireContext().getColorCompat(R.color.blue))
            binding.navigationBarLayout.actionButton.text = getString(R.string.edit)

            navigationBarLayout.backButton.setOnClickListener(::backButtonOnClickListener)
            navigationBarLayout.actionButton.setOnClickListener(::editOnClickListener)

            numbersEditText[0].addTextChangedListener(
                textWatcher(
                    numbersEditText[0],
                    numbersEditText[1]
                )
            )
            numbersEditText[1].addTextChangedListener(
                textWatcher(
                    numbersEditText[1],
                    numbersEditText[2]
                )
            )
            numbersEditText[2].addTextChangedListener(
                textWatcher(
                    numbersEditText[2],
                    numbersEditText[3]
                )
            )
            numbersEditText[3].addTextChangedListener(
                textWatcher(
                    numbersEditText[3],
                    nextPINPasswordButton
                )
            )
            numbersEditText.forEach { it.onFocusChangeListener = onFocusChangeEditText() }
            binding.nextPINPasswordButton.setOnClickListener(::nextPINOnClickListener)
        }
    }

    private fun nextPINOnClickListener(view: View) {
        viewModel.updateState(mainPreferencesViewModel.pin.value ?: "", getPIN())
    }

    private fun editOnClickListener(view: View) {
        viewModel.changeStateOnEdit()
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
                viewModel.exitError()
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
        numbersEditText[0].text.toString() + numbersEditText[1].text +
                numbersEditText[2].text + numbersEditText[3].text

    private fun showKeyboard() {
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            numbersEditText[0],
            InputMethodManager.SHOW_IMPLICIT
        )
    }

    private fun checkPIN() {
        if (viewModel.statePIN.value == State_Pin.REMOVE) return
        binding.nextPINPasswordButton.isEnabled =
            numbersEditText[0].text?.isNotEmpty() == true &&
                    numbersEditText[1].text?.isNotEmpty() == true &&
                    numbersEditText[2].text?.isNotEmpty() == true &&
                    numbersEditText[3].text?.isNotEmpty() == true
    }

    companion object {
        const val START = "start"
        const val SETTINGS = "settings"
    }
}