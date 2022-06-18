package com.fiz.mono.currency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fiz.mono.base.android.utils.getColorCompat
import com.fiz.mono.base.android.utils.launchAndRepeatWithViewLifecycle
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.base.android.utils.themeColor
import com.fiz.mono.currency.databinding.FragmentCurrencyBinding
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.radiobutton.MaterialRadioButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class CurrencyFragment : Fragment() {
    private val viewModel: CurrencyViewModel by viewModels()

    private var currencyRadioButton =
        mutableMapOf<String, MaterialRadioButton>()

    private var _binding: FragmentCurrencyBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.currencyScreen.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MdcTheme {
                    CurrencyScreen()
                }
            }
        }
        return view
    }

    @Composable
    private fun CurrencyScreen() {
        val context = LocalContext.current

        Column {

            CurrencyDivider()

            CurrencyItem(
                onClick = {
                    viewModel.clickDb()
                }
            )
        }
    }

    @Composable
    private fun CurrencyDivider() {
        val context = LocalContext.current

        Spacer(modifier = Modifier.height(16.dp))

        Divider(
            color = Color(context.themeColor(com.fiz.mono.common.ui.resources.R.attr.colorGray)),
            startIndent = 28.dp
        )

        Spacer(modifier = Modifier.height(16.dp))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        observeViewStateUpdates()
    }

    @Composable
    private fun CurrencyItem(
        onClick: () -> Unit = {}
    ) {
        val context = LocalContext.current
        val state = viewModel.viewState.collectAsState()

        val value = state.value.currency == "đ"

        val styleTextSize16ColorPrimary = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colors.primary
        )

        Row(
            verticalAlignment = CenterVertically
        ) {

            RadioButton(
                modifier = Modifier
                    .size(width = 28.dp, height = 32.dp),
                selected = value,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(context.getColorCompat(com.fiz.mono.common.ui.resources.R.color.blue)),
                    unselectedColor = Color(context.themeColor(com.fiz.mono.common.ui.resources.R.attr.colorGray)),
                    disabledColor = MaterialTheme.colors.onSurface
                )
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = Color(context.themeColor(com.fiz.mono.common.ui.resources.R.attr.colorMain))
                    )
                    .border(
                        width = 2.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(context.themeColor(com.fiz.mono.common.ui.resources.R.attr.colorGray))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "đ",
                    style = styleTextSize16ColorPrimary
                )
            }

            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = "100.000đ",
                style = styleTextSize16ColorPrimary
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "VND",
                style = styleTextSize16ColorPrimary
            )
        }
    }

    private fun setupUI() {
        currencyRadioButton["$"] = binding.USDRadioButton
        currencyRadioButton["¥"] = binding.JPYRadioButton
        currencyRadioButton["₡"] = binding.CRCRadioButton
        currencyRadioButton["£"] = binding.GBPRadioButton
        currencyRadioButton["₼"] = binding.AZNRadioButton
        currencyRadioButton["€"] = binding.ALLRadioButton
        currencyRadioButton["лв"] = binding.BGNRadioButton

        binding.navigationBarLayout.backButton.setVisible(true)
        binding.navigationBarLayout.actionButton.setVisible(false)
        binding.navigationBarLayout.choiceImageButton.setVisible(false)
        binding.navigationBarLayout.titleTextView.text =
            getString(com.fiz.mono.common.ui.resources.R.string.currency)


    }

    private fun setupListeners() {
        currencyRadioButton.values.forEach {
            it.setOnClickListener { view ->
                if (view is RadioButton) {
                    val checked = view.isChecked

                    if (checked) {
                        val selectEntriesRadioButton =
                            currencyRadioButton.entries.find { it.value.id == view.id }

                        selectEntriesRadioButton?.let {
                            viewModel.saveCurrency(it.key)
                        }
                    }
                }
            }
        }

        binding.navigationBarLayout.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewStateUpdates() {
        launchAndRepeatWithViewLifecycle {
            viewModel.viewState.collectLatest { newState ->
                updateScreenState(newState)
            }
        }
    }

    private fun updateScreenState(newState: CurrencyViewState) {
        currencyRadioButton.values.forEach { it.isChecked = false }

        currencyRadioButton[newState.currency]?.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}