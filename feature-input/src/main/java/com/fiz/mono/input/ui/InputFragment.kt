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
import com.fiz.mono.base.android.adapters.CategoriesAdapter
import com.fiz.mono.base.android.utils.ActivityContract
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.input.R
import com.fiz.mono.input.databinding.FragmentInputBinding
import com.fiz.mono.navigation.navigate
import com.fiz.mono.navigation.navigationData
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

    private val cameraActivityLauncher = registerForActivityResult(ActivityContract()) {
        if (it == AppCompatActivity.RESULT_OK)
            viewModel.onEvent(InputEvent.AddPhotoPathButtonClicked)
    }

    private val adapter: CategoriesAdapter by lazy {
        CategoriesAdapter(com.fiz.mono.common.ui.resources.R.color.blue) { position ->
            viewModel.onEvent(InputEvent.CategoryItemCardClicked(position))
        }
    }

    private val deletePhotoImagesView: List<ImageView> by lazy {
        listOf(
            binding.deletePhoto1ImageView,
            binding.deletePhoto2ImageView,
            binding.deletePhoto3ImageView
        )
    }

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setupUI()
        setupListeners()
        observeViewStateUpdates()
        observeViewEffects()
    }

    private fun init() {
        val transaction = navigationData as? Int
        transaction?.let {
            viewModel.onEvent(InputEvent.Init(it))
        }
    }

    private fun setupUI() {
        binding.apply {
            categoryRecyclerView.adapter = adapter
        }
    }

    private fun setupListeners() {
        binding.apply {

            noteCameraEditText.setOnClickListener {
                dispatchTakePictureIntent(requireContext())?.let {
                    cameraActivityLauncher.launch(it)
                }
            }

            deletePhotoImagesView.forEachIndexed { index, imageView ->
                imageView.setOnClickListener {
                    viewModel.onEvent(InputEvent.RemovePhotoButtonClicked(index + 1))
                }
            }

            submitButton.setOnClickListener {
                viewModel.onEvent(InputEvent.SubmitButtonClicked)
            }

            backButton.setOnClickListener {
                viewModel.onEvent(InputEvent.BackButtonClicked)
            }

            removeButton.setOnClickListener {
                viewModel.onEvent(InputEvent.RemoveTransactionButtonClicked)
            }

            dataRangeLayout.dateTextView.setOnClickListener {
                viewModel.onEvent(InputEvent.DataTextClicked)
            }

            valueEditText.doAfterTextChanged {
                viewModel.onEvent(InputEvent.ValueTransactionChanged(it.toString()))
            }

            noteEditText.doAfterTextChanged {
                viewModel.onEvent(InputEvent.NoteTransactionChanged(it.toString()))
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val typeTransaction = when (tab?.position) {
                        0 -> TypeTransaction.Expense
                        else -> TypeTransaction.Income
                    }
                    viewModel.onEvent(InputEvent.TypeTransactionChanged(typeTransaction))
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            dataRangeLayout.leftDateRangeImageButton.setOnClickListener {
                viewModel.onEvent(InputEvent.LeftDataIconClicked)
            }

            dataRangeLayout.rightDateRangeImageButton.setOnClickListener {
                viewModel.onEvent(InputEvent.RightDataIconClicked)
            }
        }
    }

    private fun dispatchTakePictureIntent(context: Context): Intent? {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            val photoFile: File? = try {
                createImageFile(context)
            } catch (ex: IOException) {
                Toast.makeText(
                    context,
                    getString(com.fiz.mono.common.ui.resources.R.string.cant_create_file),
                    Toast.LENGTH_LONG
                )
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
            viewModel.onEvent(InputEvent.UpdateCurrentPhotoPath(absolutePath))
        }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: InputViewState) {
        binding.apply {
            ExpenseIncomeTextView.text =
                getString(newState.getTextExpenseIncomeTextView)

            submitButton.isEnabled = newState.isSubmitButtonEnabled()

            if (noteEditText.text.toString() != newState.note)
                noteEditText.setText(newState.note)

            if (valueEditText.text.toString() != newState.value)
                valueEditText.setText(newState.value)

            submitButton.text = getString(newState.getTextSubmitButton)

            dataRangeLayout.root.setVisible(newState.isInput)
            tabLayout.setVisible(newState.isInput)
            titleTextView.setVisible(newState.isEdit)
            backButton.setVisible(newState.isEdit)
            removeButton.setVisible(newState.isEdit)

            val expenseIncomeTextViewLayoutParams =
                ExpenseIncomeTextView.layoutParams as ConstraintLayout.LayoutParams
            expenseIncomeTextViewLayoutParams.topToBottom =
                if (newState.isInput) R.id.dataRangeLayout else R.id.titleTextView

            if (newState.isInput) {
                val numberTab =
                    if (newState.selectedAdapter == TypeTransaction.Expense) 0 else 1
                tabLayout.getTabAt(numberTab)?.select()
            }

            noteCameraEditText.isEnabled = isCanAddPhoto()

            val countPhoto = newState.photoPaths.size

            photo1Card.setVisible(countPhoto > 0)
            photo2Card.setVisible(countPhoto > 0)
            photo3Card.setVisible(countPhoto > 0)

            photo1ImageView.setVisible(countPhoto > 0)
            photo2ImageView.setVisible(countPhoto > 1)
            photo3ImageView.setVisible(countPhoto > 2)

            deletePhoto1ImageView.setVisible(countPhoto > 0)
            deletePhoto2ImageView.setVisible(countPhoto > 1)
            deletePhoto3ImageView.setVisible(countPhoto > 2)

            photo1ImageView.setImageBitmap(newState.photoBitmap.getOrNull(0))
            photo2ImageView.setImageBitmap(newState.photoBitmap.getOrNull(1))
            photo3ImageView.setImageBitmap(newState.photoBitmap.getOrNull(2))

            adapter.submitList(newState.currentCategory)

            dataRangeLayout.dateTextView.text = newState.date

            titleTextView.text = newState.date

            currencyTextView.text = newState.currency
        }
    }

    private fun observeViewEffects() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEffects.collect { navigationAction ->

                when (navigationAction) {
                    InputViewEffect.MoveCalendar -> {
                        navigate(
                            R.id.action_inputFragment_to_calendarFragment,
                            com.fiz.mono.navigation.R.id.nav_host_fragment
                        )
                    }

                    InputViewEffect.MoveEdit -> {
                        navigate(R.id.action_inputFragment_to_categoryFragment)
                    }

                    InputViewEffect.MoveOnBoarding -> {
                        navigate(R.id.action_inputFragment_to_onBoardingFragment)
                    }

                    InputViewEffect.MovePinPassword -> {
                        navigate(
                            actionId =
                            R.id.action_inputFragment_to_PINPasswordFragment,
                            data = "start"
                        )
                    }

                    InputViewEffect.MoveReturn -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun isCanAddPhoto(): Boolean {
        return viewModel.checkCameraHardware(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.categoryRecyclerView.adapter = null
        _binding = null
    }

    companion object {
        const val EXPENSE = 0
        const val INCOME = 1

        const val MAX_PHOTO = 3
    }
}



