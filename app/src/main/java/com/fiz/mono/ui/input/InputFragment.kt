package com.fiz.mono.ui.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.fiz.mono.R
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.util.setDisabled
import com.fiz.mono.util.setEnabled
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class InputFragment : Fragment() {
    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InputViewModel by activityViewModels()

    private lateinit var adapter: CategoryInputAdapter

    private var selectedAdapter: Int? = 0
    private var selectedItem: Int? = null

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
        if (viewModel.firstTime) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToOnBoardingFragment()
            view.findNavController().navigate(action)
            return
        }
        if (!viewModel.log) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToPINPasswordFragment(PINPasswordFragment.START)
            view.findNavController().navigate(action)
            return
        }

        binding.currencyTextView.text = viewModel.currency

        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener())

        binding.noteCameraInputEditText.setOnClickListener {
            binding.foto1.visibility = View.VISIBLE
            binding.foto2.visibility = View.VISIBLE
            binding.foto3.visibility = View.VISIBLE

            binding.delFoto1.visibility = View.VISIBLE
            binding.delFoto2.visibility = View.VISIBLE
            binding.delFoto3.visibility = View.VISIBLE
        }

        binding.delFoto1.setOnClickListener {
            binding.foto1.visibility = View.GONE
            binding.foto2.visibility = View.GONE
            binding.foto3.visibility = View.GONE

            binding.delFoto1.visibility = View.GONE
            binding.delFoto2.visibility = View.GONE
            binding.delFoto3.visibility = View.GONE
        }

        adapter = CategoryInputAdapter(R.color.blue) { position ->

            if (position == CategoryStore.getAllCategoryExpenseForEdit().size - 1) {
                val action =
                    InputFragmentDirections
                        .actionInputFragmentToCategoryFragment("", 0, "")
                view.findNavController().navigate(action)
                return@CategoryInputAdapter
            }

            if (selectedItem == position) {
                selectedItem = null
                binding.submitInputButton.setDisabled()
            } else {
                selectedItem = position
                binding.submitInputButton.setEnabled()
            }
            adapter.selectedItem = selectedItem
            adapter.notifyDataSetChanged()
        }

        binding.submitInputButton.setOnClickListener {
            if (binding.valueEditText.text?.isBlank() == true) return@setOnClickListener
            val selectedCategoryItem = if (selectedAdapter == 0)
                CategoryStore.getAllCategoryExpense()[adapter.selectedItem!!]
            else
                CategoryStore.getAllCategoryIncome()[adapter.selectedItem!!]

            val valueEditText = binding.valueEditText.text.toString().toDouble()

            val value = if (selectedAdapter == 0)
                -valueEditText
            else
                valueEditText

            TransactionStore.insertNewTransaction(
                TransactionItem(
                    Calendar.getInstance().time,
                    value,
                    selectedCategoryItem.name,
                    binding.noteEditText.text.toString(),
                    selectedCategoryItem.imgSrc
                )
            )
            binding.valueEditText.setText("")
            binding.noteEditText.setText("")
            selectedItem = null
            adapter.selectedItem = null
            adapter.notifyDataSetChanged()
            binding.submitInputButton.setDisabled()

            binding.foto1.visibility = View.GONE
            binding.foto2.visibility = View.GONE
            binding.foto3.visibility = View.GONE

            binding.delFoto1.visibility = View.GONE
            binding.delFoto2.visibility = View.GONE
            binding.delFoto3.visibility = View.GONE

        }

        binding.dataRangeLayout.editTextDate.text =
            SimpleDateFormat("MMM dd, yyyy (EEE)").format(viewModel.date.time)

        binding.dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
            viewModel.date.add(Calendar.DAY_OF_YEAR, -1)
            binding.dataRangeLayout.editTextDate.text =
                SimpleDateFormat("MMM dd, yyyy (EEE)").format(viewModel.date.time)
        }

        binding.dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
            viewModel.date.add(Calendar.DAY_OF_YEAR, 1)
            binding.dataRangeLayout.editTextDate.text =
                SimpleDateFormat("MMM dd, yyyy (EEE)").format(viewModel.date.time)
        }

        binding.submitInputButton.setDisabled()
        adapter.submitList(CategoryStore.getAllCategoryExpenseForInput())
        binding.categoryInputRecyclerView.adapter = adapter

    }

    private fun onTabSelectedListener() = object : TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab != null) {
                when (tab.text) {
                    getString(R.string.expense) -> {
                        selectedAdapter = 0
                        adapter.submitList(CategoryStore.getAllCategoryExpenseForInput())
                    }
                    getString(R.string.income) -> {
                        selectedAdapter = 1
                        adapter.submitList(CategoryStore.getAllCategoryIncomeForInput())
                    }
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }
}

