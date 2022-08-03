package com.fiz.mono.category_add.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.category_add.databinding.FragmentCategoryAddBinding
import com.fiz.mono.category_add.ui.adapters.CategoryIconsAdapter
import com.fiz.mono.domain.models.TypeTransaction
import com.fiz.mono.navigation.navigationData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryAddFragment : Fragment() {

    private val viewModel: CategoryAddViewModel by viewModels()

    private val adapter: CategoryIconsAdapter by lazy {
        CategoryIconsAdapter { position ->
            viewModel.onEvent(CategoryAddEvent.CategoryClicked(position))
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
        setupUI()
        setupListeners()
        observeViewStateUpdates()
        observeViewEffects()
    }

    private fun init() {
        val type = navigationData as? TypeTransaction ?: TypeTransaction.Expense
        viewModel.onEvent(CategoryAddEvent.ActivityLoaded(type))
    }

    private fun setupUI() {
        binding.apply {
            categoryIconRecyclerView.adapter = adapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            navigationBarLayout.setOnClickListenerBackButton {
                viewModel.onEvent(CategoryAddEvent.BackButtonClicked)
            }

            navigationBarLayout.setOnClickListenerActionButton {
                viewModel.onEvent(CategoryAddEvent.AddButtonClicked)
            }

            categoryNameEditText.doAfterTextChanged {
                viewModel.onEvent(CategoryAddEvent.CategoryNameChanged(it.toString()))
            }
        }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: CategoryAddViewState) {
        binding.navigationBarLayout.setVisibilityActionButton(newState.isVisibilityAddButton)
        adapter.submitList(newState.allCategoryIcons)
    }

    private fun observeViewEffects() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEffects.collect { viewEffect ->
                reactTo(viewEffect)
            }
        }
    }

    private fun reactTo(viewEffect: CategoryAddViewEffect) {
        when (viewEffect) {
            CategoryAddViewEffect.MoveReturn -> {
                findNavController().popBackStack()
            }
        }
    }

}