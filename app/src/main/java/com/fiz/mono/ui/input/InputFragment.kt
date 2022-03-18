package com.fiz.mono.ui.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.ui.MainActivity
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.util.CategoriesAdapter
import com.fiz.mono.util.setDisabled
import com.google.android.material.tabs.TabLayout
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio

class InputFragment : Fragment() {
    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CategoryInputViewModel by viewModels()

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

        if (mainViewModel.firstTime) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToOnBoardingFragment()
            view.findNavController().navigate(action)
            return
        }
        if (!mainViewModel.log) {
            val action =
                InputFragmentDirections
                    .actionInputFragmentToPINPasswordFragment(PINPasswordFragment.START)
            view.findNavController().navigate(action)
            return
        }

        binding.noteCameraEditText.setOnClickListener(::noteCameraOnClickListener)
        binding.deletePhoto1ImageView.setOnClickListener { deletePhotoOnClickListener(1) }
        binding.deletePhoto2ImageView.setOnClickListener { deletePhotoOnClickListener(2) }
        binding.deletePhoto3ImageView.setOnClickListener { deletePhotoOnClickListener(3) }
        binding.submitButton.setOnClickListener(::submitButtonOnClickListener)
        binding.dataRangeLayout.leftDateRangeImageButton.setOnClickListener(::leftDateRangeOnClickListener)
        binding.dataRangeLayout.rightDateRangeImageButton.setOnClickListener(::rightDateRangeOnClickListener)

        adapter = CategoriesAdapter(R.color.blue, ::adapterOnClickListener)
        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener())
        val numberTab = if (viewModel.getSelectedAdapter() == EXPENSE) 0 else 1
        binding.tabLayout.getTabAt(numberTab)?.select()
        binding.categoryRecyclerView.adapter = adapter

        val allCategory = viewModel.getAllCategoryFromSelected()
        adapter.submitList(allCategory)

        updateUI()
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
        if (binding.valueEditText.text?.isBlank() == true) return

        val value = binding.valueEditText.text.toString().toDouble()
        val note = binding.noteEditText.text.toString()
        viewModel.clickSubmit(
            value,
            note
        )
        updateUI()
    }

    private fun adapterOnClickListener(position: Int) {
        checkClickEditPosition(position)
        viewModel.addSelectItem(position)
        adapter.submitList(viewModel.getListForSubmitAdapter())
        updateUI()
    }

    private fun checkClickEditPosition(position: Int) {
        if (viewModel.getSelectedAdapter() == EXPENSE) {
            if (viewModel.isClickEditPositionExpense(position)) {
                viewModel.cleanSelected()
                val action =
                    InputFragmentDirections
                        .actionInputFragmentToCategoryFragment("", 0, "")
                view?.findNavController()?.navigate(action)
                return
            }
        } else {
            if (viewModel.isClickEditPositionIncome(position)) {
                viewModel.cleanSelected()
                val action =
                    InputFragmentDirections
                        .actionInputFragmentToCategoryFragment("", 0, "")
                view?.findNavController()?.navigate(action)
                return
            }
        }
    }

    private fun deletePhotoOnClickListener(number: Int) {
        when (number) {
            1 -> {
                binding.photo1Card.visibility = View.GONE
                binding.photo2Card.visibility = View.GONE
                binding.photo3Card.visibility = View.GONE

                binding.deletePhoto1ImageView.visibility = View.GONE
                binding.deletePhoto2ImageView.visibility = View.GONE
                binding.deletePhoto3ImageView.visibility = View.GONE
            }

            else -> updateImage()
        }
    }

    private fun noteCameraOnClickListener(view: View) {
        val options = Options().apply {
            ratio = Ratio.RATIO_AUTO                                    //Image/video capture ratio
            count =
                3                                                   //Number of images to restrict selection count
            spanCount = 4                                               //Number for columns in grid
            path =
                "Pix/Camera"                                         //Custom Path For media Storage
            isFrontFacing =
                true                                       //Front Facing camera on start
            mode =
                Mode.Picture                                             //Option to select only pictures or videos or both
            flash =
                Flash.Auto                                          //Option to select flash type
        }

        (requireActivity() as MainActivity).addPixToActivity(R.id.photo1Card, options) {
            when (it.status) {
                PixEventCallback.Status.SUCCESS -> {
                    viewModel.setData(it.data)

                }//use results as it.data
                PixEventCallback.Status.BACK_PRESSED -> {

                } // back pressed called
            }
        }

        viewModel.state = "load"
        updateUI()
    }

    private fun updateImage() {
        for (d in viewModel.loadData)
            binding.photo1ImageView.setImageURI(d)
    }

    private fun updateUI() {
        binding.currencyTextView.text = mainViewModel.currency
        binding.dataRangeLayout.editTextDate.text = mainViewModel.getFormatDate()
        binding.ExpenseIncomeTextView.text = viewModel.getTypeFromSelectedAdapter(requireContext())

        viewModel.setStateSubmitInputButton(binding.submitButton)

        if (viewModel.state == "Только что отправили") {
            binding.valueEditText.setText("")
            binding.noteEditText.setText("")
            binding.submitButton.setDisabled()

            binding.photo1Card.visibility = View.GONE
            binding.photo2Card.visibility = View.GONE
            binding.photo3Card.visibility = View.GONE

            binding.deletePhoto1ImageView.visibility = View.GONE
            binding.deletePhoto2ImageView.visibility = View.GONE
            binding.deletePhoto3ImageView.visibility = View.GONE
        }

        if (viewModel.state == "load") {
            binding.photo1Card.visibility = View.VISIBLE
            binding.photo2Card.visibility = View.VISIBLE
            binding.photo3Card.visibility = View.VISIBLE

            binding.deletePhoto1ImageView.visibility = View.VISIBLE
            binding.deletePhoto2ImageView.visibility = View.VISIBLE
            binding.deletePhoto3ImageView.visibility = View.VISIBLE
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

