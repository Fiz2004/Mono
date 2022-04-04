package com.fiz.mono.ui.pin_password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
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
import com.fiz.mono.util.showKeyboard

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
        bindListener()
        subscribe()
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
        }
    }

    private fun bindListener() {
        binding.apply {
            navigationBarLayout.backButton.setOnClickListener {
                viewModel.clickBackButton()
            }

            navigationBarLayout.actionButton.setOnClickListener {
                viewModel.clickEditButton()
            }

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

            numbersEditText.forEach { editText ->
                val number = numbersEditText.indexOf(editText)
                editText.doOnTextChanged { text, start, before, count ->
                    viewModel.setText(number, text)
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
                val oldPin = mainPreferencesViewModel.pin.value ?: ""
                viewModel.updateState(oldPin)
            }
        }
    }

    private fun subscribe() {
        viewModel.statePIN.observe(viewLifecycleOwner) { state_Pin ->
            binding.navigationBarLayout.actionButton.setVisible(state_Pin == State_Pin.REMOVE)
            binding.navigationBarLayout.backButton.setVisible(state_Pin.isNotLogin())

            // change background on red for Remove
            binding.nextPINPasswordButton.isCheckable = state_Pin == State_Pin.REMOVE

            // change background on Error
            numbersEditText.forEach { editText -> editText.isSelected = state_Pin.isError() }

            binding.nextPINPasswordButton.isEnabled = state_Pin == State_Pin.REMOVE

            binding.warningTextView.setVisible(state_Pin.isError())
            numbersEditText.forEach { editText ->
                editText.isEnabled = state_Pin != State_Pin.REMOVE
            }


            when (state_Pin) {
                State_Pin.LOGIN, State_Pin.CREATE -> {
                    binding.decsriptionTextView.text = getString(R.string.enter_PIN)
                    binding.nextPINPasswordButton.text = getString(R.string.next)
                    numbersEditText.forEach { it.setText("") }
                    numbersEditText[0].requestFocus()
                    context?.showKeyboard(numbersEditText[0])
                }
                State_Pin.EDIT, State_Pin.REMOVE_CONFIRM -> {
                    binding.decsriptionTextView.text = getString(R.string.confirm_PIN)
                    binding.nextPINPasswordButton.text = getString(R.string.set_pin_password)
                    numbersEditText.forEach { it.setText("") }
                    numbersEditText[0].requestFocus()
                    context?.showKeyboard(numbersEditText[0])
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
                    mainPreferencesViewModel.setPin(viewModel.getPIN())
                    mainPreferencesViewModel.confirmPin()

                    if (args.fromCome == SETTINGS) {
                        findNavController().popBackStack()
                    }
                }
            }
        }

        viewModel.isReturn.observe(viewLifecycleOwner) {
            if (it)
                findNavController().popBackStack()
        }

        viewModel.pin.observe(viewLifecycleOwner) {
            if (viewModel.statePIN.value == State_Pin.REMOVE) return@observe
            binding.nextPINPasswordButton.isEnabled = viewModel.isNextPINPasswordButtonEnabled()
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
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }

    companion object {
        const val START = "start"
        const val SETTINGS = "settings"
    }
}