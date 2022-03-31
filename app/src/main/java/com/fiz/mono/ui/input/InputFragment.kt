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
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.ui.MainPreferencesViewModel
import com.fiz.mono.ui.MainPreferencesViewModelFactory
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.MainViewModelFactory
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.ui.shared_adapters.CategoriesAdapter
import com.fiz.mono.util.ActivityContract
import com.fiz.mono.util.setVisible
import com.google.android.material.tabs.TabLayout
import java.util.*

class InputFragment : Fragment() {
    private val args: InputFragmentArgs by navArgs()

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).transactionStore
        )
    }

    private val mainPreferencesViewModel: MainPreferencesViewModel by activityViewModels {
        MainPreferencesViewModelFactory(
            requireActivity().getSharedPreferences(
                getString(R.string.preferences),
                AppCompatActivity.MODE_PRIVATE
            )
        )
    }

    private val viewModel: InputViewModel by viewModels {
        InputViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).transactionStore
        )
    }

    private val cameraActivityLauncher = registerForActivityResult(ActivityContract()) {
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
        subscribe()
    }

    private fun init() {
        viewModel.findTransaction(args.transaction)

        adapter = CategoriesAdapter(R.color.blue, ::adapterOnClickListener)
    }

    private fun bind() {
        binding.apply {
            submitButton.text =
                if (viewModel.transaction == null) getString(R.string.submit) else getString(R.string.update)

            val isInput = viewModel.transaction == null
            val isEdit = !isInput

            dataRangeLayout.root.setVisible(isInput)
            tabLayout.setVisible(isInput)
            titleTextView.setVisible(isEdit)
            backButton.setVisible(isEdit)
            removeButton.setVisible(isEdit)

            val expenseIncomeTextViewLayoutParams =
                ExpenseIncomeTextView.layoutParams as ConstraintLayout.LayoutParams
            expenseIncomeTextViewLayoutParams.topToBottom =
                if (isInput) R.id.dataRangeLayout else R.id.titleTextView

            if (isInput) {
                val numberTab = if (viewModel.getSelectedAdapter() == EXPENSE) 0 else 1
                tabLayout.getTabAt(numberTab)?.select()
            } else {
                viewModel.setViewmodelTransaction(viewModel.transaction!!)
            }

            categoryRecyclerView.adapter = adapter

            noteCameraEditText.setOnClickListener(::noteCameraOnClickListener)
            deletePhoto1ImageView.setOnClickListener { deletePhotoOnClickListener(1) }
            deletePhoto2ImageView.setOnClickListener { deletePhotoOnClickListener(2) }
            deletePhoto3ImageView.setOnClickListener { deletePhotoOnClickListener(3) }
            submitButton.setOnClickListener(::submitButtonOnClickListener)
            backButton.setOnClickListener(::backButtonOnClickListener)
            removeButton.setOnClickListener(::removeButtonOnClickListener)
            dataRangeLayout.leftDateRangeImageButton.setOnClickListener(::leftDateRangeOnClickListener)
            dataRangeLayout.dateTextView.setOnClickListener(::dateOnClickListener)
            dataRangeLayout.rightDateRangeImageButton.setOnClickListener(::rightDateRangeOnClickListener)
            tabLayout.addOnTabSelectedListener(onTabSelectedListener())

            valueEditText.doOnTextChanged(::valueEditTextOnTextChanged)
            noteEditText.doOnTextChanged(::noteEditTextOnTextChanged)
        }
    }

    private fun subscribe() {
        viewModel.apply {
            allCategoryExpense.observe(viewLifecycleOwner, ::allCategoryExpenseObserve)
            allCategoryIncome.observe(viewLifecycleOwner, ::allCategoryIncomeObserve)
            photoPaths.observe(viewLifecycleOwner, ::photoPathObserve)
            note.observe(viewLifecycleOwner, ::noteObserve)
            value.observe(viewLifecycleOwner, ::valueObserve)
            allTransaction.observe(viewLifecycleOwner) {}
            selectedAdapter.observe(viewLifecycleOwner, ::selectedAdapterObserve)
            selected.observe(viewLifecycleOwner, ::selectedObserve)
        }
        mainPreferencesViewModel.apply {
            currency.observe(viewLifecycleOwner, ::currencyObserve)

            firstTime.observe(viewLifecycleOwner) {
                if (it) {
                    val action =
                        InputFragmentDirections
                            .actionInputFragmentToOnBoardingFragment()
                    findNavController().navigate(action)
                }
            }

            isConfirmPIN.observe(viewLifecycleOwner) {
                if (!it) {
                    val action =
                        InputFragmentDirections
                            .actionToPINPasswordFragment(PINPasswordFragment.START)
                    findNavController().navigate(action)
                }
            }
        }
        mainViewModel.date.observe(viewLifecycleOwner, ::dateObserve)
    }

    private fun removeButtonOnClickListener(view: View?) {
        viewModel.removeTransaction()
        findNavController().popBackStack()
    }

    private fun backButtonOnClickListener(view: View?) {
        findNavController().popBackStack()
    }

    private fun valueEditTextOnTextChanged(
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        viewModel.setValue(text.toString())
    }

    private fun noteEditTextOnTextChanged(
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        viewModel.setNote(text.toString())
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
        if (viewModel.transaction != null) {
            viewModel.clickUpdate(viewModel.transaction!!)
            findNavController().popBackStack()
        } else {
            viewModel.clickSubmit(mainViewModel.date.value?.time!!)
            adapter.submitList(viewModel.getAllCategoryFromSelected())
        }
    }

    private fun adapterOnClickListener(position: Int) {
        if (checkClickEditPosition(position)) return
        viewModel.addSelectItem(position)
        adapter.submitList(viewModel.getAllCategoryFromSelected())
    }

    private fun checkClickEditPosition(position: Int): Boolean {
        if (viewModel.isClickEditPosition(position)) {
            viewModel.cleanSelected()
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

    private fun onTabSelectedListener() = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab != null) {
                when (tab.text) {
                    getString(R.string.expense) -> {
                        viewModel.setSelectedAdapter(EXPENSE)
                        adapter.submitList(viewModel.getAllCategoryItemExpense())
                    }
                    getString(R.string.income) -> {
                        viewModel.setSelectedAdapter(INCOME)
                        adapter.submitList(viewModel.getAllCategoryItemIncome())
                    }
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }

    private fun noteObserve(note: String) {
        if (binding.noteEditText.text.toString() == note)
            return

        binding.noteEditText.setText(note)
    }

    private fun valueObserve(value: String) {
        binding.submitButton.isEnabled = viewModel.isCanSubmit()
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
        binding.deletePhoto1ImageView.setVisible(countPhoto > 1)
        binding.deletePhoto1ImageView.setVisible(countPhoto > 2)

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

    private fun allCategoryIncomeObserve(allCategoryIncome: List<CategoryItem>) {
        viewModel.transaction?.let {
            if (viewModel.getSelectedAdapter() == INCOME)
                viewModel.setSelected(it.nameCategory)
        }

        val allCategory = viewModel.getAllCategoryFromSelected()
        adapter.submitList(allCategory)
    }

    private fun allCategoryExpenseObserve(allCategoryExpense: List<CategoryItem>) {
        viewModel.transaction?.let {
            if (viewModel.getSelectedAdapter() == EXPENSE)
                viewModel.setSelected(it.nameCategory)
        }

        val allCategory = viewModel.getAllCategoryFromSelected()
        adapter.submitList(allCategory)
    }

    private fun dateObserve(date: Calendar) {
        binding.dataRangeLayout.dateTextView.text =
            mainViewModel.getFormatDate("MMM dd, yyyy (EEE)")
        binding.titleTextView.text = mainViewModel.getFormatDate("MMM dd, yyyy (EEE)")
    }

    private fun currencyObserve(currency: String) {
        binding.currencyTextView.text = currency
    }

    private fun selectedObserve(selected: Boolean) {
        binding.submitButton.isEnabled = viewModel.isCanSubmit()
    }

    private fun selectedAdapterObserve(selectedAdapter: Int) {
        binding.ExpenseIncomeTextView.text =
            viewModel.getTypeFromSelectedAdapter(requireContext())
        binding.submitButton.isEnabled = viewModel.isCanSubmit()
    }

    companion object {
        const val EXPENSE = 0
        const val INCOME = 1

        const val MAX_PHOTO = 3
    }
}

