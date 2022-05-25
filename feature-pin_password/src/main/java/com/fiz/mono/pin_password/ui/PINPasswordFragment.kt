package com.fiz.mono.pin_password.ui

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.feature.pin_password.databinding.FragmentPINPasswordBinding
import com.fiz.mono.navigation.navigationData
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.launchAndRepeatWithViewLifecycle
import com.fiz.mono.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
@WithFragmentBindings
class PINPasswordFragment : Fragment() {

    private val viewModel: PINPasswordViewModel by viewModels()

    private lateinit var numbersEditText: List<EditText>

    private lateinit var binding: FragmentPINPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPINPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        bind()
        bindListener()
        subscribe()
    }

    private fun init() {
        val fromCome = navigationData as? String ?: "settings"
        viewModel.start(fromCome)
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

            navigationBarLayout.actionButton
                .setTextColor(requireContext().getColorCompat(R.color.blue))
            navigationBarLayout.actionButton.text = getString(R.string.edit)
        }
    }

    private fun bindListener() {

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (viewModel.isCanPopBackStack()) {
                findNavController().popBackStack()
            }
        }

        binding.apply {
            navigationBarLayout.backButton.setOnClickListener {
                viewModel.clickBackButton()
            }

            navigationBarLayout.actionButton.setOnClickListener {
                viewModel.clickEditButton()
            }

            numbersEditText.forEach { editText ->
                val number = numbersEditText.indexOf(editText)
                editText.doOnTextChanged { text, start, before, count ->
                    viewModel.setText(number, text)

                    if (before == count) return@doOnTextChanged

                    if (text?.length == 1) {
                        viewModel.exitError()
                        numbersEditText.forEach {
                            if (it.text.isBlank()) {
                                it.requestFocus()
                                return@doOnTextChanged
                            }
                        }
                        nextPINPasswordButton.requestFocus()
                    }

                    if (text.toString().length > 1) {
                        editText.setText(text?.get(1).toString())
                        editText.setSelection(1)
                    }
                }


                editText.setOnFocusChangeListener { view, isFocus ->
                    if (view !is EditText) return@setOnFocusChangeListener

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

            binding.nextPINPasswordButton.setOnClickListener {
                viewModel.updateState()
            }
        }
    }

    private fun subscribe() {

        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collectLatest { uiState ->

                val state_Pin = uiState.statePIN
                binding.navigationBarLayout.actionButton.setVisible(state_Pin == StatePin.REMOVE)
                binding.navigationBarLayout.backButton.setVisible(state_Pin.isNotLogin())

                // change background on red for Remove
                binding.nextPINPasswordButton.isCheckable = state_Pin == StatePin.REMOVE

                // change background on Error
                numbersEditText.forEach { editText -> editText.isSelected = state_Pin.isError() }

                binding.nextPINPasswordButton.isEnabled = state_Pin == StatePin.REMOVE

                binding.warningTextView.setVisible(state_Pin.isError())
                numbersEditText.forEach { editText ->
                    editText.isEnabled = state_Pin != StatePin.REMOVE
                }
                binding.nextPINPasswordButton.isActivated = false

                val pin = uiState.pin
                numbersEditText.forEachIndexed { index, editText ->
                    if (editText.text.toString() != (pin[index] ?: "").toString())
                        editText.setText((pin[index] ?: "").toString())
                }

                when (state_Pin) {
                    StatePin.LOGIN, StatePin.CREATE -> {
                        binding.decsriptionTextView.text = getString(R.string.enter_PIN)
                        binding.nextPINPasswordButton.text = getString(R.string.next)
                        numbersEditText[0].requestFocus()
                        context?.showKeyboard(numbersEditText[0])
                    }
                    StatePin.EDIT, StatePin.REMOVE_CONFIRM -> {
                        binding.decsriptionTextView.text = getString(R.string.confirm_PIN)
                        binding.nextPINPasswordButton.text = getString(R.string.set_pin_password)
                        numbersEditText[0].requestFocus()
                        context?.showKeyboard(numbersEditText[0])
                    }
                    StatePin.REMOVE -> {
                        binding.decsriptionTextView.text = getString(R.string.delete_PIN)
                        binding.nextPINPasswordButton.text = getString(R.string.remove_PIN)
                        binding.nextPINPasswordButton.isActivated = true
                    }
                    StatePin.REMOVE_CONFIRM_FINISH -> {
                        Toast.makeText(requireContext(), "PIN deleted", Toast.LENGTH_SHORT).show()
                        viewModel.deletePin()
                    }
                    StatePin.LOGIN_FINISH -> {
                        viewModel.loginFinish()
                    }
                    StatePin.EDIT_FINISH, StatePin.CREATE_FINISH -> {
                        viewModel.setPin(viewModel.getPIN())
                    }
                }



                if (uiState.statePIN == StatePin.REMOVE) return@collectLatest
                binding.nextPINPasswordButton.isEnabled = viewModel.isNextPINPasswordButtonEnabled()
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.navigationState.collectLatest { navigationState ->
                if (navigationState.isReturn) {
                    findNavController().popBackStack()
//                    viewModel.returned()
                }
            }
        }

    }

    companion object {
        const val START = "start"
        const val SETTINGS = "settings"
    }
}