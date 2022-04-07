package com.fiz.mono.ui.category_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentCategoryAddBinding
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setVisible
import kotlinx.coroutines.launch

class CategoryAddFragment : Fragment() {
    private var _binding: FragmentCategoryAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryAddViewModel by viewModels {
        CategoryAddViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).categoryIconStore,
        )
    }

    private lateinit var adapter: CategoryIconsAdapter

    private val args: CategoryAddFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        bind()
        bindListener()
        subscribe()
    }

    private fun init() {
        viewModel.init(args.type)

        adapter = CategoryIconsAdapter { position ->
            viewModel.clickRecyclerView(position)
        }
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
                viewModel.clickBackButton()
            }

            navigationBarLayout.actionButton.setOnClickListener {
                viewModel.clickAddButton()
            }

            categoryNameEditText.doOnTextChanged { text, start, before, count ->
                viewModel.setCategoryName(text)
            }
        }
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    adapter.submitList(uiState.allCategoryIcons)
                    binding.navigationBarLayout.actionButton.setVisible(viewModel.getVisibilityAddButton())

                    if (uiState.isReturn)
                        findNavController().popBackStack()

                }
            }
        }
    }
}