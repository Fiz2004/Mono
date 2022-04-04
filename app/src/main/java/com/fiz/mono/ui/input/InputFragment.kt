package com.fiz.mono.ui.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.MainPreferencesViewModelFactory
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.ui.shared_adapters.CategoriesAdapter
import com.fiz.mono.util.ActivityContract
import com.fiz.mono.util.setVisible
import com.google.android.material.tabs.TabLayout

class InputFragment : Fragment() {
    private val args: InputFragmentArgs by navArgs()

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InputViewModel by viewModels {
        InputViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).transactionStore
        )
    }

    private val mainViewModel: MainViewModel by activityViewModels()

    private val mainPreferencesViewModel: MainPreferencesViewModel by activityViewModels {
        MainPreferencesViewModelFactory(
            requireActivity().getSharedPreferences(
                getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            )
        )
    }

    private val cameraActivityLauncher = registerForActivityResult(ActivityContract()) {
        if (it == 1)
            viewModel.addPhotoPath()
    }

    private lateinit var adapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
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
        viewModel.init(args.transaction)

        adapter = CategoriesAdapter(
            (requireActivity().application as App).categoryIconStore,
            R.color.blue
        ) { position ->
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
                viewModel.clickSubmitButton(mainViewModel.date.value!!)
            }

            backButton.setOnClickListener {
                viewModel.clickBackButton()
            }

            removeButton.setOnClickListener {
                viewModel.removeTransaction()
            }

            dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
                mainViewModel.dateDayMinusOne()
            }

            dataRangeLayout.dateTextView.setOnClickListener {
                viewModel.clickDate()
            }

            dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
                mainViewModel.dateDayPlusOne()
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> viewModel.setSelectedAdapter(EXPENSE)
                        1 -> viewModel.setSelectedAdapter(INCOME)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            valueEditText.doOnTextChanged { text, start, before, count ->
                viewModel.setValue(text.toString())
            }

            noteEditText.doOnTextChanged { text, start, before, count ->
                viewModel.setNote(text.toString())
            }
        }
    }

    private fun subscribe() {
        viewModel.apply {
            allCategoryExpense.observe(viewLifecycleOwner) {
                viewModel.transaction.value?.let {
                    if (viewModel.getSelectedAdapter() == EXPENSE)
                        viewModel.setSelected(viewModel.selectedAdapter.value, it.nameCategory)
                }

                val allCategory =
                    viewModel.getAllCategoryFromSelectedForInput(viewModel.selectedAdapter.value)
                adapter.submitList(allCategory)
            }

            allCategoryIncome.observe(viewLifecycleOwner) {
                viewModel.transaction.value?.let {
                    if (viewModel.getSelectedAdapter() == INCOME)
                        viewModel.setSelected(viewModel.selectedAdapter.value, it.nameCategory)
                }

                val allCategory =
                    viewModel.getAllCategoryFromSelectedForInput(viewModel.selectedAdapter.value)
                adapter.submitList(allCategory)
            }

            photoPaths.observe(viewLifecycleOwner) { photoPaths ->
                val countPhoto = viewModel.photoPaths.value?.size ?: 0

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
            }

            photoBitmap.observe(viewLifecycleOwner) {
                it.getOrNull(0)?.let { bitmap ->
                    binding.photo1ImageView.setImageBitmap(bitmap)
                } ?: binding.photo1ImageView.setImageBitmap(null)

                it.getOrNull(1)?.let { bitmap ->
                    binding.photo2ImageView.setImageBitmap(bitmap)
                } ?: binding.photo2ImageView.setImageBitmap(null)

                it.getOrNull(2)?.let { bitmap ->
                    binding.photo3ImageView.setImageBitmap(bitmap)
                } ?: binding.photo3ImageView.setImageBitmap(null)
            }

            note.observe(viewLifecycleOwner) { note ->
                if (binding.noteEditText.text.toString() == note)
                    return@observe

                binding.noteEditText.setText(note)
            }

            value.observe(viewLifecycleOwner) { value ->
                binding.submitButton.isEnabled = viewModel.isSubmitButtonEnabled()
                if (binding.valueEditText.text.toString() == value)
                    return@observe

                binding.valueEditText.setText(value)
            }

            selectedAdapter.observe(viewLifecycleOwner) {
                binding.ExpenseIncomeTextView.text =
                    viewModel.getTypeFromSelectedAdapter(requireContext())
                binding.submitButton.isEnabled = viewModel.isSubmitButtonEnabled()
            }

            selected.observe(viewLifecycleOwner) {
                binding.submitButton.isEnabled = viewModel.isSubmitButtonEnabled()
            }

            transaction.observe(viewLifecycleOwner) {
                val isInput = it == null
                val isEdit = !isInput

                binding.submitButton.text =
                    if (isInput) getString(R.string.submit) else getString(R.string.update)

                binding.dataRangeLayout.root.setVisible(isInput)
                binding.tabLayout.setVisible(isInput)
                binding.titleTextView.setVisible(isEdit)
                binding.backButton.setVisible(isEdit)
                binding.removeButton.setVisible(isEdit)

                val expenseIncomeTextViewLayoutParams =
                    binding.ExpenseIncomeTextView.layoutParams as ConstraintLayout.LayoutParams
                expenseIncomeTextViewLayoutParams.topToBottom =
                    if (isInput) R.id.dataRangeLayout else R.id.titleTextView

                if (isInput) {
                    val numberTab = if (viewModel.getSelectedAdapter() == EXPENSE) 0 else 1
                    binding.tabLayout.getTabAt(numberTab)?.select()
                } else {
                    viewModel.setViewmodelTransaction(viewModel.transaction.value!!)
                }
            }

            isReturn.observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().popBackStack()
                    viewModel.isReturnRefresh()
                }
            }

            isMoveEdit.observe(viewLifecycleOwner) {
                if (it) {
                    val action =
                        InputFragmentDirections
                            .actionToCategoryFragment()
                    findNavController().navigate(action)
                    viewModel.isMoveEditRefresh()
                }
            }

            isMoveCalendar.observe(viewLifecycleOwner) {
                if (it) {
                    val action =
                        InputFragmentDirections
                            .actionToCalendarFragment()
                    findNavController().navigate(action)
                    viewModel.isMoveCalendarRefresh()
                }
            }
        }

        mainPreferencesViewModel.apply {
            currency.observe(viewLifecycleOwner) { currency ->
                binding.currencyTextView.text = currency
            }

            isFirstTime.observe(viewLifecycleOwner) { isFirstTime ->
                if (isFirstTime) {
                    val action =
                        InputFragmentDirections
                            .actionInputFragmentToOnBoardingFragment()
                    findNavController().navigate(action)
                }
            }

            isConfirmPIN.observe(viewLifecycleOwner) { isConfirmPIN ->
                if (!isConfirmPIN) {
                    val action =
                        InputFragmentDirections
                            .actionToPINPasswordFragment(PINPasswordFragment.START)
                    findNavController().navigate(action)
                }
            }
        }

        mainViewModel.date.observe(viewLifecycleOwner) {
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



