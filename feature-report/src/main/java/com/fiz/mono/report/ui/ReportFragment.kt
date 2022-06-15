package com.fiz.mono.report.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.feature.report.R
import com.fiz.mono.feature.report.databinding.FragmentReportBinding
import com.fiz.mono.navigation.navigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportFragment : Fragment() {

    private val viewModel: ReportViewModel by viewModels(
        ownerProducer = { this }
    )

    private val navController: NavController by lazy {
        val navHostFragment: NavHostFragment =
            (childFragmentManager.findFragmentById(com.fiz.mono.navigation.R.id.report_host_fragment) as NavHostFragment)
        navHostFragment.navController
    }

    private val binding get() = _binding!!
    private var _binding: FragmentReportBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        observeViewStateUpdates()
        observeViewEffects()
    }

    private fun setupUI() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navigationBarLayout.apply {
                backButton.setVisible(destination.id == R.id.reportCategoryFragment)
                choiceImageButton.setVisible(destination.id != R.id.reportCategoryFragment)
            }
        }

        binding.navigationBarLayout.apply {
            actionButton.setVisible(false)
        }
    }

    private fun setupListeners() {
        binding.apply {
            navigationBarLayout.apply {

                backButton.setOnClickListener {
                    navController.popBackStack()
                }

                choiceImageButton.setOnClickListener {
                    val reportDialog = ReportDialog()
                    reportDialog.show(childFragmentManager, "Choice Report")
                }
            }
        }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collect { newState ->
                updateScreenState()
            }
        }
    }

    private fun updateScreenState() {
        binding.navigationBarLayout.titleTextView.text =
            getString(viewModel.getTextTypeReport())
    }

    private fun observeViewEffects() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewEffects.collect { viewEffect ->
                reactTo(viewEffect)
            }
        }
    }

    private fun reactTo(viewEffect: ReportViewEffect) {
        when (viewEffect) {
            ReportViewEffect.MoveSelectCategory -> {
                navigate(
                    R.id.action_reportMonthlyFragment_to_selectCategoryFragment,
                    com.fiz.mono.navigation.R.id.report_host_fragment
                )
            }
            ReportViewEffect.MoveReturn -> {
                navController.popBackStack()
            }
            ReportViewEffect.MoveCalendar -> {
                navigate(R.id.action_reportFragment_to_calendarFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}