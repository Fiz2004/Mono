package com.fiz.mono.report.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.feature_report.ui.monthly.ReportMonthlyFragmentDirections
import com.fiz.mono.report.databinding.FragmentReportBinding
import com.fiz.mono.util.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportFragment : Fragment(), ReportDialog.Choicer {

    private val viewModel: ReportViewModel by viewModels()

    private val navController: NavController by lazy {
        val navHostFragment: NavHostFragment =
            (childFragmentManager.findFragmentById(com.fiz.mono.report.R.id.report_host_fragment) as NavHostFragment)
        navHostFragment.navController
    }

    private lateinit var binding: FragmentReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
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
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navigationBarLayout.backButton.setVisible(destination.id == com.fiz.mono.common.ui.resources.R.id.reportCategoryFragment)
            binding.navigationBarLayout.choiceImageButton.setVisible(destination.id != com.fiz.mono.common.ui.resources.R.id.reportCategoryFragment)
        }
    }

    private fun bind() {
        binding.apply {
            navigationBarLayout.actionButton.setVisible(false)
        }
    }

    private fun bindListener() {
        binding.apply {
            navigationBarLayout.apply {
                backButton.setOnClickListener {
                    navController.popBackStack()
                }

                choiceImageButton.setOnClickListener {
                    val reportDialog = ReportDialog()
                    reportDialog.choicer = this@ReportFragment

                    val args = Bundle()
                    val currentChoice = viewModel.uiState.value.categorySelectedReport
                    args.putInt("currentChoice", currentChoice)
                    reportDialog.arguments = args

                    reportDialog.show(childFragmentManager, "Choice Report")
                }
            }
        }
    }

    private fun subscribe() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect {
                binding.navigationBarLayout.titleTextView.text =
                    getString(viewModel.getTextTypeReport())
            }
        }
    }

    override fun choiceCategory() {
        if (viewModel.clickCategory()) {
            val action =
                ReportMonthlyFragmentDirections
                    .actionReportMonthlyFragmentToSelectCategoryFragment()
            navController.navigate(action)
        }
    }

    override fun choiceMonthly() {
        if (viewModel.clickMonthly())
            navController.popBackStack()
    }

    fun clickData() {
        val action =
            ReportFragmentDirections
                .actionToCalendarFragment()
        findNavController().navigate(action)
    }
}