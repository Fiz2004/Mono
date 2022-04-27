package com.fiz.mono.feature_input.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.ui.MainViewModel
import com.fiz.mono.core.ui.shared_adapters.CategoriesAdapter
import com.fiz.mono.core.util.ActivityContract
import com.fiz.mono.core.util.launchAndRepeatWithViewLifecycle
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import com.fiz.mono.pin_password.ui.PINPasswordFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InputFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    private val mainPreferencesViewModel: com.fiz.mono.core.ui.MainPreferencesViewModel by activityViewModels()

    private val viewModel: InputViewModel by viewModels()

    private val args: InputFragmentArgs by navArgs()

    private val cameraActivityLauncher = registerForActivityResult(ActivityContract()) {
        if (it == AppCompatActivity.RESULT_OK)
            viewModel.addPhotoPath()
    }

    private lateinit var binding: FragmentInputBinding

    private lateinit var adapter: CategoriesAdapter

    @Inject
    lateinit var settingsLocalDataSource: SettingsLocalDataSource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        bind()
        bindListener()
        subscribe()
        setupNavigation()
    }

    private fun setupNavigation() {
        launchAndRepeatWithViewLifecycle {
            viewModel.navigationUiState.collect { navigationUiState ->
                if (navigationUiState.isReturn) {
                    findNavController().popBackStack()
                    viewModel.returned()
                }

                if (navigationUiState.isMoveEdit) {
                    val action =
                        InputFragmentDirections
                            .actionToCategoryFragment()
                    findNavController().navigate(action)
                    viewModel.movedEdit()
                }

                if (navigationUiState.isMoveCalendar) {
                    val action =
                        InputFragmentDirections
                            .actionToCalendarFragment()
                    findNavController().navigate(action)
                    viewModel.movedCalendar()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainPreferencesViewModel.start()
    }

    private fun init() {
        viewModel.start(args.transaction)

        adapter = CategoriesAdapter(R.color.blue) { position ->
            viewModel.clickRecyclerView(position)
        }
    }

    private fun bind() {
        binding.apply {
            categoryRecyclerView.adapter = adapter
        }
    }

    private fun bindListener() {
        binding.apply {

            noteCameraEditText.setOnClickListener {
                viewModel.dispatchTakePictureIntent(requireContext())?.let {
                    cameraActivityLauncher.launch(it)
                }
            }

            deletePhoto1ImageView.setOnClickListener {
                viewModel.removePhotoPath(1)
            }

            deletePhoto2ImageView.setOnClickListener {
                viewModel.removePhotoPath(2)
            }

            deletePhoto3ImageView.setOnClickListener {
                viewModel.removePhotoPath(3)
            }

            submitButton.setOnClickListener {

                mainViewModel.date.value?.let {
                    viewModel.onEvent(InputUiEvent.ClickSubmit(it))
                }
            }

            backButton.setOnClickListener {
                viewModel.clickBackButton()
            }

            removeButton.setOnClickListener {
                viewModel.removeTransaction()
            }

            dataRangeLayout.dateTextView.setOnClickListener {
                viewModel.onEvent(InputUiEvent.ClickData)
            }

            valueEditText.doAfterTextChanged {
                viewModel.onEvent(InputUiEvent.ValueChange(it.toString()))
            }

            noteEditText.doAfterTextChanged {
                viewModel.onEvent(InputUiEvent.NoteChange(it.toString()))
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.onEvent(InputUiEvent.ChangeTypeTransactions(tab?.position))
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
                mainViewModel.dateDayMinusOne()
            }

            dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
                mainViewModel.dateDayPlusOne()
            }
        }
    }

    private fun subscribe() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { uiState ->
                when (uiState.isLoading) {
                    true -> {
                        binding.circularProgressIndicator.setVisible(true)
                    }
//                        is Resource.Error -> {
//                            binding.circularProgressIndicator.setVisible(false)
//                            Toast.makeText(
//                                this@InputFragment.requireContext(),
//                                "Error loading CategoryExpense",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    false -> {
                        binding.circularProgressIndicator.setVisible(false)
                        }
                    }




                    binding.ExpenseIncomeTextView.text =
                        getString(uiState.getTextExpenseIncomeTextView)

                    binding.submitButton.isEnabled = uiState.isSubmitButtonEnabled()

                    if (binding.noteEditText.text.toString() != uiState.note)
                        binding.noteEditText.setText(uiState.note)

                    if (binding.valueEditText.text.toString() != uiState.value)
                        binding.valueEditText.setText(uiState.value)

                    binding.submitButton.text = getString(uiState.getTextSubmitButton)

                    binding.dataRangeLayout.root.setVisible(uiState.isInput)
                    binding.tabLayout.setVisible(uiState.isInput)
                    binding.titleTextView.setVisible(uiState.isEdit)
                    binding.backButton.setVisible(uiState.isEdit)
                    binding.removeButton.setVisible(uiState.isEdit)

                    val expenseIncomeTextViewLayoutParams =
                        binding.ExpenseIncomeTextView.layoutParams as ConstraintLayout.LayoutParams
                    expenseIncomeTextViewLayoutParams.topToBottom =
                        if (uiState.isInput) com.fiz.mono.R.id.dataRangeLayout else com.fiz.mono.R.id.titleTextView

                    if (uiState.isInput) {
                        val numberTab = if (viewModel.getSelectedAdapter() == EXPENSE) 0 else 1
                        binding.tabLayout.getTabAt(numberTab)?.select()
                    }

                    val countPhoto = uiState.photoPaths.size

                    binding.noteCameraEditText.isEnabled = isCanAddPhoto()

                    binding.photo1Card.setVisible(countPhoto > 0)
                    binding.photo2Card.setVisible(countPhoto > 0)
                    binding.photo3Card.setVisible(countPhoto > 0)

                    binding.photo1ImageView.setVisible(countPhoto > 0)
                    binding.photo2ImageView.setVisible(countPhoto > 1)
                    binding.photo3ImageView.setVisible(countPhoto > 2)

                    binding.deletePhoto1ImageView.setVisible(countPhoto > 0)
                    binding.deletePhoto2ImageView.setVisible(countPhoto > 1)
                    binding.deletePhoto3ImageView.setVisible(countPhoto > 2)

                    uiState.photoBitmap.getOrNull(0)?.let { bitmap ->
                        binding.photo1ImageView.setImageBitmap(bitmap)
                    } ?: binding.photo1ImageView.setImageBitmap(null)

                    uiState.photoBitmap.getOrNull(1)?.let { bitmap ->
                        binding.photo2ImageView.setImageBitmap(bitmap)
                    } ?: binding.photo2ImageView.setImageBitmap(null)

                    uiState.photoBitmap.getOrNull(2)?.let { bitmap ->
                        binding.photo3ImageView.setImageBitmap(bitmap)
                    } ?: binding.photo3ImageView.setImageBitmap(null)

                    adapter.submitList(uiState.currentCategory)

                    if (uiState.isAllTransactionsLoaded) {
                        uiState.transaction?.let {
                            viewModel.setViewModelTransaction(it)
                            viewModel.setSelected(it.nameCategory)
                        }
                        viewModel.onTrasactionLoaded()
                    }

                    if (uiState.isPhotoPathsChange)
                        viewModel.onPhotoPathsChange()
                }
        }

        mainPreferencesViewModel.apply {
            currency.observe(viewLifecycleOwner) { currency ->
                binding.currencyTextView.text = currency
            }

            firstTime.observe(viewLifecycleOwner) { isFirstTime ->
                if (settingsLocalDataSource.loadFirstTime()) {
                    val action =
                        InputFragmentDirections
                            .actionInputFragmentToOnBoardingFragment()
                    findNavController().navigate(action)
                }
            }

            isConfirmPIN.observe(viewLifecycleOwner) { isConfirmPIN ->
                if (!settingsLocalDataSource.loadConfirmPin()) {
                    val action =
                        InputFragmentDirections
                            .actionToPINPasswordFragment(PINPasswordFragment.START)
                    findNavController().navigate(action)
                }
            }
        }

        mainViewModel.date.observe(viewLifecycleOwner)
        {
            binding.dataRangeLayout.dateTextView.text =
                mainViewModel.getFormatDate("MMM dd, yyyy (EEE)")
            binding.titleTextView.text = mainViewModel.getFormatDate("MMM dd, yyyy (EEE)")
        }
    }

    private fun isCanAddPhoto(): Boolean {
        return viewModel.checkCameraHardware(requireContext())
    }

    companion object {
        const val EXPENSE = 0
        const val INCOME = 1

        const val MAX_PHOTO = 3
    }
}



