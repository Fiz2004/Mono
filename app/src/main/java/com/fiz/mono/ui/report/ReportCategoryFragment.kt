package com.fiz.mono.ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fiz.mono.R
import com.fiz.mono.databinding.FragmentReportCategoryBinding
import com.fiz.mono.databinding.FragmentReportMonthlyBinding

class ReportCategoryFragment : Fragment() {
    private var _binding: FragmentReportCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}