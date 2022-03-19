package com.fiz.mono.ui.input

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentInputBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.pin_password.PINPasswordFragment
import com.fiz.mono.util.CategoriesAdapter
import com.fiz.mono.util.setDisabled
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class InputFragment : Fragment() {
    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CategoryInputViewModel by viewModels()

    private lateinit var adapter: CategoriesAdapter

    lateinit var currentPhotoPath: String

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

        val pm = requireActivity().packageManager
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            binding.noteCameraEditText.isEnabled = false

        updateUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 102 && resultCode == RESULT_OK) {
            val intentData = data?.extras?.get("data")
            intentData?.let {
                val imageBitmap = intentData as Bitmap

                binding.photo1ImageView.setImageBitmap(imageBitmap)
            }
            setPic()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.fiz.mono.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 102)
                }
            }
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = binding.photo1ImageView.width
        val targetH: Int = binding.photo1ImageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)

        val photoW: Int = bmOptions.outWidth
        val photoH: Int = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            binding.photo1ImageView.setImageBitmap(bitmap)
        }
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

        viewModel.state = "load"
        updateUI()
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
            viewModel.state = ""
        }

        if (viewModel.state == "load") {
            binding.photo1Card.visibility = View.VISIBLE
            binding.photo2Card.visibility = View.VISIBLE
            binding.photo3Card.visibility = View.VISIBLE

            binding.deletePhoto1ImageView.visibility = View.VISIBLE
            binding.deletePhoto2ImageView.visibility = View.VISIBLE
            binding.deletePhoto3ImageView.visibility = View.VISIBLE
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

