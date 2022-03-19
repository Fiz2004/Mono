package com.fiz.mono.ui.input

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.util.ActivityContract
import com.fiz.mono.util.CategoriesAdapter
import com.fiz.mono.util.setDisabled
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.io.IOException

class InputFragment : Fragment() {
    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CategoryInputViewModel by viewModels()

    private lateinit var adapter: CategoriesAdapter

    private var cashCheckCameraHardware: Boolean? = null

    private val cameraActivityLauncher = registerForActivityResult(ActivityContract()) { result ->
        result?.extras?.get("data")?.let {
            val intentData = result.extras?.get("data")
            intentData?.let {
                val imageBitmap = intentData as Bitmap
                binding.photo1ImageView.setImageBitmap(imageBitmap)
            }
        }
        if (binding.photo1ImageView.drawable == null)
            viewModel.setPic(binding.photo1ImageView.width, binding.photo1ImageView.height).also {
                binding.photo1ImageView.setImageBitmap(it)
                return@registerForActivityResult
            }
        if (binding.photo2ImageView.drawable == null)
            viewModel.setPic(binding.photo2ImageView.width, binding.photo2ImageView.height).also {
                binding.photo2ImageView.setImageBitmap(it)
                return@registerForActivityResult
            }
        if (binding.photo3ImageView.drawable == null)
            viewModel.setPic(binding.photo3ImageView.width, binding.photo3ImageView.height).also {
                binding.photo3ImageView.setImageBitmap(it)
                return@registerForActivityResult
            }
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

    private fun checkCameraHardware(context: Context): Boolean {
        if (cashCheckCameraHardware == null)
            cashCheckCameraHardware = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
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
        if (binding.valueEditText.text?.isBlank() == true) return

        val value = binding.valueEditText.text.toString().toDouble()
        val note = binding.noteEditText.text.toString()
        viewModel.clickSubmit(
            value,
            note
        )
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
                val action =
                    InputFragmentDirections
                        .actionInputFragmentToCategoryFragment("", 0, "")
                view?.findNavController()?.navigate(action)
                return true
            }
        } else {
            if (viewModel.isClickEditPositionIncome(position)) {
                viewModel.cleanSelected()
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
        when (number) {
            1 -> {
                binding.photo1Card.visibility = View.GONE
                binding.photo2Card.visibility = View.GONE
                binding.photo3Card.visibility = View.GONE

                binding.deletePhoto1ImageView.visibility = View.GONE
                binding.deletePhoto2ImageView.visibility = View.GONE
                binding.deletePhoto3ImageView.visibility = View.GONE
            }

            else -> {}
        }
    }

    private fun noteCameraOnClickListener(view: View) {
        binding.photo1Card.visibility = View.VISIBLE
        binding.photo2Card.visibility = View.VISIBLE
        binding.photo3Card.visibility = View.VISIBLE

        binding.photo1ImageView.visibility = View.VISIBLE
        binding.photo2ImageView.visibility = View.VISIBLE
        binding.photo3ImageView.visibility = View.VISIBLE

        binding.deletePhoto1ImageView.visibility = View.VISIBLE
        binding.deletePhoto2ImageView.visibility = View.VISIBLE
        binding.deletePhoto3ImageView.visibility = View.VISIBLE

        dispatchTakePictureIntent()

        updateUI()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    viewModel.createImageFile(requireContext())
                } catch (ex: IOException) {
                    Toast.makeText(requireContext(), "I can't create a file", Toast.LENGTH_LONG).show()
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.fiz.mono.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraActivityLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private fun updateUI() {
        binding.currencyTextView.text = mainViewModel.currency
        binding.dataRangeLayout.editTextDate.text = mainViewModel.getFormatDate()
        binding.ExpenseIncomeTextView.text = viewModel.getTypeFromSelectedAdapter(requireContext())

        viewModel.setStateSubmitInputButton(binding.submitButton)

        binding.noteCameraEditText.isEnabled = checkCameraHardware(requireActivity())

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
            viewModel.state = ""
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

