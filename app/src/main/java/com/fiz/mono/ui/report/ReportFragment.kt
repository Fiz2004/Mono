package com.fiz.mono.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentReportBinding
import com.fiz.mono.ui.report.monthly.ReportMonthlyFragmentDirections
import com.fiz.mono.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

@AndroidEntryPoint
@WithFragmentBindings
class ReportFragment : Fragment(), ReportDialog.Choicer {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val reportViewModel: ReportViewModel by viewModels()

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

        init()
        bind()
        subscribe()
    }

    private fun init() {
        val navHostFragment: NavHostFragment =
            childFragmentManager.findFragmentById(R.id.report_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navigationBarLayout.backButton.setVisible(destination.id == R.id.reportCategoryFragment)
            binding.navigationBarLayout.choiceImageButton.setVisible(destination.id != R.id.reportCategoryFragment)
        }
    }

    private fun subscribe() {
        reportViewModel.categorySelectedReport.observe(viewLifecycleOwner) {
            binding.navigationBarLayout.titleTextView.text =
                if (it == MONTHLY)
                    getString(R.string.month_report)
                else
                    getString(R.string.category_report)
        }

    }

    private fun bind() {
        binding.apply {
            navigationBarLayout.actionButton.setVisible(false)
            navigationBarLayout.backButton.setOnClickListener(::backButtonOnClickListener)
            navigationBarLayout.choiceImageButton.setOnClickListener(::choiceReportOnClickListener)
        }
    }

    private fun backButtonOnClickListener(view: View) {
        navController.popBackStack()
    }

    private fun choiceReportOnClickListener(view: View) {
        val reportDialog = ReportDialog()
        reportDialog.choicer = this

        val args = Bundle()
        val currentChoice = reportViewModel.categorySelectedReport.value ?: 0
        args.putInt("currentChoice", currentChoice)
        reportDialog.arguments = args

        reportDialog.show(childFragmentManager, "Choice Report")
    }

    override fun choiceCategory() {
        if (reportViewModel.categorySelectedReport.value == CATEGORY) return
        reportViewModel.clickCategory()
        val action =
            ReportMonthlyFragmentDirections
                .actionReportMonthlyFragmentToSelectCategoryFragment()
        navController.navigate(action)
    }

    override fun choiceMonthly() {
        if (reportViewModel.categorySelectedReport.value == MONTHLY) return
        reportViewModel.clickMonthly()
        navController.popBackStack()
    }

    fun clickData() {
        val action =
            ReportFragmentDirections
                .actionToCalendarFragment()
        findNavController().navigate(action)
    }

    companion object {
        const val MONTHLY = 0
        const val CATEGORY = 1
    }
}