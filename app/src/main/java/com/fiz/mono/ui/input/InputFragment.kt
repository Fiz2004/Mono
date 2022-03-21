package com.fiz.mono.ui.input

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.util.ActivityContract
import com.fiz.mono.util.CategoriesAdapter
import com.fiz.mono.util.setDisabled
import com.fiz.mono.util.setEnabled
import com.google.android.material.tabs.TabLayout

class InputFragment : Fragment() {
    private val args: InputFragmentArgs by navArgs()

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CategoryInputViewModel by viewModels()

    private lateinit var adapter: CategoriesAdapter

    private var cashCheckCameraHardware: Boolean? = null

    private val cameraActivityLauncher = registerForActivityResult(ActivityContract()) { result ->
        viewModel.addPhotoPath()
        updateUI()
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

        if (mainViewModel.firstTime) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToOnBoardingFragment()
            view.findNavController().navigate(action)
            return
        }

        if (!args.log && mainViewModel.PIN.isNotBlank()) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToPINPasswordFragment(PINPasswordFragment.START)
            view.findNavController().navigate(action)
            return
        }

        if (viewModel.edit) {
            viewModel.initLoad()
            viewModel.edit = false
        }

        binding.noteCameraEditText.setOnClickListener(::noteCameraOnClickListener)
        binding.deletePhoto1ImageView.setOnClickListener { deletePhotoOnClickListener(1) }
        binding.deletePhoto2ImageView.setOnClickListener { deletePhotoOnClickListener(2) }
        binding.deletePhoto3ImageView.setOnClickListener { deletePhotoOnClickListener(3) }
        binding.submitButton.setOnClickListener(::submitButtonOnClickListener)
        binding.dataRangeLayout.leftDateRangeImageButton.setOnClickListener(::leftDateRangeOnClickListener)
        binding.dataRangeLayout.rightDateRangeImageButton.setOnClickListener(::rightDateRangeOnClickListener)
        binding.valueEditText.doOnTextChanged(::valueEditTextOnTextChanged)
        binding.noteEditText.doOnTextChanged(::noteEditTextOnTextChanged)

        adapter = CategoriesAdapter(R.color.blue, ::adapterOnClickListener)
        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener())
        val numberTab = if (viewModel.getSelectedAdapter() == EXPENSE) 0 else 1
        binding.tabLayout.getTabAt(numberTab)?.select()
        binding.categoryRecyclerView.adapter = adapter

        val allCategory = viewModel.getAllCategoryFromSelected()
        adapter.submitList(allCategory)

        viewModel.allCategoryExpense.observe(viewLifecycleOwner) {
            val allCategory = viewModel.getAllCategoryFromSelected()
            adapter.submitList(allCategory)
        }
        viewModel.allCategoryIncome.observe(viewLifecycleOwner) {
            val allCategory = viewModel.getAllCategoryFromSelected()
            adapter.submitList(allCategory)
        }
        viewModel.photoPath.observe(viewLifecycleOwner) {
            it.getOrNull(0)?.let {
                viewModel.setPic(300, 300, it).also {
                    binding.photo1ImageView.setImageBitmap(it)
                }
            } ?: binding.photo1ImageView.setImageBitmap(null)
            it.getOrNull(1)?.let {
                viewModel.setPic(300, 300, it).also {
                    binding.photo2ImageView.setImageBitmap(it)
                }
            } ?: binding.photo2ImageView.setImageBitmap(null)
            it.getOrNull(2)?.let {
                viewModel.setPic(300, 300, it).also {
                    binding.photo3ImageView.setImageBitmap(it)
                }
            } ?: binding.photo3ImageView.setImageBitmap(null)
        }
        viewModel.note.observe(viewLifecycleOwner) {
//            binding.noteEditText.setText(it.toString())
        }
        viewModel.value.observe(viewLifecycleOwner) {
//            if (it == 0.0)
//                binding.valueEditText.setText("")
//            else
//                binding.valueEditText.setText(it.toString())
        }

        updateUI()
    }

    private fun valueEditTextOnTextChanged(
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        viewModel.setValue(text)
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
        mainViewModel.datePlusOne()
        updateUI()
    }

    private fun leftDateRangeOnClickListener(view: View) {
        mainViewModel.dateMinusOne()
        updateUI()
    }

    private fun submitButtonOnClickListener(view: View) {
        viewModel.clickSubmit()
        adapter.submitList(viewModel.getAllCategoryFromSelected())
        updateUI()
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
                viewModel.edit = true
                val action =
                    InputFragmentDirections
                        .actionInputFragmentToCategoryFragment("", 0, "")
                view?.findNavController()?.navigate(action)
                return true
            }
        } else {
            if (viewModel.isClickEditPositionIncome(position)) {
                viewModel.cleanSelected()
                viewModel.edit = true
                val action =
                    InputFragmentDirections
                        .actionInputFragmentToCategoryFragment("", 0, "")
                view?.findNavController()?.navigate(action)
                return true
            }
        }
        return false
    }

    private fun deletePhotoOnClickListener(number: Int) {
        viewModel.removePhotoPath(number)
        updateUI()
    }

    private fun noteCameraOnClickListener(view: View) {
        viewModel.dispatchTakePictureIntent(requireContext())?.let {
            cameraActivityLauncher.launch(it)
        }

        updateUI()
    }

    private fun updateUI() {
        binding.currencyTextView.text = mainViewModel.currency
        binding.dataRangeLayout.editTextDate.text = mainViewModel.getFormatDate()
        binding.ExpenseIncomeTextView.text = viewModel.getTypeFromSelectedAdapter(requireContext())

        binding.noteCameraEditText.isEnabled = checkCameraHardware(requireActivity())
        if (viewModel.photoPath.value?.size == 3)
            binding.noteCameraEditText.isEnabled = false

        if (viewModel.photoPath.value?.size == 0) {
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
            when (viewModel.photoPath.value?.size) {
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

        if (viewModel.isSelected() && viewModel.value.value != 0.0)
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

    companion object {
        const val EXPENSE = 0
        const val INCOME = 1

    }
}

