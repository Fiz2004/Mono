package com.fiz.mono.ui.input

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.ui.shared_adapters.CategoriesAdapter
import com.fiz.mono.util.ActivityContract
import com.fiz.mono.util.setDisabled
import com.fiz.mono.util.setEnabled
import com.google.android.material.tabs.TabLayout
import kotlin.math.abs


class InputFragment : Fragment() {
    private val args: InputFragmentArgs by navArgs()

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    var currentTransaction = -1
    var transaction: TransactionItem? = null

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CategoryInputViewModel by viewModels(factoryProducer = viewModelInit())

    private val cameraActivityLauncher =
        registerForActivityResult(ActivityContract(), ::resultCameraActivity)

    private lateinit var adapter: CategoriesAdapter

    private var cashCheckCameraHardware: Boolean? = null

    private fun resultCameraActivity(result: Intent?) {
        viewModel.addPhotoPath()
    }

    private fun viewModelInit(): () -> CategoryInputViewModelFactory = {
        CategoryInputViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).transactionStore
        )
    }

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

        if (isStartFirstTime() || isNeedLog()) return

        adapter = CategoriesAdapter(R.color.blue, ::adapterOnClickListener)
        val numberTab = if (viewModel.getSelectedAdapter() == EXPENSE) 0 else 1

        currentTransaction = args.transaction
        if (currentTransaction != -1)
            transaction = viewModel.findTransaction(currentTransaction)

        binding.apply {

            noteCameraEditText.setOnClickListener(::noteCameraOnClickListener)
            deletePhoto1ImageView.setOnClickListener { deletePhotoOnClickListener(1) }
            deletePhoto2ImageView.setOnClickListener { deletePhotoOnClickListener(2) }
            deletePhoto3ImageView.setOnClickListener { deletePhotoOnClickListener(3) }
            submitButton.setOnClickListener(::submitButtonOnClickListener)
            backButton.setOnClickListener(::backButtonOnClickListener)
            removeButton.setOnClickListener(::removeButtonOnClickListener)

            valueEditText.doOnTextChanged(::valueEditTextOnTextChanged)
            noteEditText.doOnTextChanged(::noteEditTextOnTextChanged)
            categoryRecyclerView.adapter = adapter

            if (currentTransaction == -1) {
                view.findViewById<View>(R.id.dataRangeLayout).visibility = View.VISIBLE
                dataRangeLayout.leftDateRangeImageButton.setOnClickListener(::leftDateRangeOnClickListener)
                dataRangeLayout.dateTextView.setOnClickListener(::dateOnClickListener)
                dataRangeLayout.rightDateRangeImageButton.setOnClickListener(::rightDateRangeOnClickListener)
                tabLayout.visibility = View.VISIBLE
                tabLayout.addOnTabSelectedListener(onTabSelectedListener())
                tabLayout.getTabAt(numberTab)?.select()

                titleTextView.visibility = View.GONE
                backButton.visibility = View.GONE
                removeButton.visibility = View.GONE

                (ExpenseIncomeTextView.layoutParams as ConstraintLayout.LayoutParams).topToBottom =
                    R.id.dataRangeLayout
                (ExpenseIncomeTextView.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 24F, resources.displayMetrics
                    ).toInt()

                submitButton.text = getString(R.string.submit)
            } else {
                view.findViewById<View>(R.id.dataRangeLayout).visibility = View.GONE
                tabLayout.visibility = View.GONE
                titleTextView.visibility = View.VISIBLE
                backButton.visibility = View.VISIBLE
                removeButton.visibility = View.VISIBLE

                (ExpenseIncomeTextView.layoutParams as ConstraintLayout.LayoutParams).topToBottom =
                    R.id.titleTextView
                (ExpenseIncomeTextView.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 24F, resources.displayMetrics
                    ).toInt()

                titleTextView.text = mainViewModel.getFormatDate("MMM dd, yyyy (EEE)")

                if (transaction?.value!! > 0)
                    viewModel.setSelectedAdapter(INCOME)
                else
                    viewModel.setSelectedAdapter(EXPENSE)

                transaction!!.value = abs(transaction!!.value)

                ExpenseIncomeTextView.text = viewModel.getTypeFromSelectedAdapter(requireContext())

                viewModel.setValue(transaction!!.value.toString())
                viewModel.setNote(transaction!!.note)
                viewModel.setPhotoPath(transaction!!.photo.toMutableList())

                submitButton.text = getString(R.string.update)
            }
        }

        viewModel.apply {
            allCategoryExpense.observe(viewLifecycleOwner, ::allCategoryExpenseObserve)
            allCategoryIncome.observe(viewLifecycleOwner, ::allCategoryIncomeObserve)
            photoPaths.observe(viewLifecycleOwner, ::photoPathObserve)
            note.observe(viewLifecycleOwner, ::noteObserve)
            value.observe(viewLifecycleOwner, ::valueObserve)
            allTransaction.observe(viewLifecycleOwner) {}
        }

        updateUI()
    }

    private fun removeButtonOnClickListener(view: View?) {
        viewModel.removeTransaction(transaction!!)
        findNavController().popBackStack()
    }

    private fun backButtonOnClickListener(view: View?) {
        findNavController().popBackStack()
    }

    private fun isStartFirstTime(): Boolean {
        if (mainViewModel.firstTime) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToOnBoardingFragment()
            findNavController().navigate(action)
            return true
        }
        return false
    }

    private fun isNeedLog(): Boolean {
        if (!args.log && mainViewModel.PIN.isNotBlank()) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToPINPasswordFragment(PINPasswordFragment.START)
            findNavController().navigate(action)
            return true
        }
        return false
    }

    private fun valueEditTextOnTextChanged(
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        viewModel.setValue(text.toString())
        updateUI()
    }

    private fun noteEditTextOnTextChanged(
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        viewModel.setNote(text.toString())
    }

    private fun checkCameraHardware(context: Context): Boolean {
        if (cashCheckCameraHardware == null)
            cashCheckCameraHardware =
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        return cashCheckCameraHardware ?: false
    }

    private fun rightDateRangeOnClickListener(view: View) {
        mainViewModel.dateDayPlusOne()
        updateUI()
    }

    private fun dateOnClickListener(view: View) {
        val action =
            InputFragmentDirections
                .actionInputFragmentToCalendarFragment()
        findNavController().navigate(action)
    }

    private fun leftDateRangeOnClickListener(view: View) {
        mainViewModel.dateDayMinusOned()
        updateUI()
    }

    private fun submitButtonOnClickListener(view: View) {
        if (transaction != null) {
            viewModel.clickUpdate(transaction!!)
            findNavController().popBackStack()
        } else {
            viewModel.clickSubmit(mainViewModel.date.value?.time!!)
            adapter.submitList(viewModel.getAllCategoryFromSelected())
            updateUI()
        }
    }

    private fun adapterOnClickListener(position: Int) {
        if (checkClickEditPosition(position)) return
        viewModel.addSelectItem(position)
        adapter.submitList(viewModel.getAllCategoryFromSelected())
        updateUI()
    }

    private fun checkClickEditPosition(position: Int): Boolean {
        if (viewModel.getSelectedAdapter() == EXPENSE) {
            if (viewModel.isClickEditPositionExpense(position)) {
                viewModel.cleanSelected()
                val action =
                    InputFragmentDirections
                        .actionInputFragmentToCategoryFragment("", "", "")
                findNavController().navigate(action)
                return true
            }
        } else {
            if (viewModel.isClickEditPositionIncome(position)) {
                viewModel.cleanSelected()
                val action =
                    InputFragmentDirections
                        .actionInputFragmentToCategoryFragment("", "", "")
                findNavController().navigate(action)
                return true
            }
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

    private fun updateUI() {
        binding.currencyTextView.text = mainViewModel.currency
        binding.dataRangeLayout.dateTextView.text =
            mainViewModel.getFormatDate("MMM dd, yyyy (EEE)")
        binding.ExpenseIncomeTextView.text = viewModel.getTypeFromSelectedAdapter(requireContext())

        binding.noteCameraEditText.isEnabled = checkCameraHardware(requireActivity())

        if (viewModel.isSelected() && viewModel.value.value?.isNotBlank() == true)
            binding.submitButton.setEnabled()
        else
            binding.submitButton.setDisabled()
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
                updateUI()
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }

    private fun noteObserve(note: String) {
        if (binding.noteEditText.text.toString() == note.toString())
            return

        binding.noteEditText.setText(note.toString())
    }

    private fun valueObserve(value: String) {
        if (binding.valueEditText.text.toString() == value.toString())
            return

        binding.valueEditText.setText(value.toString())
    }

    private fun photoPathObserve(photoPaths: MutableList<String?>) {
        if (viewModel.photoPaths.value?.size == 3)
            binding.noteCameraEditText.isEnabled = false

        if (viewModel.photoPaths.value?.size == 0) {
            binding.photo1Card.visibility = View.GONE
            binding.photo2Card.visibility = View.GONE
            binding.photo3Card.visibility = View.GONE

            binding.photo1ImageView.visibility = View.GONE
            binding.photo2ImageView.visibility = View.GONE
            binding.photo3ImageView.visibility = View.GONE

            binding.deletePhoto1ImageView.visibility = View.GONE
            binding.deletePhoto2ImageView.visibility = View.GONE
            binding.deletePhoto3ImageView.visibility = View.GONE
        } else {
            binding.photo1Card.visibility = View.VISIBLE
            binding.photo2Card.visibility = View.VISIBLE
            binding.photo3Card.visibility = View.VISIBLE
            when (viewModel.photoPaths.value?.size) {
                1 -> {
                    binding.photo1ImageView.visibility = View.VISIBLE
                    binding.photo2ImageView.visibility = View.GONE
                    binding.photo3ImageView.visibility = View.GONE

                    binding.deletePhoto1ImageView.visibility = View.VISIBLE
                    binding.deletePhoto2ImageView.visibility = View.GONE
                    binding.deletePhoto3ImageView.visibility = View.GONE
                }
                2 -> {
                    binding.photo1ImageView.visibility = View.VISIBLE
                    binding.photo2ImageView.visibility = View.VISIBLE
                    binding.photo3ImageView.visibility = View.GONE

                    binding.deletePhoto1ImageView.visibility = View.VISIBLE
                    binding.deletePhoto2ImageView.visibility = View.VISIBLE
                    binding.deletePhoto3ImageView.visibility = View.GONE

                }
                3 -> {
                    binding.photo1ImageView.visibility = View.VISIBLE
                    binding.photo2ImageView.visibility = View.VISIBLE
                    binding.photo3ImageView.visibility = View.VISIBLE

                    binding.deletePhoto1ImageView.visibility = View.VISIBLE
                    binding.deletePhoto2ImageView.visibility = View.VISIBLE
                    binding.deletePhoto3ImageView.visibility = View.VISIBLE
                }
            }
        }



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

    private fun allCategoryIncomeObserve(allCategoryIncome: List<CategoryItem>) {
        transaction?.let {
            if (viewModel.getSelectedAdapter() == INCOME)
                it.nameCategory.let {
                    viewModel.setSelected(
                        it
                    )
                }
        }
        val allCategory = viewModel.getAllCategoryFromSelected()
        adapter.submitList(allCategory)
    }

    private fun allCategoryExpenseObserve(allCategoryExpense: List<CategoryItem>) {
        transaction?.let {
            if (viewModel.getSelectedAdapter() == EXPENSE)
                it.nameCategory.let {
                    viewModel.setSelected(
                        it
                    )
                }
        }
        val allCategory = viewModel.getAllCategoryFromSelected()
        adapter.submitList(allCategory)
    }

    companion object {
        const val EXPENSE = 0
        const val INCOME = 1

    }
}

