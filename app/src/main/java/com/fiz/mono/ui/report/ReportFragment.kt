package com.fiz.mono.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentReportBinding
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setVisible
import com.fiz.mono.util.themeColor

class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val monthlyViewModel: ReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment: NavHostFragment =
            childFragmentManager.findFragmentById(R.id.report_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.apply {
            navigationBarLayout.backButton.setVisible(false)
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.choiceImageButton.setVisible(true)
            navigationBarLayout.titleTextView.text = getString(R.string.month_report)
            navigationBarLayout.choiceImageButton.setOnClickListener(::choiceReportOnClickListener)
            monthlyTextView.setOnClickListener(::monthlyOnClickListener)
            categoryTextView.setOnClickListener(::categoryOnClickListener)
        }
    }

    private fun categoryOnClickListener(view: View) {
        monthlyViewModel.categorySelectedReport = 1
        binding.navigationBarLayout.titleTextView.text = getString(R.string.category_report)
        val action =
            ReportMonthlyFragmentDirections
                .actionReportMonthlyFragmentToSelectCategoryFragment()
        navController.navigate(action)
        binding.choiceReportConstraintLayout.visibility = View.GONE
    }

    private fun monthlyOnClickListener(view: View) {
        monthlyViewModel.categorySelectedReport = 0
        binding.navigationBarLayout.titleTextView.text = getString(R.string.month_report)
        navController.popBackStack()
        binding.choiceReportConstraintLayout.visibility = View.GONE
    }

    private fun choiceReportOnClickListener(view: View) {
        binding.choiceReportConstraintLayout.visibility =
            if (binding.choiceReportConstraintLayout.visibility == View.GONE)
                View.VISIBLE
            else
                View.GONE

        updateMenu()
    }

    private fun updateMenu() {
        if (monthlyViewModel.categorySelectedReport == 0) {
            binding.monthlyTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
            binding.categoryTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))
        } else {
            binding.monthlyTextView.setTextColor(requireContext().themeColor(androidx.appcompat.R.attr.colorPrimary))
            binding.categoryTextView.setTextColor(requireContext().getColorCompat(R.color.blue))
        }
    }
}