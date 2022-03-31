package com.fiz.mono.ui.category_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fiz.mono.App
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentCategoryAddBinding
import com.fiz.mono.ui.MainViewModel
import com.fiz.mono.ui.MainViewModelFactory
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setVisible

class CategoryAddFragment : Fragment() {
    private var _binding: FragmentCategoryAddBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as App).categoryStore,
            (requireActivity().application as App).transactionStore
        )
    }

    private val viewModel: CategoryAddViewModel by viewModels()

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
    }

    private fun init() {
        adapter = CategoryIconsAdapter(::adapterOnClickListener)
        adapter.submitList(viewModel.getAllCategoryIcon())
    }

    private fun bind() {
        binding.apply {
            navigationBarLayout.backButton.setVisible(true)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.actionButton.text = getString(R.string.add)
            navigationBarLayout.actionButton.setTextColor(requireContext().getColorCompat(R.color.blue))
            navigationBarLayout.choiceImageButton.setVisible(false)
            navigationBarLayout.titleTextView.text = getString(R.string.add_category)

            navigationBarLayout.backButton.setOnClickListener(::backButtonOnClickListener)

            navigationBarLayout.actionButton.setOnClickListener(::addButtonOnClickListener)

            expenseRecyclerView.adapter = adapter
        }
    }

    private fun backButtonOnClickListener(v: View): Unit {
        findNavController().popBackStack()
    }

    private fun addButtonOnClickListener(view: View): Unit {
        if (viewModel.isSelected() && binding.categoryNameEditText.text?.isNotBlank() == true) {

            val name = binding.categoryNameEditText.text.toString()

            mainViewModel.addNewCategory(name, args.type, viewModel.getSelectedIcon())

            val action =
                CategoryAddFragmentDirections
                    .actionCategoryAddFragmentToCategoryFragment()
            view.findNavController().navigate(action)
            return
        }
    }

    private fun adapterOnClickListener(position: Int) {
        viewModel.addSelectItem(position)
        binding.navigationBarLayout.actionButton.visibility = viewModel.getVisibilityAddButton()
        adapter.submitList(viewModel.getAllCategoryIcon())
    }

}