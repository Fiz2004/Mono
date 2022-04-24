package com.fiz.mono.feature_category_add.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.core.util.getColorCompat
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.databinding.FragmentCategoryAddBinding
import com.fiz.mono.feature_category_add.ui.adapters.CategoryIconsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryAddFragment : Fragment() {

    private val viewModel: CategoryAddViewModel by viewModels()

    private val args: CategoryAddFragmentArgs by navArgs()

    private val adapter: CategoryIconsAdapter by lazy {
        CategoryIconsAdapter { position ->
            viewModel.onEvent(FeatureAddUIEvent.ClickCategory(position))
        }
    }

    private lateinit var binding: FragmentCategoryAddBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        bind()
        bindListener()
        subscribe()
    }

    private fun init() {
        viewModel.onEvent(FeatureAddUIEvent.Loading(args.type))
    }

    private fun bind() {
        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.actionButton.text = getString(R.string.add)
            navigationBarLayout.actionButton.setTextColor(requireContext().getColorCompat(R.color.blue))
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.add_category)
            categoryIconRecyclerView.adapter = adapter
        }
    }

    private fun bindListener() {
        binding.apply {
            navigationBarLayout.backButton.setOnClickListener {
                viewModel.onEvent(FeatureAddUIEvent.ClickBackButton)
            }

            navigationBarLayout.actionButton.setOnClickListener {
                viewModel.onEvent(FeatureAddUIEvent.ClickAddButton)
            }

            categoryNameEditText.doAfterTextChanged {
                viewModel.onEvent(FeatureAddUIEvent.ChangeCategoryName(it.toString()))
            }
        }
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.navigationBarLayout.actionButton.setVisible(uiState.isVisibilityAddButton)

                    adapter.submitList(uiState.allCategoryIcons)

                    if (uiState.isReturn) {
                        findNavController().popBackStack()
                        viewModel.onEvent(FeatureAddUIEvent.OnClickBackButton)
                    }

                }
            }
        }
    }
}