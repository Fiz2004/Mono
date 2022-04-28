package com.fiz.mono.input.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.base.android.adapters.CategoriesAdapter
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.ActivityContract
import com.fiz.mono.core.util.launchAndRepeatWithViewLifecycle
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.feature.input.databinding.FragmentInputBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class InputFragment : Fragment() {

    private val viewModel: InputViewModel by viewModels()

    private val args: InputFragmentArgs by navArgs()

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)")

    private val cameraActivityLauncher = registerForActivityResult(ActivityContract()) {
        if (it == AppCompatActivity.RESULT_OK)
            viewModel.onEvent(InputUiEvent.AddPhotoPath)
    }

    private val adapter: CategoriesAdapter by lazy {
        CategoriesAdapter(R.color.blue) { position ->
            viewModel.onEvent(InputUiEvent.ClickCategory(position))
        }
    }

    private val deletePhotoImagesView: List<ImageView> by lazy {
        listOf(
            binding.deletePhoto1ImageView,
            binding.deletePhoto2ImageView,
            binding.deletePhoto3ImageView
        )
    }

    private lateinit var binding: FragmentInputBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        bind()
        bindListener()
        subscribe()
        setupNavigation()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onEvent(InputUiEvent.Start)
    }

    private fun init() {
        viewModel.onEvent(InputUiEvent.Init(args.transaction))
    }

    private fun bind() {
        binding.apply {
            categoryRecyclerView.adapter = adapter
        }
    }

    private fun bindListener() {
        binding.apply {

            noteCameraEditText.setOnClickListener {
                dispatchTakePictureIntent(requireContext())?.let {
                    cameraActivityLauncher.launch(it)
                }
            }

            deletePhotoImagesView.forEachIndexed { index, imageView ->
                imageView.setOnClickListener {
                    viewModel.onEvent(InputUiEvent.ClickRemovePhoto(index + 1))
                }
            }

            submitButton.setOnClickListener {
                viewModel.onEvent(InputUiEvent.ClickSubmit)
            }

            backButton.setOnClickListener {
                viewModel.onEvent(InputUiEvent.ClickBackButton)
            }

            removeButton.setOnClickListener {
                viewModel.onEvent(InputUiEvent.ClickRemoveTransaction)
            }

            dataRangeLayout.dateTextView.setOnClickListener {
                viewModel.onEvent(InputUiEvent.ClickData)
            }

            valueEditText.doAfterTextChanged {
                viewModel.onEvent(InputUiEvent.ValueChange(it.toString()))
            }

            noteEditText.doAfterTextChanged {
                viewModel.onEvent(InputUiEvent.NoteChange(it.toString()))
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.onEvent(InputUiEvent.ChangeTypeTransactions(tab?.position))
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
                viewModel.onEvent(InputUiEvent.ClickLeftData)
            }

            dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
                viewModel.onEvent(InputUiEvent.ClickRightData)
            }
        }
    }

    private fun dispatchTakePictureIntent(context: Context): Intent? {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            val photoFile: File? = try {
                createImageFile(context)
            } catch (ex: IOException) {
                Toast.makeText(context, getString(R.string.cant_create_file), Toast.LENGTH_LONG)
                    .show()
                null
            }

            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    context,
                    "com.fiz.mono.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                return takePictureIntent
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File? {
        val timeStamp: String =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneOffset.UTC)
                .format(Instant.now())
        val storageDir: File =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return null
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            viewModel.onEvent(InputUiEvent.UpdateCurrentPhotoPath(absolutePath))
        }
    }

    private fun subscribe() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { uiState ->

                binding.apply {
                    circularProgressIndicator.setVisible(uiState.isLoading)

                    ExpenseIncomeTextView.text =
                        getString(uiState.getTextExpenseIncomeTextView)

                    submitButton.isEnabled = uiState.isSubmitButtonEnabled()

                    if (noteEditText.text.toString() != uiState.note)
                        noteEditText.setText(uiState.note)

                    if (valueEditText.text.toString() != uiState.value)
                        valueEditText.setText(uiState.value)

                    submitButton.text = getString(uiState.getTextSubmitButton)

                    dataRangeLayout.root.setVisible(uiState.isInput)
                    tabLayout.setVisible(uiState.isInput)
                    titleTextView.setVisible(uiState.isEdit)
                    backButton.setVisible(uiState.isEdit)
                    removeButton.setVisible(uiState.isEdit)

                    val expenseIncomeTextViewLayoutParams =
                        ExpenseIncomeTextView.layoutParams as ConstraintLayout.LayoutParams
                    expenseIncomeTextViewLayoutParams.topToBottom =
                        if (uiState.isInput) com.fiz.mono.feature.input.R.id.dataRangeLayout else com.fiz.mono.feature.input.R.id.titleTextView

                    if (uiState.isInput) {
                        val numberTab = if (uiState.selectedAdapter == EXPENSE) 0 else 1
                        tabLayout.getTabAt(numberTab)?.select()
                    }

                    val countPhoto = uiState.photoPaths.size

                    noteCameraEditText.isEnabled = isCanAddPhoto()

                    photo1Card.setVisible(countPhoto > 0)
                    photo2Card.setVisible(countPhoto > 0)
                    photo3Card.setVisible(countPhoto > 0)

                    photo1ImageView.setVisible(countPhoto > 0)
                    photo2ImageView.setVisible(countPhoto > 1)
                    photo3ImageView.setVisible(countPhoto > 2)

                    deletePhoto1ImageView.setVisible(countPhoto > 0)
                    deletePhoto2ImageView.setVisible(countPhoto > 1)
                    deletePhoto3ImageView.setVisible(countPhoto > 2)

                    photo1ImageView.setImageBitmap(uiState.photoBitmap.getOrNull(0))
                    photo2ImageView.setImageBitmap(uiState.photoBitmap.getOrNull(1))
                    photo3ImageView.setImageBitmap(uiState.photoBitmap.getOrNull(2))

                    adapter.submitList(uiState.currentCategory)

                    dataRangeLayout.dateTextView.text = dateFormatter.format(uiState.date)

                    titleTextView.text = dateFormatter.format(uiState.date)

                    currencyTextView.text = uiState.currency
                }
            }
        }
    }

    private fun setupNavigation() {
        launchAndRepeatWithViewLifecycle {
            viewModel.navigationUiState.collect { navigationUiState ->
                if (navigationUiState.isMoveOnBoarding) {
                    val action =
                        InputFragmentDirections
                            .actionInputFragmentToOnBoardingFragment()
                    findNavController().navigate(action)
                    viewModel.onEvent(InputUiEvent.MovedOnBoarding)
                }

                if (navigationUiState.isMovePinPassword) {
                    val action =
                        InputFragmentDirections
                            .actionToPINPasswordFragment("start")
                    findNavController().navigate(action)
                    viewModel.onEvent(InputUiEvent.MovedPinPassword)

                }

                if (navigationUiState.isMoveReturn) {
                    findNavController().popBackStack()
                    viewModel.onEvent(InputUiEvent.Returned)
                }

                if (navigationUiState.isMoveEdit) {
                    val action =
                        InputFragmentDirections
                            .actionToCategoryFragment()
                    findNavController().navigate(action)
                    viewModel.onEvent(InputUiEvent.MovedEdit)
                }

                if (navigationUiState.isMoveCalendar) {
                    val action =
                        InputFragmentDirections
                            .actionToCalendarFragment()
                    findNavController().navigate(action)
                    viewModel.onEvent(InputUiEvent.MovedCalendar)
                }

            }
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



