package com.fiz.mono.ui.input

import android.net.Uri
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
import com.fiz.mono.ui.MainActivity
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.util.CategoriesAdapter
import com.fiz.mono.util.setDisabled
import com.fiz.mono.util.setEnabled
import com.google.android.material.tabs.TabLayout
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio
import java.text.SimpleDateFormat
import java.util.*

class InputFragment : Fragment() {
    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: CategoriesAdapter

    private var selectedAdapter: Int? = 0
    private var selectedItem: Int? = null

    var load = false
    lateinit var loadData: List<Uri>

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

            val options = Options().apply {
                ratio = Ratio.RATIO_AUTO                                    //Image/video capture ratio
                count =
                    3                                                   //Number of images to restrict selection count
                spanCount = 4                                               //Number for columns in grid
                path = "Pix/Camera"                                         //Custom Path For media Storage
                isFrontFacing = true                                       //Front Facing camera on start
                mode =
                    Mode.Picture                                             //Option to select only pictures or videos or both
                flash = Flash.Auto                                          //Option to select flash type
            }

            (requireActivity() as MainActivity).addPixToActivity(R.id.foto1, options) {
                when (it.status) {
                    PixEventCallback.Status.SUCCESS -> {
                        load = true
                        loadData = it.data

                    }//use results as it.data
                    PixEventCallback.Status.BACK_PRESSED -> {

                    } // back pressed called
                }
            }

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

        binding.delFoto2.setOnClickListener {
            updateImage()
        }



        adapter = CategoriesAdapter(R.color.blue) { position ->
            if (selectedAdapter == 0) {

                if (position == CategoryStore.getAllCategoryExpenseForEdit().size - 1) {
                    selectedItem = null
                    CategoryStore.getAllCategoryExpenseForEdit().forEach { it.selected = false }
                    val action =
                        InputFragmentDirections
                            .actionInputFragmentToCategoryFragment("", 0, "")
                    view.findNavController().navigate(action)
                    return@CategoriesAdapter
                }

                if (selectedItem == position) {
                    selectedItem = null
                    CategoryStore.getAllCategoryExpenseForInput()[position].selected = false
                    binding.submitInputButton.setDisabled()
                } else {
                    selectedItem?.let {
                        CategoryStore.getAllCategoryExpenseForInput()[selectedItem!!].selected =
                            false
                    }
                    CategoryStore.getAllCategoryExpenseForInput()[position].selected = true
                    selectedItem = position
                    binding.submitInputButton.setEnabled()
                }
                adapter.submitList(CategoryStore.getAllCategoryExpenseForInput().map { it.copy() })
            } else {

                if (position == CategoryStore.getAllCategoryIncomeForEdit().size - 1) {
                    selectedItem = null
                    CategoryStore.getAllCategoryIncomeForEdit().forEach { it.selected = false }
                    val action =
                        InputFragmentDirections
                            .actionInputFragmentToCategoryFragment("", 0, "")
                    view.findNavController().navigate(action)
                    return@CategoriesAdapter
                }

                if (selectedItem == position) {
                    selectedItem = null
                    CategoryStore.getAllCategoryIncomeForInput()[position].selected = false
                    binding.submitInputButton.setDisabled()
                } else {
                    selectedItem?.let {
                        CategoryStore.getAllCategoryIncomeForInput()[selectedItem!!].selected =
                            false
                    }
                    CategoryStore.getAllCategoryIncomeForInput()[position].selected = true
                    selectedItem = position
                    binding.submitInputButton.setEnabled()
                }
                adapter.submitList(CategoryStore.getAllCategoryIncomeForInput().map { it.copy() })
            }

        }

        binding.submitInputButton.setOnClickListener {
            if (binding.valueEditText.text?.isBlank() == true) return@setOnClickListener
            val selectedCategoryItem = if (selectedAdapter == 0)
                CategoryStore.getAllCategoryExpense()[selectedItem!!]
            else
                CategoryStore.getAllCategoryIncome()[selectedItem!!]

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
            CategoryStore.getAllCategoryExpenseForInput()[selectedItem!!].selected = false
            selectedItem = null
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
        adapter.submitList(CategoryStore.getAllCategoryExpenseForInput().map { it.copy() })
        binding.categoryInputRecyclerView.adapter = adapter

        updateUI()
    }

    private fun updateImage() {
        for (d in loadData)
            binding.foto1ImageView.setImageURI(d)
    }

    private fun updateUI() {
        when (selectedAdapter) {
            0 -> binding.ExpenseIncomeTextView.text = getString(R.string.expense)
            1 -> binding.ExpenseIncomeTextView.text = getString(R.string.income)
        }
    }

    private fun onTabSelectedListener() = object : TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab != null) {
                when (tab.text) {
                    getString(R.string.expense) -> {
                        selectedAdapter = 0
                        selectedItem = null
                        adapter.submitList(CategoryStore.getAllCategoryExpenseForInput())
                    }
                    getString(R.string.income) -> {
                        selectedAdapter = 1
                        selectedItem = null
                        adapter.submitList(CategoryStore.getAllCategoryIncomeForInput())
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
}

