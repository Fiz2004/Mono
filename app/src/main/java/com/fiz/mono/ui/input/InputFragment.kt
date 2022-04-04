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
            R.color.blue,
            ::adapterOnClickListener
        )
    }

    private fun bind() {
        binding.apply {
            categoryRecyclerView.adapter = adapter
        }
    }

    private fun bindListener() {
        binding.apply {
            noteCameraEditText.setOnClickListener(::noteCameraOnClickListener)
            deletePhoto1ImageView.setOnClickListener { deletePhotoOnClickListener(1) }
            deletePhoto2ImageView.setOnClickListener { deletePhotoOnClickListener(2) }
            deletePhoto3ImageView.setOnClickListener { deletePhotoOnClickListener(3) }
            submitButton.setOnClickListener(::submitButtonOnClickListener)
            backButton.setOnClickListener(::backButtonOnClickListener)

            removeButton.setOnClickListener {
                viewModel.removeTransaction(viewModel.transaction.value)
                findNavController().popBackStack()
            }

            dataRangeLayout.leftDateRangeImageButton.setOnClickListener(::leftDateRangeOnClickListener)
            dataRangeLayout.dateTextView.setOnClickListener(::dateOnClickListener)
            dataRangeLayout.rightDateRangeImageButton.setOnClickListener(::rightDateRangeOnClickListener)

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

            photoPaths.observe(viewLifecycleOwner, ::photoPathObserve)
            note.observe(viewLifecycleOwner, ::noteObserve)
            value.observe(viewLifecycleOwner, ::valueObserve)
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

    private fun backButtonOnClickListener(view: View?) {
        findNavController().popBackStack()
    }

    private fun rightDateRangeOnClickListener(view: View) {
        mainViewModel.dateDayPlusOne()
    }

    private fun dateOnClickListener(view: View) {
        val action =
            InputFragmentDirections
                .actionToCalendarFragment()
        findNavController().navigate(action)
    }

    private fun leftDateRangeOnClickListener(view: View) {
        mainViewModel.dateDayMinusOne()
    }

    private fun submitButtonOnClickListener(view: View) {
        val selectedCategoryItem =
            viewModel.getSelectedInputForInput(viewModel.selectedAdapter.value)
        if (viewModel.transaction != null) {
            val transaction = viewModel.getTransactionItemForUpdate(selectedCategoryItem) ?: return
            viewModel.clickUpdate(
                transaction
            )
            findNavController().popBackStack()
        } else {
            val newId = viewModel.getNewId()
            val transaction =
                viewModel.getTransactionItemForNew(
                    selectedCategoryItem,
                    newId,
                    mainViewModel.date.value!!
                )
            viewModel.clickSubmit(transaction)
            viewModel.cleanSelectedForInput()
            viewModel.clickSubmit()
            adapter.submitList(viewModel.getAllCategoryFromSelectedForInput(viewModel.selectedAdapter.value))
        }
    }

    private fun adapterOnClickListener(position: Int) {
        if (checkClickEditPosition(position)) return
        viewModel.addSelectItem(position, viewModel.selectedAdapter.value)
        viewModel.changeSelected()
        adapter.submitList(viewModel.getAllCategoryFromSelectedForInput(viewModel.selectedAdapter.value))
    }

    private fun checkClickEditPosition(position: Int): Boolean {
        if (viewModel.isClickEditPosition(position, viewModel.selectedAdapter.value)) {
            viewModel.cleanSelectedForInput()
            val action =
                InputFragmentDirections
                    .actionToCategoryFragment()
            findNavController().navigate(action)
            return true
        }
        return false
    }

    private fun deletePhotoOnClickListener(number: Int) {
        viewModel.removePhotoPath(number)
    }

    private fun noteCameraOnClickListener(view: View) {
        viewModel.dispatchTakePictureIntent(requireContext())?.let {
            cameraActivityLauncher.launch(it)
        }
    }

    private fun noteObserve(note: String) {
        if (binding.noteEditText.text.toString() == note)
            return

        binding.noteEditText.setText(note)
    }

    private fun valueObserve(value: String) {
        binding.submitButton.isEnabled =
            viewModel.isCanSubmit() && viewModel.isSelectedForInput(viewModel.selectedAdapter.value)
        if (binding.valueEditText.text.toString() == value)
            return

        binding.valueEditText.setText(value)
    }

    private fun photoPathObserve(photoPaths: MutableList<String?>) {
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

        photoPaths.getOrNull(0)?.let { photoPath ->
            viewModel.setPic(300, 300, photoPath).also {
                binding.photo1ImageView.setImageBitmap(it)
            }
        } ?: binding.photo1ImageView.setImageBitmap(null)
        photoPaths.getOrNull(1)?.let { photoPath ->
            viewModel.setPic(300, 300, photoPath).also {
                binding.photo2ImageView.setImageBitmap(it)
            }
        } ?: binding.photo2ImageView.setImageBitmap(null)
        photoPaths.getOrNull(2)?.let { photoPath ->
            viewModel.setPic(300, 300, photoPath).also {
                binding.photo3ImageView.setImageBitmap(it)
            }
        } ?: binding.photo3ImageView.setImageBitmap(null)
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



